package pma.feedback;

import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s167501
 */
public class AutoFeedbackEvaluator implements FeedbackEvaluator {

    @Override
    public void evaluateFeedback(FeedbackRequest request) {
        request.setFeedback(request.getOptions()[0]);
    }
    
}
