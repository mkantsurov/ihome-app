package technology.positivehome.ihome.security.model.user;

public enum Role {

    UNDEFINED,
    ADMIN,
    SUPERVISOR,
    CHILDREN_ROOM1_MANAGER,
    CHILDREN_ROOM2_MANAGER,
    AUTHORIZED_GUEST;

    public String authority() {
        return "ROLE_" + this.name();
    }

}
