package technology.positivehome.ihome.security.service;

import org.springframework.stereotype.Service;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.model.user.UserRole;

import java.util.Collections;
import java.util.Optional;

/**
 * Created by maxim on 1/5/19.
 **/
@Service
public class DatabaseUserService implements UserService {
    @Override
    public Optional<User> getByUsername(String username) {
        if ("admin".equals(username)) {
            return Optional.of(new User(
                    1L,
                    "admin",
                    "$2a$10$9L3UYusKc9VYjzHSgAjo.edSat.g36KAmbX6FFcERC.le0nr6RZSK", //"$2a$10$9L3UYusKc9VYjzHSgAjo.eDu3d9U/2ui/84TjZL2BvSuuOrY9SbJa",
                    Collections.singletonList(new UserRole(1L, Role.ADMIN))));
        }
        return Optional.empty();
    }
}
