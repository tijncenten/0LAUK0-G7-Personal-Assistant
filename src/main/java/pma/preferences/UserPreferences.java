package pma.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import pma.layer.Storable;

/**
 *
 * @author s167501
 */
public class UserPreferences implements Storable {
    
    /** Categorization preferences */
    public enum CategorizationPrefs {BLOCK, AUTO, ALLOW};
    private CategorizationPrefs questionPrefs = CategorizationPrefs.BLOCK;
    private CategorizationPrefs answerPrefs = CategorizationPrefs.AUTO;
    private CategorizationPrefs announcementPrefs = CategorizationPrefs.AUTO;
    private CategorizationPrefs confirmationPrefs = CategorizationPrefs.AUTO;
    
    /** Thread preferences */
    private double threadDepthFactor = 0.75;

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

    public void decreaseThreadDepthFactor(double threadDepthFactor) {
        this.threadDepthFactor -= 0.1;
        this.threadDepthFactor = Math.max(Math.min(this.threadDepthFactor, 1), 0);
    }
    
    public void increaseThreadDepthFactor() {
        this.threadDepthFactor += 0.1;
        this.threadDepthFactor = Math.max(Math.min(this.threadDepthFactor, 1), 0);
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
            writer.write("threadDepthFactor:" + threadDepthFactor);
            
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
                }
            }
            
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
}
