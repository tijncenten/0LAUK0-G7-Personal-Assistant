package pma.layer;

import java.util.List;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public interface Trainable {
    public void train(List<Message> messages);
}
