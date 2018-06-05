package pma.feedback;

import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s167501
 */
public interface FeedbackListener {
    public void ApplyFeedback(FeedbackRequest request);
}
