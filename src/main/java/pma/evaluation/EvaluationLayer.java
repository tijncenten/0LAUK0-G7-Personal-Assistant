package pma.evaluation;

import pma.evaluation.function.EvaluationFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pma.layer.Layer;
import pma.PersonalMessagingAssistant.EvalResult;
import pma.feedback.FeedbackListener;
import pma.feedback.FeedbackModule;
import pma.feedback.FeedbackModule.FeedbackType;
import pma.feedback.request.FeedbackRequest;
import pma.feedback.request.MessageFeedbackRequest;
import pma.layer.LayerNetwork;
import pma.layer.Storable;
import pma.layer.Trainable;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class EvaluationLayer extends Layer implements Trainable, Storable {
    
    protected final EvaluationFunction evalFunc;
    private ArrayList<Evaluation> evaluations = new ArrayList<>();

    public EvaluationLayer(Layer childLayer, EvaluationFunction evalFunc) {
        super(childLayer);
        this.evalFunc = evalFunc;
    }
    
    public EvaluationLayer(EvaluationFunction evalFunc) {
        super();
        this.evalFunc = evalFunc;
    }
    
    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
    }

    @Override
    protected void performTask(List<Message> messages, LayerNetwork network) {
        double[][] results = new double[evaluations.size()][messages.size()];
        for (int i = 0; i < evaluations.size(); i++) {
            results[i] = evaluations.get(i).evaluate(messages);
        }
        
        double[] result = evalFunc.calculate(results);
        
        double threshold = network.getPrefs().getEvaluationThreshold();
        double uncertainty = network.getPrefs().getEvaluationUncertainty();
        
        for (int i = 0; i < messages.size(); i++) {
            
            EvalResult evalResult;
            if (result[i] > threshold + uncertainty) {
                evalResult = EvalResult.high;
            } else if (result[i] > threshold - uncertainty) {
                evalResult = EvalResult.medium;
            } else {
                evalResult = EvalResult.low;
            }
            
            messages.get(i).setResult(evalResult);
            if (evalResult == EvalResult.medium) {
                MessageFeedbackRequest req = new MessageFeedbackRequest(messages.get(i));
                network.getFeedbackModule().requestFeedback(req);
            }
        }
    }
    
    @Override
    public void build(LayerNetwork network) {
        network.getFeedbackModule().addFeedbackListener(FeedbackType.MESSAGE_IMPORTANCE, (request) -> {
            MessageFeedbackRequest req = (MessageFeedbackRequest) request;
            Message message = new Message(req.getMessage(), req.isSpam());
            List<Message> train = new ArrayList<>();
            train.add(message);

            this.train(train);
        });
    }

    @Override
    public void train(List<Message> messages) {
        for (Evaluation eval : evaluations) {
            if (eval instanceof Trainable) {
                Trainable t = (Trainable) eval;
                t.train(messages);
            }
        }
    }

    @Override
    public void save(String path, String name) {
        for (Evaluation eval : evaluations) {
            if (eval instanceof Storable) {
                Storable s = (Storable) eval;
                s.save(path, name);
            }
        }
    }

    @Override
    public void load(String path, String name) {
        for (Evaluation eval : evaluations) {
            if (eval instanceof Storable) {
                Storable s = (Storable) eval;
                s.load(path, name);
            }
        }
    }
    
}
