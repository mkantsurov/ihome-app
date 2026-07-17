package technology.positivehome.ihome.model.runtime.user;

import technology.positivehome.ihome.security.model.user.Role;

import java.util.List;

/**
 * Response DTO representing a user's public information.
 */
public record UserInfo(Long id, String username, List<Role> roles) {

    public static UserInfo from(Long id, String username, List<Role> roles) {
        return new UserInfo(id, username, roles);
    }
}
