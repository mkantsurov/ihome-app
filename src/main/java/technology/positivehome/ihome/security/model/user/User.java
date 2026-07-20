package technology.positivehome.ihome.security.model.user;

import java.util.List;

public record User(Long id, String username, String password, List<UserRole> roles) {

    public boolean hasOneOfRoles(Role... rolesToCheck) {
        if (roles == null) return false;
        for (UserRole ur : roles) {
            for (Role r : rolesToCheck) {
                if (ur.role().equals(r)) {
                    return true;
                }
            }
        }
        return false;
    }
}
