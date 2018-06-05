package pma.feedback.request;

import pma.feedback.FeedbackModule.FeedbackType;

/**
 *
 * @author s167501
 */
public class BlockRateFeedbackRequest extends FeedbackRequest {
    
    @Override
    public String getTitle() {
        return "Please indicate whether more or less notifications should be blocked";
    }

    @Override
    public String getSubTitle() {
        return "";
    }

    @Override
    public String[] getOptions() {
        return new String[] {"MORE", "LESS", "STAY"};
    }

    @Override
    public FeedbackType getFeedbackType() {
        return FeedbackType.BLOCK_RATE;
    }
    
}
