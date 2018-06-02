package pma.evaluation;

import java.util.List;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public abstract class Evaluation {
    
    public abstract double[] evaluate(List<Message> messages);
    
}
