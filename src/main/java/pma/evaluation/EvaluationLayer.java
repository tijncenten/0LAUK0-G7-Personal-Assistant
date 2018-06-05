package pma.evaluation;

import pma.evaluation.function.EvaluationFunction;
import java.util.ArrayList;
import java.util.List;
import pma.layer.Layer;
import pma.PersonalMessagingAssistant.EvalResult;
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
        
        EvalResult[] result = evalFunc.calculate(results);
        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).setResult(result[i]);
        }
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
