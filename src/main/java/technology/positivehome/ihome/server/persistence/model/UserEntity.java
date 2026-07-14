package technology.positivehome.ihome.server.persistence.model;

import technology.positivehome.ihome.security.model.user.Role;

import java.util.List;

public record UserEntity(Long id, String username, String password, List<Role> roles) {

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}
