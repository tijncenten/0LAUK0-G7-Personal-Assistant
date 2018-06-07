package pma.feedback;

import java.util.Arrays;
import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s167501
 */
public class AutoFeedbackEvaluator implements FeedbackEvaluator {

    @Override
    public void evaluateFeedback(FeedbackRequest request) {
        System.out.println("\n==============\nASK FEEDBACK:\n" + request.getTitle());
        System.out.println(request.getSubTitle());
        System.out.println("Options: " + Arrays.toString(request.getOptions()));
        System.out.println("==============\n");
        request.setFeedback(request.getOptions()[0]);
    }
    
}
