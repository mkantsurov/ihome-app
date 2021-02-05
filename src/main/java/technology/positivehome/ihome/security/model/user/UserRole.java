package technology.positivehome.ihome.security.model.user;

import java.io.Serializable;

public class UserRole {

    public static class Id implements Serializable {
        private static final long serialVersionUID = 1322120000551624359L;
        protected Long userId;

        protected Role role;

        public Id() {
        }

        public Id(Long userId, Role role) {
            this.userId = userId;
            this.role = role;
        }
    }

    public UserRole(long id, Role ur) {
        this.id = new Id(id, ur);
        this.role = ur;
    }

    public UserRole(Id id) {
        this.id = id;
        this.role = id.role;
    }

    Id id = new Id();

    protected Role role;

    public Role getRole() {
        return role;
    }

}
