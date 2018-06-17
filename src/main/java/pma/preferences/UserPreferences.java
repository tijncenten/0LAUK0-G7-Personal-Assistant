package pma.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pma.feedback.FeedbackListener;
import pma.feedback.FeedbackModule;
import pma.feedback.FeedbackModule.FeedbackType;
import pma.feedback.request.BlockRateFeedbackRequest;
import pma.feedback.request.FeedbackNumberFeedbackRequest;
import pma.feedback.request.FeedbackRequest;
import pma.feedback.request.ThreadDepthFeedbackRequest;
import pma.layer.LayerNetwork;
import pma.layer.Storable;

/**
 *
 * @author s167501
 */
public class UserPreferences implements Storable {
    
    /** Categorization preferences */
    public enum CategorizationPrefs {BLOCK, AUTO, ALLOW};
    private CategorizationPrefs questionPrefs = CategorizationPrefs.AUTO;
    private CategorizationPrefs answerPrefs = CategorizationPrefs.AUTO;
    private CategorizationPrefs announcementPrefs = CategorizationPrefs.AUTO;
    private CategorizationPrefs confirmationPrefs = CategorizationPrefs.AUTO;
    
    /** Thread preferences */
    private double threadDepthFactor = 0.75;
    
    /** Evaluation preferences */
    private double evaluationThreshold = 0.5;
    private double evaluationUncertainty = 0.0;

    public CategorizationPrefs getQuestionPrefs() {
        return questionPrefs;
    }

    public void setQuestionPrefs(CategorizationPrefs questionPrefs) {
        this.questionPrefs = questionPrefs;
    }

    public CategorizationPrefs getAnswerPrefs() {
        return answerPrefs;
    }

    public void setAnswerPrefs(CategorizationPrefs answerPrefs) {
        this.answerPrefs = answerPrefs;
    }

    public CategorizationPrefs getAnnouncementPrefs() {
        return announcementPrefs;
    }

    public void setAnnouncementPrefs(CategorizationPrefs announcementPrefs) {
        this.announcementPrefs = announcementPrefs;
    }

    public CategorizationPrefs getConfirmationPrefs() {
        return confirmationPrefs;
    }

    public void setConfirmationPrefs(CategorizationPrefs confirmationPrefs) {
        this.confirmationPrefs = confirmationPrefs;
    }

    public double getThreadDepthFactor() {
        return threadDepthFactor;
    }

    public void decreaseThreadDepthFactor() {
        this.threadDepthFactor -= 0.1;
        this.threadDepthFactor = Math.max(Math.min(this.threadDepthFactor, 1), 0);
    }
    
    public void increaseThreadDepthFactor() {
        this.threadDepthFactor += 0.1;
        this.threadDepthFactor = Math.max(Math.min(this.threadDepthFactor, 1), 0);
    }
    
    public double getEvaluationThreshold() {
        return evaluationThreshold;
    }
    
    public double getEvaluationUncertainty() {
        return evaluationUncertainty;
    }
    
    private void decreaseEvaluationThreshold() {
        this.evaluationThreshold -= 0.1;
        this.evaluationThreshold = Math.max(Math.min(this.evaluationThreshold, 1), 0);
    }
    
    private void increaseEvaluationThreshold() {
        this.evaluationThreshold += 0.1;
        this.evaluationThreshold = Math.max(Math.min(this.evaluationThreshold, 1), 0);
    }
    
    private void decreaseEvaluationUncertainty() {
        this.evaluationUncertainty /= 2;
    }
    
    private void increaseEvaluationUncertainty() {
        if (evaluationThreshold + evaluationUncertainty * 2 < 1 &&
                evaluationThreshold - evaluationUncertainty * 2 > 0) {
            this.evaluationUncertainty *= 2;
        }
    }
    
    public void build(LayerNetwork network) {
        FeedbackModule fm = network.getFeedbackModule();
        
        fm.addFeedbackListener(FeedbackType.BLOCK_RATE, new FeedbackListener() {
            @Override
            public void ApplyFeedback(FeedbackRequest request) {
                BlockRateFeedbackRequest req = (BlockRateFeedbackRequest) request;
                int result = req.getResult();
                switch (result) {
                case 1:
                    increaseEvaluationThreshold();
                    break;
                case -1:
                    decreaseEvaluationThreshold();
                    break;
                }
            }
        });
        
        fm.addFeedbackListener(FeedbackType.NR_FEEDBACK, new FeedbackListener() {
            @Override
            public void ApplyFeedback(FeedbackRequest request) {
                FeedbackNumberFeedbackRequest req = (FeedbackNumberFeedbackRequest) request;
                int result = req.getResult();
                switch (result) {
                case 1:
                    increaseEvaluationUncertainty();
                    break;
                case -1:
                    decreaseEvaluationUncertainty();
                    break;
                }
            }
        });
        
        fm.addFeedbackListener(FeedbackType.THREAD_DEPTH, new FeedbackListener() {
            @Override
            public void ApplyFeedback(FeedbackRequest request) {
                ThreadDepthFeedbackRequest req = (ThreadDepthFeedbackRequest) request;
                int result = req.getResult();
                switch (result) {
                case 1:
                    increaseThreadDepthFactor();
                    break;
                case -1:
                    decreaseThreadDepthFactor();
                    break;
                }
            }
        });
    }
    
    @Override
    public void save(String path, String name) {
        try {
            File file = new File("pa-network-storage/" + name + ".prefs.txt");
            file.getParentFile().mkdirs();
            file.createNewFile();
            
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            
            writer.write("questionCategorizationPrefs:" + questionPrefs.name() + "\n");
            writer.write("answerCategorizationPrefs:" + answerPrefs.name() + "\n");
            writer.write("announcementCategorizationPrefs:" + announcementPrefs.name() + "\n");
            writer.write("confirmationCategorizationPrefs:" + confirmationPrefs.name() + "\n");
            writer.write("threadDepthFactor:" + threadDepthFactor + "\n");
            writer.write("evaluationThreshold:" + evaluationThreshold + "\n");
            writer.write("evaluationUncertainty:" + evaluationUncertainty);
            
            writer.close();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void load(String path, String name) {
        FileReader reader = null;
        try {
            File file = new File("pa-network-storage/" + name + ".prefs.txt");
            reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            
            this.load(bufferedReader);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void load(InputStream is) {
        try {
            this.load(new BufferedReader(new InputStreamReader(is, "UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void load(BufferedReader bufferedReader) throws IOException {
        String line;
            
        while((line = bufferedReader.readLine()) != null) {
            String[] lineSplit = line.split(":");
            switch (lineSplit[0]) {
            case "questionCategorizationPrefs":
                this.questionPrefs = CategorizationPrefs.valueOf(lineSplit[1]);
                break;
            case "answerCategorizationPrefs":
                this.answerPrefs = CategorizationPrefs.valueOf(lineSplit[1]);
                break;
            case "announcementCategorizationPrefs":
                this.announcementPrefs = CategorizationPrefs.valueOf(lineSplit[1]);
                break;
            case "confirmationCategorizationPrefs":
                this.confirmationPrefs = CategorizationPrefs.valueOf(lineSplit[1]);
                break;
            case "threadDepthFactor":
                this.threadDepthFactor = Double.parseDouble(lineSplit[1]);
                break;
            case "evaluationThreshold":
                this.evaluationThreshold = Double.parseDouble(lineSplit[1]);
                break;
            case "evaluationUncertainty":
                this.evaluationUncertainty = Double.parseDouble(lineSplit[1]);
            }
        }
    }
}
