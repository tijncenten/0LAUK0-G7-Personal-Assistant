package pma;

import java.util.List;
import pma.evaluation.BayesianEvaluation;
import pma.evaluation.Evaluation;
import pma.layer.OutputLayer;
import pma.evaluation.function.EvaluationFunction;
import pma.evaluation.EvaluationLayer;
import pma.evaluation.KeywordEvaluation;
import pma.evaluation.function.MaxEvaluationFunction;
import pma.feedback.FeedbackModule;
import pma.filter.CategorizationFilter;
import pma.filter.Filter;
import pma.filter.SpamFilter;
import pma.layer.Layer;
import pma.layer.LayerNetwork;
import pma.layer.NormalizationLayer;
import pma.layer.PreprocessingLayer;
import pma.layer.Storable;
import pma.layer.ThreadLayer;
import pma.layer.Trainable;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class PersonalMessagingAssistant implements Trainable, Storable {
    
    public enum EvalResult {test, low, medium, high};
    
    private LayerNetwork network;
    private UserPreferences prefs = new UserPreferences();
    private FeedbackModule feedbackModule = new FeedbackModule();

    public PersonalMessagingAssistant() {
        constructNetwork();
    }
    
    private void constructNetwork() {
        OutputLayer output = new OutputLayer();
        
        network = new LayerNetwork();
        network.setPrefs(prefs);
        network.setFeedbackModule(feedbackModule);
        
        network.addLayer(new PreprocessingLayer());
        //network.addLayer(new SpamFilter(output));
        network.addLayer(new ThreadLayer());
        network.addLayer(new CategorizationFilter(output));
        network.addLayer(new NormalizationLayer());
        
        EvaluationFunction evalFunc = new MaxEvaluationFunction();
        EvaluationLayer evalLayer = new EvaluationLayer(evalFunc);
        BayesianEvaluation evaluation = new BayesianEvaluation();
        evalLayer.addEvaluation(evaluation);
        
        network.addLayer(evalLayer);
        network.addLayer(output);
    }
    
    protected EvalResult[] process(List<Message> messages) {
        
        int numberOfMessages = messages.size();

        network.process(messages);
        
        //System.out.println(evaluation.save("bayesian"));
        
        List<Message> outputMessages = network.getOutputLayer().getOutput();
        for (Message m : outputMessages) {
            System.out.println(m);
        }
        
        if (numberOfMessages != outputMessages.size()) {
            throw new IllegalStateException("Number of input messages (" + messages.size() + ") is not equal to number of output messages (" + outputMessages.size() + ")");
        }
        
        EvalResult[] results = new EvalResult[numberOfMessages];
        
        for (int i = 0; i < outputMessages.size(); i++) {
            Message m = outputMessages.get(i);
            if (!m.hasResult()) {
                throw new IllegalStateException("Message (" + m + ") does not have a result");
            }
            results[i] = m.getResult();
        }
        
        calculateAccuracy(outputMessages);
        
        return results;
    }
    
    public static void calculateAccuracy(List<Message> messages) {
        int truePositives = 0;
        int trueNegatives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;
        
        for (Message m : messages) {
            if (m.isSpam()) {
                if (m.getResult() == EvalResult.low) {
                    trueNegatives++;
                } else {
                    falsePositives++;
                }
            } else {
                if (m.getResult() == EvalResult.high) {
                    truePositives++;
                } else {
                    falseNegatives++;
                }
            }
        }
        
        System.out.println("\n+------------------------+");
        System.out.println("| TP: " + truePositives + "\t\tFP: " + falsePositives);
        System.out.println("| FN: " + falseNegatives + "\t\tTN: " + trueNegatives);
        System.out.println("+------------------------+");
        System.out.println("| Total: " + messages.size());
        System.out.println("+------------------------+");
        System.out.println("| Precision: " + truePositives * 1. / (truePositives + falsePositives));
        System.out.println("| Recall/sensitivity: " + truePositives * 1. / (truePositives + falseNegatives));
        System.out.println("| Specificity: " + trueNegatives * 1. / (falsePositives + trueNegatives));
        System.out.println("| Accuracy: " + (truePositives + trueNegatives) * 1. / messages.size());
        System.out.println("+------------------------+");
    }
    
    @Override
    public void train(List<Message> messages) {
        for (Message m : messages) {
            if (m.isSpam() == null) {
                throw new IllegalArgumentException("A message does not contain"
                        + " spam classification and can not be trained -> " + m);
            }
        }
        
        if (network == null) {
            throw new IllegalStateException("LayerNetwork 'network' is null");
        }
        
        network.train(messages);
    }
    
    @Override
    public void save(String path, String name) {
        if (network == null) {
            throw new IllegalStateException("LayerNetwork 'network' is null");
        }
        network.save(path, name);
        prefs.save(path, name);
    }
    
    @Override
    public void load(String path, String name) {
        if (network == null) {
            throw new IllegalStateException("LayerNetwork 'network' is null");
        }
        network.load(path, name);
        prefs.load(path, name);
    }
}
