package pma.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pma.layer.Layer;
import pma.PersonalMessagingAssistant.EvalResult;
import pma.layer.LayerNetwork;
import pma.message.Message;
import pma.preferences.UserPreferences;
import pma.preferences.UserPreferences.CategorizationPrefs;

/**
 *
 * @author s167501
 */
public class CategorizationFilter extends Filter {
    
    public enum Categories {QUESTION, ANSWER, ANNOUNCEMENT, CONFIRMATION};
    
    private String[] questionWords = {"who", "what", "where", "when", "is", "are", "have", "shall"};
    private int questionWordsPoints = 2;
    private String[] questionWordsWeak = {"whom", "whose", "do", "did"};
    private int questtionWordsWeakPoints = 1;
    private String[] questionMiddleWords = {"know", "any", "anyone"};

    public CategorizationFilter(Layer childLayer, Layer alternativeLayer) {
        super(childLayer, alternativeLayer);
    }
    
    public CategorizationFilter(Layer alternativeLayer) {
        super(alternativeLayer);
    }

    @Override
    protected boolean applyFilter(Message m, LayerNetwork network) {
        int nrOfChars = m.getText().length();
        int nrOfWords = m.getText().split(" ").length;
        
        int questionScore = 0;
        if (m.getText().length() > 0 && m.getText().charAt(m.getText().length() - 1) == '?') {
            questionScore++;
        }
        
        Pattern p = Pattern.compile("(?:^|(?:[.!?]\\s))(\\w+)");
        Matcher mtchr = p.matcher(m.getText());
        List<String> firstWords = new ArrayList<>();
        while (mtchr.find()) {
            firstWords.add(mtchr.group(1));
        }
        
        for (String qWord : questionWords) {
            for (String w : firstWords) {
                if (w.toLowerCase().equals(qWord)) {
                    questionScore += questionWordsPoints;
                    break;
                }
            }
        }

        if (questionScore > 2) {
            CategorizationPrefs questionPrefs = network.getPrefs().getQuestionPrefs();
            if (null != questionPrefs) switch (questionPrefs) {
            case AUTO:
                return false;
            case ALLOW:
                m.setResult(EvalResult.high);
                break;
            case BLOCK:
                m.setResult(EvalResult.low);
                break;
            default:
                break;
            }
            return true;
        }
        
        boolean answer = false;
        boolean announcement = false;
        boolean confirmation = false;
        
        return false;
    }
    
}
