package pma.evaluation.function;


/**
 *
 * @author s167501
 */
public class MinEvaluationFunction extends EvaluationFunction {

    @Override
    public double[] calculate(double[][] values) {
        int nrMessages = values[0].length;
        double[] results = new double[nrMessages];
        
        for (int i = 0; i < nrMessages; i++) {
            double minValue = 1;
            for (int j = 0; j < values.length; j++) {
                double value = values[j][i];
                minValue = Math.min(minValue, value);
            }
            results[i] = minValue;
        }
        
        return results;
    }
    
}
