package technology.positivehome.ihome.security.service;

import technology.positivehome.ihome.security.model.user.User;

import java.util.Optional;

/**
 * Created by maxim on 1/5/19.
 **/
public interface UserService {
    Optional<User> getByUsername(String username);

    User getById(long userId);
}
