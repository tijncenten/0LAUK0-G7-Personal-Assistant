package pma.clustering;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pma.contact.Contact;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class HierarchicalClustering extends ClusteringAlgorithm {
    
    private int depth = 0;
    
    public HierarchicalClustering(int depth) {
        this.depth = depth;
    }

    @Override
    public void cluster(List<Message> messages) {
        Set<Cluster> clusters = new HashSet<>();
        
        for (Message m : messages) {
            clusters.add(new Cluster(m));
        }
        
        for (int i = 0; i < depth; i++) {
            findAndCombineClusters(clusters);
        }
        
        int index = 0;
        for (Cluster c : clusters) {
            int messageIndex = -1;
            if (c.getMessages().size() > 1) {
                messageIndex = index;
            }
            
            for (Message m : c.getMessages()) {
                m.setThreadIndex(messageIndex);
            }
            index++;
        }
    }
    
    private void findAndCombineClusters(Set<Cluster> clusters) {
        Cluster c1 = null;
        Cluster c2 = null;
        double smallestDistance = Double.MAX_VALUE;
        for (Cluster ci : clusters) {
            for (Cluster cj : clusters) {
                if (ci == cj) {
                    continue;
                }
                
                double distance = ci.distance(cj);
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    c1 = ci;
                    c2 = cj;
                }
            }
        }
        
        if (c1 == null || c2 == null) {
            throw new IllegalStateException("c1 or c2 is null");
        }
        
        clusters.remove(c2);
        c1.CombineCluster(c2);
    }

    
    private class Cluster {
        
        private Set<Message> messages = new HashSet<>();
        
        Cluster(Message m) {
            messages.add(m);
        }
        
        public void CombineCluster(Cluster other) {
            this.messages.addAll(other.getMessages());
        }
        
        public Set<Message> getMessages() {
            return this.messages;
        }
        
        public double distance(Cluster other) {
            double sum = 0;
            sum += Math.pow(this.getTimeMean() - other.getTimeMean(), 2);
            sum += Math.pow(5 * senderDistance(other), 2); // TODO: Choose a good value
            
            return Math.sqrt(sum);
        }
        
        public long getTimeMean() {
            long sum = 0;
            for (Message m : messages) {
                sum += m.getTimestamp();
            }
            return sum / messages.size();
        }
        
        public double senderDistance(Cluster other) {
            Set<Contact> contacts = new HashSet<>();
            Set<Contact> otherContacts = new HashSet<>();
            for (Message m : messages) {
                contacts.add(m.getSender());
            }
            for (Message m : other.getMessages()) {
                otherContacts.add(m.getSender());
            }
            Set<Contact> contactsCopy = new HashSet<>(contacts);
            contacts.removeAll(otherContacts);
            otherContacts.removeAll(contactsCopy);
            int diffNumber = contacts.size() + otherContacts.size();
            int totalNumber = messages.size() + other.getMessages().size();
            
            if (diffNumber > totalNumber) {
                throw new IllegalStateException("Difference is greater than total");
            }
            
            return diffNumber * 1.0 / totalNumber;
        }
        
    }
    
}
