package pma.layer;

import java.util.ArrayList;
import java.util.List;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class OutputLayer extends Layer {
    
    List<Message> messages = new ArrayList<>();

    public OutputLayer() {
        super(null);
    }

    @Override
    protected void performTask(List<Message> messages, LayerNetwork network) {
        this.messages.addAll(messages);
    }
    
    public List<Message> getOutput() {
        messages.sort((o1, o2) -> {
            //return (int) (o1.getTimestamp() - o2.getTimestamp()); //To change body of generated lambdas, choose Tools | Templates.
            if(o1.getTimestamp() > o2.getTimestamp()){
                return 1;
            } else if (o1.getTimestamp() < o2.getTimestamp()) {
                return -1;
            }
            return 0;
        });
        List<Message> stored = messages;
        messages = new ArrayList<>();
        return stored;
    }
    
}
