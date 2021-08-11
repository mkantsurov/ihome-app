package technology.positivehome.ihome.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Created by maxim on 1/5/19.
 **/
public class UserContext {

    private final long userId;
    private final List<GrantedAuthority> authorities;

    public UserContext(long userId, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    public static UserContext create(long userId, List<GrantedAuthority> authorities) {
        if (userId == 0) {
            throw new IllegalArgumentException("UserId :" + userId);
        }
        return new UserContext(userId, authorities);
    }


    public long getUserId() {
        return userId;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
