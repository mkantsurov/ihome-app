package technology.positivehome.ihome.security.service;

import jakarta.annotation.Nullable;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.server.model.SearchParam;

import java.util.List;
import java.util.Optional;

/**
 * Created by maxim on 1/5/19.
 **/
public interface UserService {
    Optional<User> getByUsername(String username);

    User getById(long userId);

    /**
     * Searches for users whose username matches the given pattern (case-insensitive, supports SQL LIKE wildcards).
     *
     * @param usernamePattern the username pattern (e.g. "adm%", "%min")
     * @param page            the page index (0-based)
     * @param size            the page size
     * @return a list of matching users for the requested page
     */
    List<User> searchByUsername(String usernamePattern, int page, int size);

    /**
     * Counts how many users match the given username pattern.
     *
     * @param usernamePattern the username pattern
     * @return the total count of matching users
     */
    long countByUsernamePattern(String usernamePattern);

    /**
     * Searches for users using filter-based criteria (e.g. by username pattern, by role).
     *
     * @param filters the list of search parameters (nullable)
     * @param page    the page index (0-based, nullable for no pagination)
     * @param size    the page size (nullable for no pagination)
     * @return a list of matching users for the requested page
     */
    List<User> searchUsers(@Nullable List<SearchParam> filters, @Nullable Integer page, @Nullable Integer size);

    /**
     * Counts users matching the given filter-based search criteria.
     *
     * @param filters the list of search parameters (nullable)
     * @return the total count of matching users
     */
    long countUsers(@Nullable List<SearchParam> filters);

    long createUser(String username, String password, List<Role> roles);

    void updateUser(long userId, String username, String password, List<Role> roles);

    void deleteUser(long userId);

    boolean existsByUsername(String username);
}
