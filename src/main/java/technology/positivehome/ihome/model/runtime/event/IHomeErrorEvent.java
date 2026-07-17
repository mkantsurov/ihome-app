package technology.positivehome.ihome.model.runtime.event;

import org.springframework.context.ApplicationEvent;
import technology.positivehome.ihome.model.constant.ErrorEventType;

/**
 * Event fired when an error occurs in the iHome system.
 * Services can listen for this event to log errors persistently.
 */
public class IHomeErrorEvent extends ApplicationEvent {

    private final ErrorEventType type;
    private final String message;

    public IHomeErrorEvent(Object source, ErrorEventType type, String message) {
        super(source);
        this.type = type;
        this.message = message;
    }

    public ErrorEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
