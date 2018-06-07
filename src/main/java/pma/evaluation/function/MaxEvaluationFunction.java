package pma.evaluation.function;


/**
 *
 * @author s167501
 */
public class MaxEvaluationFunction extends EvaluationFunction {

    @Override
    public double[] calculate(double[][] values) {
        int nrMessages = values[0].length;
        double[] results = new double[nrMessages];
        
        for (int i = 0; i < nrMessages; i++) {
            double maxValue = 0;
            for (int j = 0; j < values.length; j++) {
                double value = values[j][i];
                maxValue = Math.max(maxValue, value);
            }
            results[i] = maxValue;
        }
        
        return results;
    }
    
}
