package technology.positivehome.ihome.security.auth.ajax;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by maxim on 12/20/18.
 **/
public class LoginRequest {
    private String username;
    private String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
