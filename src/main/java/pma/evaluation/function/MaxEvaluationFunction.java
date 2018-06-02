package pma.evaluation.function;

import pma.PersonalMessagingAssistant.EvalResult;

/**
 *
 * @author s167501
 */
public class MaxEvaluationFunction extends EvaluationFunction {

    @Override
    public EvalResult[] calculate(double[][] values) {
        int nrMessages = values[0].length;
        EvalResult[] results = new EvalResult[nrMessages];
        
        for (int i = 0; i < nrMessages; i++) {
            double maxValue = 0;
            for (int j = 0; j < values.length; j++) {
                double value = values[j][i];
                maxValue = Math.max(maxValue, value);
            }
            
            if (maxValue > 0.5) {
                results[i] = EvalResult.high;
            } else {
                results[i] = EvalResult.low;
            }
        }
        
        return results;
    }
    
}
