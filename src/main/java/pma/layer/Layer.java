package pma.layer;

import java.util.List;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 * 
 * A layer is an abstract class that performs some kind of process on incoming
 * messages. After the process is done, the messages are sent to the next layer
 */
public abstract class Layer {
    
    protected Layer childLayer;
    
    public Layer(Layer childLayer) {
        this.childLayer = childLayer;
    }
    
    public Layer() {
        
    }
    
    public void setChildLayer(Layer layer) {
        this.childLayer = layer;
    }
    
    public void process(List<Message> messages, UserPreferences prefs) {
        performTask(messages, prefs);
        if (childLayer != null) {
            childLayer.process(messages, prefs);
        }
    }
    
    protected abstract void performTask(List<Message> messages, UserPreferences prefs);
    
}
