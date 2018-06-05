package pma.layer;

import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pma.message.Message;
import pma.preferences.UserPreferences;
import pma.utils.EnglishNumberToWords;
import pma.utils.StringReplacer;

/**
 *
 * @author s167501
 * 
 * Layer for simplifying the textual data of the messages
 */
public class NormalizationLayer extends Layer {

    @Override
    protected void performTask(List<Message> messages, LayerNetwork network) {
        for (Message m : messages) {
            preprocess(m);
        }
    }
    
    private void preprocess(Message m) {
        String text = m.getText();
        
        // Remove words without any context
        text = removeMeaninglessWords(text);

        // Remove punctuation
        text = removePunctuation(text);
        
        // Replace/remove numbers
        text = convertNumbersToWords(text);
        
        // Perform stemming (walking -> walk) done
        text = verbToBase(text);
        
        // Perform lemmatization (better -> good)
        
        // Replace abbreviations with full text
        
        
        m.setText(text);
    }
    
    private String removeMeaninglessWords(String text) {
        String[] words = text.split(" ");
        
        String[] removeWords = {"the", "a", "an", "to"};
        
        String reconstruct = "";
        for (String word : words) {
            boolean preserveWord = true;
            for (String remove : removeWords) {
                if (word.equals(remove)) {
                    preserveWord = false;
                    break;
                }
            }
            
            if (preserveWord) {
                if (reconstruct.length() != 0) {
                    reconstruct += " " + word;
                } else {
                    reconstruct += word;
                }
            }
        }
        
        return reconstruct;
    }
    
    private String verbToBase(String text) {
        System.setProperty("wordnet.database.dir", "dict");
        Morphology id = Morphology.getInstance();

        String[] words = text.split(" ");
        String reconstruct = "";
        for(String word : words){
            String[] arr = id.getBaseFormCandidates(word, SynsetType.VERB);
            if(arr.length > 0) {
                if (reconstruct.length() != 0) {
                    reconstruct += " " + arr[arr.length-1];
                } else {
                    reconstruct += word;
                }
            } else {
                if (reconstruct.length() != 0) {
                    reconstruct += " " + word;
                } else {
                    reconstruct += word;
                }
            }
        }  
        return reconstruct;
    }
    
    private String removePunctuation(String text) {
        return text.replaceAll("[^\\dA-Za-z ]", "");
    }
    
    private String convertNumbersToWords(String text){
        text = StringReplacer.replace(text, Pattern.compile("\\d+"), (Matcher m) -> {
            return EnglishNumberToWords.convert(Integer.parseInt(m.group()));
        });
        return text;
    }
    
}
