package technology.positivehome.ihome.security.model.user;

import java.util.List;

public class User {
    //db instance. must be reviewed

    private Long id;

    private String username;
    private String password;
    private List<UserRole> roles;

    public User() {
    }

    public User(Long id, String username, String password, List<UserRole> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<UserRole> getRoles() {
        return roles;
    }
}

