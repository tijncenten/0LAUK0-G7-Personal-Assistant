package pma.contact;

/**
 *
 * @author s167501
 */
public class Contact {
    
    protected final String phoneNumber;
    protected String name;
    
    protected boolean isAddedToContacts = false;
    
    public enum Priority {low, medium, high};
    protected Priority customPriority = null;
    
    public Contact(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
    
    public Priority getPriority() {
        if (customPriority == null) {
            if (isAddedToContacts) {
                return Priority.medium;
            } else {
                return Priority.low;
            }
        }
        return customPriority;
    }
    
    @Override
    public String toString() {
        return phoneNumber + " (" + name + ")";
    }
}
