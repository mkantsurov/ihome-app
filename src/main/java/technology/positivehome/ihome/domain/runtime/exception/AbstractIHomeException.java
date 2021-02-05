package technology.positivehome.ihome.domain.runtime.exception;

/**
 * Created by maxim on 6/30/19.
 **/
public class AbstractIHomeException extends Exception {
    public AbstractIHomeException(String message) {
        super(message);
    }

    public AbstractIHomeException(String message, Throwable cause) {
        super(message, cause);
    }
}
