package pma.feedback.request;

import pma.feedback.FeedbackModule.FeedbackType;

/**
 *
 * @author s167501
 */
public abstract class FeedbackRequest {
    
    private String feedback;
    private boolean done = false;
    private FeedbackDoneListener doneListener;
    
    public abstract String getTitle();
    
    public abstract String getSubTitle();
    
    public abstract String[] getOptions();
    
    public abstract FeedbackType getFeedbackType();
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.done = true;
        
        if (doneListener == null) {
            throw new IllegalStateException("No listener set on the feedback request");
        }
        
        doneListener.processFeedback(this);
    }
    
    public String getFeedback() {
        return this.feedback;
    }
    
    public boolean isDone() {
        return done;
    }
    
    public void setDoneListener(FeedbackDoneListener doneCallback) {
        this.doneListener = doneCallback;
    }
    
            
    public interface FeedbackDoneListener {
        public void processFeedback(FeedbackRequest request);
    }
}
