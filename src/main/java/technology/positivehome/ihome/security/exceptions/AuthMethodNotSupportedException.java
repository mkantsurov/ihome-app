package technology.positivehome.ihome.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Created by maxim on 12/20/18.
 **/
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 8274983722657367657L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }

}
