package pma.evaluation.function;

/**
 *
 * @author s167501
 */
public class AverageEvaluationFunction extends EvaluationFunction {

    @Override
    public double[] calculate(double[][] values) {
        int nrMessages = values[0].length;
        double[] results = new double[nrMessages];
        
        for (int i = 0; i < nrMessages; i++) {
            double total = 0;
            for (int j = 0; j < values.length; j++) {
                total += values[j][i];
            }
            // Store the average in results
            results[i] = total / values.length;
        }
        
        return results;
    }
    
}
