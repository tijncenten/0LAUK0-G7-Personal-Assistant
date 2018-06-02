package pma.evaluation;

import java.util.List;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class KeywordEvaluation extends Evaluation {

    @Override
    public double[] evaluate(List<Message> messages) {
        double[] results = new double[messages.size()];
        
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getText().contains("good")) {
                results[i] = 1;
            } else {
                results[i] = 0;
            }
        }
        
        return results;
    }
    
}
