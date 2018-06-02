package pma.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.IClassifier;
import net.sf.classifier4J.bayesian.BayesianClassifier;
import net.sf.classifier4J.bayesian.IWordsDataSource;
import net.sf.classifier4J.bayesian.SimpleWordsDataSource;
import net.sf.classifier4J.bayesian.WordProbability;
import net.sf.classifier4J.bayesian.WordsDataSourceException;
import pma.layer.Storable;
import pma.layer.Trainable;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class BayesianEvaluation extends Evaluation implements Trainable, Storable {
    
    SimpleWordsDataSource wds = new SimpleWordsDataSource();
        
    BayesianClassifier classifier = new BayesianClassifier(wds);
    
    public BayesianEvaluation() {

    }
    
    private void train(String text, boolean spam) throws ClassifierException {
        if (spam) {
            classifier.teachMatch(text);
        } else {
            classifier.teachNonMatch(text);
        }
    }

    @Override
    public double[] evaluate(List<Message> messages) {
        double[] results = new double[messages.size()];
        
        for (int i = 0; i < messages.size(); i++) {
            try {
                results[i] = 1 - classifier.classify(messages.get(i).getText());
            } catch (ClassifierException ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        return results;
    }
    
    @Override
    public void train(List<Message> messages) {
        for (Message m : messages) {
            try {
                train(m.getText(), m.isSpam());
            } catch (ClassifierException ex) {
                Logger.getLogger(BayesianEvaluation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void save(String path, String name) {
        try {
            String data = "";
            Collection c = wds.getAll();
            
            int count = 0;
            
            for (Object o : c) {
                if (!(o instanceof WordProbability)) {
                    continue;
                }
                count++;
                
                WordProbability wp = (WordProbability) o;
                data += wp.getCategory() + "|"
                        + wp.getWord()+ "|"
                        + wp.getMatchingCount() + "/" + wp.getNonMatchingCount() + "|"
                        + wp.getProbability();
                
                data += "\n";
            }
            data = count + "\n" + data;
            
            File file = new File("pa-network-storage/" + name + ".bayesian.txt");
            file.getParentFile().mkdirs();
            file.createNewFile();
            
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            
            writer.write(data);
            writer.close();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void load(String path, String name) {
        FileReader reader = null;
        try {
            File file = new File("pa-network-storage/" + name + ".bayesian.txt");
            reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int count = Integer.parseInt(bufferedReader.readLine());
            
            for (int i = 0; i < count; i++) {
                String line = bufferedReader.readLine();
                String[] parts = line.split("\\|");
                String word = parts[1];
                String[] counts = parts[2].split("/");
                int matchCount = Integer.parseInt(counts[0]);
                int nonMatchCount = Integer.parseInt(counts[1]);
                WordProbability wp = new WordProbability(word, matchCount, nonMatchCount);
                wp.setCategory(parts[0]);
                wds.setWordProbability(wp);
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
