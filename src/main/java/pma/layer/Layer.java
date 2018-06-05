package pma.layer;

import java.util.List;
import pma.message.Message;

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
    
    public void process(List<Message> messages, LayerNetwork network) {
        performTask(messages, network);
        if (childLayer != null) {
            childLayer.process(messages, network);
        }
    }
    
    protected abstract void performTask(List<Message> messages, LayerNetwork network);
    
}
