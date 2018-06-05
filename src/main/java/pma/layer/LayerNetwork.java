package pma.layer;

import java.util.LinkedList;
import java.util.List;
import pma.feedback.FeedbackModule;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class LayerNetwork implements Trainable, Storable {
    
    LinkedList<Layer> layers = new LinkedList<>();
    private UserPreferences prefs;
    private FeedbackModule feedbackModule;
    
    public void addLayer(Layer layer) {
        if (!layers.isEmpty()) {
            layers.getLast().setChildLayer(layer);
        }
        layers.add(layer);
    }
    
    public void process(List<Message> messages) {
        layers.getFirst().process(messages, this);
    }
    
    public OutputLayer getOutputLayer() {
        for (Layer l : layers) {
            if (l instanceof OutputLayer) {
                return (OutputLayer) l;
            }
        }
        throw new IllegalStateException("No outputlayer present");
    }
    
    public UserPreferences getPrefs() {
        return this.prefs;
    }
    
    public void setPrefs(UserPreferences prefs) {
        this.prefs = prefs;
    }
    
    public FeedbackModule getFeedbackModule() {
        return this.feedbackModule;
    }
    
    public void setFeedbackModule(FeedbackModule feedbackModule) {
        this.feedbackModule = feedbackModule;
    }

    @Override
    public void train(List<Message> messages) {
        for (Layer l : layers) {
            if (l instanceof Trainable) {
                Trainable t = (Trainable) l;
                t.train(messages);
            }
        }
    }
    
    @Override
    public void save(String path, String name) {
        for (Layer l : layers) {
            if (l instanceof Storable) {
                Storable s = (Storable) l;
                s.save(path, name);
            }
        }
    }

    @Override
    public void load(String path, String name) {
        for (Layer l : layers) {
            if (l instanceof Storable) {
                Storable s = (Storable) l;
                s.load(path, name);
            }
        }
    }
    
}
