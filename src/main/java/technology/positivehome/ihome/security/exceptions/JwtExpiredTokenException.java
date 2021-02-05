package technology.positivehome.ihome.security.exceptions;

import org.springframework.security.core.AuthenticationException;
import technology.positivehome.ihome.security.model.token.JwtToken;

public class JwtExpiredTokenException extends AuthenticationException {

    private static final long serialVersionUID = -196262366498730012L;

    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }

}
