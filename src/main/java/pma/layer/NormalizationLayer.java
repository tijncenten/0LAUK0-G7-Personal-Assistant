package pma.layer;

import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
    
    public NormalizationLayer(Layer childLayer) {
        super(childLayer);
    }
    
    public NormalizationLayer() {
        super();
    }

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
        text = replaceAbbrivations(text);
        
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
            if (m.group().length() > 9) {
                // Do not convert to prevent exceptions and large text numbers
                return m.group();
            }
            return EnglishNumberToWords.convert(Integer.parseInt(m.group()));
        });
        return text;
    }
    
    private String replaceAbbrivations(String text) {
        String[] words = text.split(" ");
        String reconstruct = "";
        try {
            String excelFile = "input/abbreviations.xlsx";
            Workbook workbook = WorkbookFactory.create(new File(excelFile));
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            
            for (String word: words) {
                Boolean isChanged = false;
                //System.out.println(word);
                for (Row row: sheet) {
                    String abbreviation = dataFormatter.formatCellValue(row.getCell(0));
                    Cell neighbourCell =  row.getCell(1);
                    //System.out.println(abbreviation);
                    if(word.equals(abbreviation)) {
                        
                        if (reconstruct.length() != 0) {
                            reconstruct += " " + dataFormatter.formatCellValue(neighbourCell);
                        } else {
                            reconstruct += dataFormatter.formatCellValue(neighbourCell);
                        }
                        isChanged = true;
                    }
                }
                if(!isChanged) {
                    if (reconstruct.length() != 0) {
                        reconstruct += " " + word;
                    } else {
                        reconstruct += word;
                    }
                }
            }


            
            return reconstruct;
        } catch (IOException ex) {
            Logger.getLogger(NormalizationLayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(NormalizationLayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(NormalizationLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "there is an error";
    }
    
}
