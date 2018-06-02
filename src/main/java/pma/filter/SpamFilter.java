package pma.filter;

import pma.layer.Layer;
import pma.PersonalMessagingAssistant.EvalResult;
import pma.message.Message;
import pma.preferences.UserPreferences;


/**
 *
 * @author s167501
 */
public class SpamFilter extends Filter {

    public SpamFilter(Layer childLayer, Layer alternativeLayer) {
        super(childLayer, alternativeLayer);
    }
    
    public SpamFilter(Layer alternativeLayer) {
        super(alternativeLayer);
    }

    @Override
    protected boolean applyFilter(Message m, UserPreferences prefs) {
        // Dummy code filtering out all messages containing the word 'spam'
        boolean result = m.getText().contains("spam");
        
        if (result) {
            m.setResult(EvalResult.low);
        }
        
        return result;
    }

    
}
