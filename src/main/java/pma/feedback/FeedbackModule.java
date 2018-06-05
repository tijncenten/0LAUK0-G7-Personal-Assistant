package pma.feedback;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import pma.feedback.request.FeedbackRequest;
import pma.feedback.request.FeedbackRequest.FeedbackDoneListener;

/**
 *
 * @author s167501
 */
public class FeedbackModule implements FeedbackDoneListener {
    
    public enum FeedbackType {ALL, BLOCK_RATE, THREAD_DEPTH, NR_FEEDBACK, MESSAGE_IMPORTANCE};
    
    private final Multimap<FeedbackType, FeedbackListener> listeners = MultimapBuilder.hashKeys().arrayListValues().build();
    private FeedbackEvaluator feedbackEvaluator = null;
    
    public void addFeedbackListener(FeedbackListener listener) {
        addFeedbackListener(FeedbackType.ALL, listener);
    }
    
    public void addFeedbackListener(FeedbackType type, FeedbackListener listener) {
        listeners.put(type, listener);
    }
    
    public void removeFeedbackListener(FeedbackListener listener) {
        removeFeedbackListener(FeedbackType.ALL, listener);
    }
    
    public void removeFeedbackListener(FeedbackType type, FeedbackListener listener) {
        listeners.remove(type, listener);
    }
    
    private void notifyListeners(FeedbackRequest request) {
        for (FeedbackListener l : listeners.get(request.getFeedbackType())) {
            l.ApplyFeedback(request);
        }
    }
    
    public void setFeedbackEvaluator(FeedbackEvaluator evaluator) {
        feedbackEvaluator = evaluator;
    }
    
    /**
     * Method for requesting feedback from within one of the layers;
     * Can be called when evaluation function is unsure about message categorization
     * 
     * @param request
     */
    public void requestFeedback(FeedbackRequest request) {
        if (feedbackEvaluator == null) {
            throw new IllegalStateException("No feedback evaluator set in the feedback module");
        }
        
        request.setDoneListener(this);
        
        feedbackEvaluator.evaluateFeedback(request);
    }
    
    /**
     * Method for processing feedback given by the user;
     * The appropriate feedback listeners should be notified about the feedback
     * 
     * @param request
     */
    @Override
    public void processFeedback(FeedbackRequest request) {
        if (!request.isDone()) {
            throw new IllegalStateException("request is not done");
        }
        notifyListeners(request);
    }
}
