package pma.feedback.request;

import pma.feedback.FeedbackModule.FeedbackType;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class MessageFeedbackRequest extends FeedbackRequest {
    
    private final Message message;
    
    public MessageFeedbackRequest(Message message) {
        this.message = message;
    }

    @Override
    public String getTitle() {
        return "Please indicate whether the message is usefull";
    }

    @Override
    public String getSubTitle() {
        return message.getSender() + ": " + message.getText();
    }

    @Override
    public String[] getOptions() {
        return new String[] {"USEFULL", "NOT USEFULL"};
    }

    @Override
    public FeedbackType getFeedbackType() {
        return FeedbackType.MESSAGE_IMPORTANCE;
    }
    
    public Message getMessage() {
        return this.message;
    }
    
    public boolean isSpam() {
        return this.getFeedback().equals("NOT USEFULL");
    }
    
}
