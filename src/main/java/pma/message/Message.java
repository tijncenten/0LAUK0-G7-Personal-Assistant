package pma.message;

import pma.PersonalMessagingAssistant.EvalResult;
import pma.contact.Contact;

/**
 *
 * @author s167501
 */
public class Message {
    
    protected String text;
    protected String originalText;
    protected final long timestamp;
    protected final Contact sender;
    protected Message request = null;
    protected final boolean hasMedia = false;
    
    protected EvalResult result = null;
    protected int threadIndex = -1;
    
    protected Boolean spam = null;
    
    public Message(String text, long timestamp, Contact sender) {
        this.text = text;
        this.originalText = text;
        this.timestamp = timestamp;
        this.sender = sender;
    }
    
    public Message(Message original, boolean spam) {
        this.text = original.getText();
        this.originalText = original.getOriginalText();
        this.timestamp = original.getTimestamp();
        this.sender = original.getSender();
        this.result = original.getResult();
        this.spam = spam;
    }
    
    public Message(String text, long timestamp, Contact sender, boolean spam) {
        this.text = text;
        this.originalText = text;
        this.timestamp = timestamp;
        this.sender = sender;
        this.spam = spam;
    }

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getOriginalText() {
        return originalText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Contact getSender() {
        return sender;
    }

    public Message getRequest() {
        return request;
    }

    public boolean isHasMedia() {
        return hasMedia;
    }
    
    public boolean hasResult() {
        return result != null;
    }
    
    public EvalResult getResult() {
        return result;
    }
    
    public void setResult(EvalResult result) {
        this.result = result;
    }
    
    public int getThreadIndex() {
        return this.threadIndex;
    }
    
    public void setThreadIndex(int threadIndex) {
        this.threadIndex = threadIndex;
    }
    
    public boolean hasThread() {
        return this.threadIndex >= 0;
    }
    
    public Boolean isSpam() {
        return this.spam;
    }
    
    @Override
    public String toString() {
        if (hasResult()) {
            return getSender() + ": (" + this.threadIndex + ") : " + getText() + " <= " + getResult().name() + " => " + "Spam: " + this.spam;
        }
        return getSender() + ": (" + this.threadIndex + ") : " + getText() + " <= --- =>";
    }
    
}
