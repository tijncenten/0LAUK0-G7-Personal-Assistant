package pma;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import pma.chatparsers.MessageParser;
import pma.evaluation.BayesianEvaluation;
import pma.layer.OutputLayer;
import pma.evaluation.function.EvaluationFunction;
import pma.evaluation.EvaluationLayer;
import pma.evaluation.function.AverageEvaluationFunction;
import pma.feedback.FeedbackEvaluator;
import pma.feedback.FeedbackModule;
import pma.filter.CategorizationFilter;
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
    
    private final MessageParser parser;
    private final int batchSize;
    
    private List<Message> lastOutputMessages;
    private long lastProcessingTime = 0;

    public PersonalMessagingAssistant(MessageParser parser, int batchSize) {
        constructNetwork();
        this.parser = parser;
        this.batchSize = batchSize;
    }
    
    private void constructNetwork() {
        OutputLayer output = new OutputLayer();
        
        network = new LayerNetwork();
        network.setPrefs(prefs);
        network.setFeedbackModule(feedbackModule);
        
        network.addLayer(new PreprocessingLayer());
        network.addLayer(new ThreadLayer());
        network.addLayer(new CategorizationFilter(output));
        network.addLayer(new NormalizationLayer());
        
        EvaluationFunction evalFunc = new AverageEvaluationFunction();
        EvaluationLayer evalLayer = new EvaluationLayer(evalFunc);
        BayesianEvaluation evaluation = new BayesianEvaluation();
        evalLayer.addEvaluation(evaluation);
        
        network.addLayer(evalLayer);
        network.addLayer(output);
        
        network.build();
    }
    
    public void setFeedbackEvaluator(FeedbackEvaluator evaluator){
        feedbackModule.setFeedbackEvaluator(evaluator);
    }
    protected EvalResult[] process(List<Message> messages) {
        
        int numberOfMessages = messages.size();

        int iterations = (int) Math.ceil(numberOfMessages * 1. / batchSize);
        
        long startTime = System.nanoTime();
        
        List<Message> outputMessages = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            int start = i * batchSize;
            int end = (i+1) * batchSize;
            if (end > messages.size()) {
                end = messages.size();
            }
            network.process(new ArrayList<>(messages.subList(start, end)));
            
            outputMessages.addAll(network.getOutputLayer().getOutput());
        }
        
        long endTime = System.nanoTime();
        lastProcessingTime = endTime - startTime;

        for (Message m : outputMessages) {
            System.out.println(m);
        }
        
        lastOutputMessages = outputMessages;
        
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
    
    public List<Message> getLastOutputMessages() {
        return this.lastOutputMessages;
    }
    
    public long getLastProcessingTime() {
        return this.lastProcessingTime;
    }
    
    public EvalResult[] process(File f) throws ParseException, FileNotFoundException {
        return this.process(parser.parse(f));
    }
    
    public static int[] calculateStatistics(List<Message> messages) {
        int truePositives = 0;
        int trueNegatives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;
        int count = 0;
        
        for (Message m : messages) {
            if (m.isSpam() == null) {
                continue;
            }
            if (m.isSpam()) {
                if (m.getResult() == EvalResult.low) {
                    trueNegatives++;
                } else {
                    falsePositives++;
                }
            } else {
                if (m.getResult() == EvalResult.low) {
                    falseNegatives++;
                } else {
                    truePositives++;
                }
            }
            count++;
        }
        return new int[] {truePositives, trueNegatives, falsePositives, falseNegatives, count};
    }
    
    public static void calculateAccuracy(List<Message> messages) {
        int[] stats = calculateStatistics(messages);
        
        int truePositives = stats[0];
        int trueNegatives = stats[1];
        int falsePositives = stats[2];
        int falseNegatives = stats[3];
        int count = stats[4];
        
        System.out.println("\n+------------------------+");
        System.out.println("| TP: " + truePositives + "\t\tFP: " + falsePositives);
        System.out.println("| FN: " + falseNegatives + "\t\tTN: " + trueNegatives);
        System.out.println("+------------------------+");
        System.out.println("| Total: " + count);
        System.out.println("+------------------------+");
        System.out.println("| Precision: " + truePositives * 1. / (truePositives + falsePositives));
        System.out.println("| Recall/sensitivity: " + truePositives * 1. / (truePositives + falseNegatives));
        System.out.println("| Specificity: " + trueNegatives * 1. / (falsePositives + trueNegatives));
        System.out.println("| Accuracy: " + (truePositives + trueNegatives) * 1. / count);
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
    
    public void train(File f) throws ParseException, FileNotFoundException {
        this.train(parser.parse(f));
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
