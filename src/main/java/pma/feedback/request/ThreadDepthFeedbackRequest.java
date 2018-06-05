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
    
}
