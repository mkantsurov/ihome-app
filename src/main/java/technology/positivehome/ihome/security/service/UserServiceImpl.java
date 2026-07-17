package technology.positivehome.ihome.security.service;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.model.user.UserRole;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.model.UserEntity;
import technology.positivehome.ihome.server.persistence.repository.UserRepository;
import technology.positivehome.ihome.server.model.SessionInfo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by maxim on 1/5/19.
 **/
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public User getById(long userId) {
        UserEntity entity = userRepository.getById(userId);
        return toDomain(entity);
    }

    @Override
    public List<User> searchByUsername(String usernamePattern, int page, int size) {
        return userRepository.findByUsernamePattern(usernamePattern, page, size).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUsernamePattern(String usernamePattern) {
        return userRepository.countByUsernamePattern(usernamePattern);
    }

    @Override
    public List<User> searchUsers(@Nullable List<SearchParam> filters, @Nullable Integer page, @Nullable Integer size) {
        return userRepository.searchUsers(filters, page, size).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUsers(@Nullable List<SearchParam> filters) {
        return userRepository.countUsers(filters);
    }

    @Override
    @Transactional
    public long createUser(String username, String password, List<Role> roles) {
        List<Role> safeRoles = roles != null ? roles : Collections.emptyList();
        UserEntity entity = new UserEntity(null, username, password, safeRoles);
        SessionInfo sessionInfo = new SessionInfo(); // TODO: populate with actual session info
        return userRepository.create(sessionInfo, null, entity);
    }

    @Override
    @Transactional
    public void updateUser(long userId, String username, String password, List<Role> roles) {
        UserEntity existing = userRepository.getById(userId);

        String updatedUsername = username != null ? username : existing.username();
        String updatedPassword = password != null ? password : existing.password();
        List<Role> updatedRoles = roles != null ? roles : existing.roles();

        UserEntity entity = new UserEntity(userId, updatedUsername, updatedPassword, updatedRoles);
        SessionInfo sessionInfo = new SessionInfo(); // TODO: populate with actual session info
        userRepository.update(sessionInfo, null, entity);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        SessionInfo sessionInfo = new SessionInfo(); // TODO: populate with actual session info
        userRepository.remove(sessionInfo, null, userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private User toDomain(UserEntity entity) {
        List<UserRole> userRoles = entity.roles().stream()
                .map(role -> new UserRole(entity.id(), role))
                .collect(Collectors.toList());
        return new User(entity.id(), entity.username(), entity.password(), userRoles);
    }
}
