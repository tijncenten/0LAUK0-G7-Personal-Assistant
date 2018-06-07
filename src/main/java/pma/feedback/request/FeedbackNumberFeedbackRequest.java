package pma.feedback.request;

import pma.feedback.FeedbackModule.FeedbackType;

/**
 *
 * @author s167501
 */
public class FeedbackNumberFeedbackRequest extends FeedbackRequest {

    @Override
    public String getTitle() {
        return "Please indicate your view on the number of feedback requests";
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
        return FeedbackType.NR_FEEDBACK;
    }
    
    /**
     * Returns -1 when feedback should lower, 1 when feedback should increase,
     * 0 otherwise
     * 
     * @return 
     */
    public int getResult() {
        switch (this.getFeedback()) {
        case "TOO MANY":
            return -1;
        case "TOO FEW":
            return 1;
        default:
            return 0;
        }
    }
    
}
