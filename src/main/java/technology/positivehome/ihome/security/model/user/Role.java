package technology.positivehome.ihome.security.model.user;

public enum Role {

    UNDEFINED,
    ADMIN;

    public String authority() {
        return "ROLE_" + this.name();
    }

}
