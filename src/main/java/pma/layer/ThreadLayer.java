package pma.layer;

import java.util.ArrayList;
import java.util.List;
import pma.clustering.ClusteringAlgorithm;
import pma.clustering.HierarchicalClustering;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class ThreadLayer extends Layer {

    public ThreadLayer(Layer childLayer) {
        super(childLayer);
    }
    
    public ThreadLayer() {
        super();
    }

    @Override
    protected void performTask(List<Message> messages, LayerNetwork network) {
        ClusteringAlgorithm clusteringAlgorithm = new HierarchicalClustering(
                (int) (messages.size() * network.getPrefs().getThreadDepthFactor()));
        clusteringAlgorithm.cluster(messages);
    }
    
    public static List<List<Message>> getThreads(List<Message> messages) {
        List<List<Message>> threads = new ArrayList<>();
        int numberOfThreads = getNumberOfThreads(messages);
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new ArrayList<>());
        }
        
        for (Message m : messages) {
            int threadIndex = m.getThreadIndex();
            threads.get(threadIndex).add(m);
        }
        
        return threads;
    }
    
    public static int getNumberOfThreads(List<Message> messages) {
        int maxIndex = 0;
        for (Message m : messages) {
            maxIndex = Math.max(m.getThreadIndex(), maxIndex);
        }
        return maxIndex + 1;
    }
    
}
