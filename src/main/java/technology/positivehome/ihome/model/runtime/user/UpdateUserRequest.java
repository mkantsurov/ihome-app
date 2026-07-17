package technology.positivehome.ihome.model.runtime.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.List;

/**
 * Request DTO for updating an existing user.
 * Only non-null fields will be updated.
 */
public record UpdateUserRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("roles") List<Role> roles) {
}
