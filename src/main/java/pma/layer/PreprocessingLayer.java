package pma.layer;

import java.util.List;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class PreprocessingLayer extends Layer {

    public PreprocessingLayer(Layer childLayer) {
        super(childLayer);
    }
    
    public PreprocessingLayer() {
        super();
    }

    @Override
    protected void performTask(List<Message> messages, UserPreferences prefs) {
        for (Message m : messages) {
            preprocess(m);
        }
    }
    
    private void preprocess(Message m) {
        String text = m.getText();
        
        // Make text lower case and remove excess white spaces
        text = text.toLowerCase().trim();
        
        // Replace emoticons with textual expression
        
        
        m.setText(text);
    }
    
}
