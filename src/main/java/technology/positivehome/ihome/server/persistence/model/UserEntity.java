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

    /**
     * Creates a new {@link Builder} instance.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link UserEntity}.
     * Allows step-by-step construction with nullable fields.
     */
    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private List<Role> roles;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserEntity build() {
            if (roles == null) {
                roles = List.of();
            }
            return new UserEntity(id, username, password, roles);
        }
    }
}
