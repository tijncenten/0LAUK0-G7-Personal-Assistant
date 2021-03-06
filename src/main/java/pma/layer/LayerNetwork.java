package pma.layer;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private LinkedList<Layer> trainLayers = new LinkedList<>();
    
    private boolean isBuilt = false;
    
    public void addLayer(Layer layer) {
        if (isBuilt) {
            throw new IllegalStateException("Layer network is already built");
        }
        if (!layers.isEmpty()) {
            layers.getLast().setChildLayer(layer);
        }
        layers.add(layer);
        
        if (layer instanceof PreprocessingLayer || layer instanceof NormalizationLayer) {
            Class<? extends Layer> preClass = layer.getClass();
            try {
                addTrainLayer(preClass.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void addTrainLayer(Layer layer) {
        if (isBuilt) {
            throw new IllegalStateException("Layer network is already built");
        }
        if (!trainLayers.isEmpty()) {
            trainLayers.getLast().setChildLayer(layer);
        }
        trainLayers.add(layer);
    }
    
    public void process(List<Message> messages) {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        layers.getFirst().process(messages, this);
    }
    
    public OutputLayer getOutputLayer() {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        for (Layer l : layers) {
            if (l instanceof OutputLayer) {
                return (OutputLayer) l;
            }
        }
        throw new IllegalStateException("No outputlayer present");
    }
    
    private OutputLayer getTrainOutputLayer() {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        for (Layer l : trainLayers) {
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
        if (isBuilt) {
            throw new IllegalStateException("Layer network is already built");
        }
        this.prefs = prefs;
    }
    
    public FeedbackModule getFeedbackModule() {
        return this.feedbackModule;
    }
    
    public void setFeedbackModule(FeedbackModule feedbackModule) {
        if (isBuilt) {
            throw new IllegalStateException("Layer network is already built");
        }
        this.feedbackModule = feedbackModule;
    }
    
    public void build() {
        if (isBuilt) {
            throw new IllegalStateException("Layer network is already built");
        }
        
        addTrainLayer(new OutputLayer());
        
        for (Layer l : layers) {
            l.build(this);
        }
        prefs.build(this);
        
        isBuilt = true;
    }

    @Override
    public void train(List<Message> messages) {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        
        // Preprocess the messages for training
        trainLayers.getFirst().process(messages, this);
        messages = getTrainOutputLayer().getOutput();
        
        for (Layer l : layers) {
            if (l instanceof Trainable) {
                Trainable t = (Trainable) l;
                t.train(messages);
            }
        }
    }
    
    @Override
    public void save(String path, String name) {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        
        for (Layer l : layers) {
            if (l instanceof Storable) {
                Storable s = (Storable) l;
                s.save(path, name);
            }
        }
    }

    @Override
    public void load(String path, String name) {
        if (!isBuilt) {
            throw new IllegalStateException("Layer network is not built yet");
        }
        
        for (Layer l : layers) {
            if (l instanceof Storable) {
                Storable s = (Storable) l;
                s.load(path, name);
            }
        }
    }
    
}
