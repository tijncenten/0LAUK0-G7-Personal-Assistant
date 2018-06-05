package pma.filter;

import java.util.ArrayList;
import java.util.List;
import pma.layer.Layer;
import pma.layer.LayerNetwork;
import pma.layer.OutputLayer;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 * 
 * A filter is an abstract class extended from Layer,
 * that expects a list of messages as input,
 * with this input the filter will do its work.
 * 
 * If the filter is activated for a message, the message will be propagated
 * to the alternative layer; otherwise the message will be sent to the default
 * child layer.
 */
public abstract class Filter extends Layer {
    
    protected Layer alternativeLayer;
    
    Filter(Layer childLayer, Layer alternativeLayer) {
        super(childLayer);
        this.alternativeLayer = alternativeLayer;
    }
    
    Filter(Layer alternativeLayer) {
        super();
        this.alternativeLayer = alternativeLayer;
    }
    
    @Override
    protected void performTask(List<Message> messages, LayerNetwork network) {
        List<Message> filteredMessages = new ArrayList<>();
        
        for (Message m : messages) {
            if (applyFilter(m, network)) {
                filteredMessages.add(m);
                
                if (alternativeLayer instanceof OutputLayer) {
                    if (!m.hasResult()) {
                        throw new IllegalStateException("Message should have a result before output layer");
                    }
                }
            }
        }
        
        for (Message m : filteredMessages) {
            messages.remove(m);
        }
        
        if (alternativeLayer != null) {
            alternativeLayer.process(filteredMessages, network);
        }
    }
    
    /**
     * Applies a filter to each incoming message. If the filter is activated,
     * the message will be propagated to the alternative layer; otherwise
     * the message will be sent to the default child layer.
     * 
     * @param m The message that is to be filtered
     * @return A boolean indicating if the filter activated
     */
    protected abstract boolean applyFilter(Message m, LayerNetwork network);
    
}
