package pma.feedback.request;

import pma.feedback.FeedbackModule.FeedbackType;

/**
 *
 * @author s167501
 */
public class ThreadDepthFeedbackRequest extends FeedbackRequest {

    @Override
    public String getTitle() {
        return "Please indicate your view on the creation of message threads";
    }

    @Override
    public String getSubTitle() {
        return "";
    }

    @Override
    public String[] getOptions() {
        return new String[] {"TOO FEW", "TOO MANY", "GOOD"};
    }

    @Override
    public FeedbackType getFeedbackType() {
        return FeedbackType.THREAD_DEPTH;
    }
    
    /**
     * Returns -1 when too few threads are created, 1 when too many threads
     * and 0 otherwise
     * 
     * @return 
     */
    public int getResult() {
        switch (this.getFeedback()) {
        case "TOO FEW":
            return -1;
        case "TOO MANY":
            return 1;
        default:
            return 0;
        }
    }
    
}
