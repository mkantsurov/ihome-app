package technology.positivehome.ihome.security.service;

import org.springframework.stereotype.Service;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.model.user.UserRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by maxim on 1/5/19.
 **/
@Service
public class UserServiceImpl implements UserService {

    private static final List<User> users = Arrays.asList(new User(
            1L,
            "admin",
            "$2a$10$9L3UYusKc9VYjzHSgAjo.eXbKqnj72O0qIgo/ty6jEWw2GFuQkPSK", //"г р 11 к 3 2 3 #%"
            Collections.singletonList(new UserRole(1L, Role.ADMIN))));

    @Override
    public Optional<User> getByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    @Override
    public User getById(long userId) {
        return users.stream().filter(user -> userId == user.getId()).findFirst().orElseThrow();
    }

}
