package pma.evaluation.function;

import pma.PersonalMessagingAssistant.EvalResult;

/**
 *
 * @author s167501
 */
public abstract class EvaluationFunction {
    
    public abstract EvalResult[] calculate(double[][] values);
    
}
