package pma.clustering;

import java.util.List;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public abstract class ClusteringAlgorithm {
    
    public abstract void cluster(List<Message> messages);

}
