package pma.feedback;

import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s167501
 */
public interface FeedbackEvaluator {
    
    /**
     * Method that will evaluate the given request to, for example, a user;
     * Afterwards invoking the method {@code setFeedback} from the request
     * which will send the feedback back
     * 
     * @param request 
     */
    public void evaluateFeedback(FeedbackRequest request);
}
