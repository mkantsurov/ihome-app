package technology.positivehome.ihome.server.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.model.runtime.user.CreateUserRequest;
import technology.positivehome.ihome.model.runtime.user.UpdateUserRequest;
import technology.positivehome.ihome.model.runtime.user.UserInfo;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.service.UserService;
import technology.positivehome.ihome.server.model.SearchParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing iHome users.
 * Provides endpoints to list, create, update, and delete users.
 * All endpoints require authentication; mutating endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves users with pagination and optional username pattern search.
     * <p>
     * Returns a paginated slice with an {@code X-Total-Count} header.
     * An optional {@code filter} parameter can be used to search by username
     * pattern (SQL LIKE syntax, e.g. {@code "adm%"}).
     *
     * @param filter optional username search pattern (supports {@code %} and {@code _} wildcards)
     * @param page   the page index (0-based), defaults to 0
     * @param size   the page size (1-100), defaults to 20
     * @return list of users (without passwords)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserInfo>> search(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {

        String effectiveFilter = (filter != null && !filter.isBlank()) ? filter : "%";
        logger.debug("Searching users by pattern '{}', page={}, size={}", effectiveFilter, page, size);

        long totalCount = userService.countByUsernamePattern(effectiveFilter);
        List<User> users = userService.searchByUsername(effectiveFilter, page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Total-Count");
        headers.add("X-Total-Count", String.valueOf(totalCount));

        return ResponseEntity.ok()
                .headers(headers)
                .body(users.stream().map(this::toUserInfo).collect(Collectors.toList()));
    }

    /**
     * Searches for users using filter-based criteria (e.g. by username pattern, by role).
     * <p>
     * Accepts a list of {@link SearchParam} filters. Supported filter keys:
     * <ul>
     *   <li>{@code USERNAME} — with predicates: ilike, like, =, !=, lIlike, rIlike, etc.</li>
     *   <li>{@code ROLE} — with predicates: =, !=, in, not in (values: ADMIN, UNDEFINED)</li>
     * </ul>
     *
     * @param filters the list of search parameters
     * @param page    the page index (0-based), defaults to 0
     * @param size    the page size (1-100), defaults to 20
     * @return list of users (without passwords) with X-Total-Count header
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/search")
    public ResponseEntity<List<UserInfo>> searchUsers(
            @RequestBody(required = false) List<SearchParam> filters,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {

        logger.debug("Searching users with filters: {}, page={}, size={}", filters, page, size);

        long totalCount = userService.countUsers(filters);
        List<User> users = userService.searchUsers(filters, page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Total-Count");
        headers.add("X-Total-Count", String.valueOf(totalCount));

        return ResponseEntity.ok()
                .headers(headers)
                .body(users.stream().map(this::toUserInfo).collect(Collectors.toList()));
    }

    /**
     * Retrieves a single user by ID.
     *
     * @param userId the user ID
     * @return the user info (without password)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable long userId) {
        logger.debug("Fetching user by id: {}", userId);
        try {
            User user = userService.getById(userId);
            return ResponseEntity.ok(toUserInfo(user));
        } catch (Exception e) {
            logger.warn("User with id {} not found", userId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new user.
     *
     * @param request the create user request containing username, password, and roles
     * @return the created user info with generated ID, or conflict if username already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserInfo> createUser(@RequestBody CreateUserRequest request) {
        logger.debug("Creating user: {}", request.username());

        if (userService.existsByUsername(request.username())) {
            logger.warn("Username '{}' already exists", request.username());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        long userId = userService.createUser(request.username(), encodedPassword, request.roles());
        logger.info("User created with id: {}", userId);

        User created = userService.getById(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserInfo(created));
    }

    /**
     * Updates an existing user.
     *
     * @param userId  the user ID to update
     * @param request the update request with fields to change (null fields are ignored)
     * @return the updated user info, or not found if user doesn't exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserInfo> updateUser(@PathVariable long userId,
                                               @RequestBody UpdateUserRequest request) {
        logger.debug("Updating user: {}", userId);

        // Verify user exists
        try {
            userService.getById(userId);
        } catch (Exception e) {
            logger.warn("User with id {} not found for update", userId);
            return ResponseEntity.notFound().build();
        }

        // If a new username is provided, check it doesn't conflict with another user
        if (request.username() != null && !request.username().isBlank()) {
            Optional<User> existing = userService.getByUsername(request.username());
            if (existing.isPresent() && existing.get().getId() != userId) {
                logger.warn("Username '{}' already taken by another user", request.username());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        String encodedPassword = request.password() != null
                ? passwordEncoder.encode(request.password())
                : null;

        userService.updateUser(userId, request.username(), encodedPassword, request.roles());
        logger.info("User {} updated", userId);

        User updated = userService.getById(userId);
        return ResponseEntity.ok(toUserInfo(updated));
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the user ID to delete
     * @return no content on success, or not found if user doesn't exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        logger.debug("Deleting user: {}", userId);

        // Verify user exists
        try {
            userService.getById(userId);
        } catch (Exception e) {
            logger.warn("User with id {} not found for deletion", userId);
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(userId);
        logger.info("User {} deleted", userId);
        return ResponseEntity.noContent().build();
    }

    private UserInfo toUserInfo(User user) {
        List<Role> roles = Optional.ofNullable(user.getRoles())
                .map(userRoles -> userRoles.stream()
                        .map(ur -> ur.getRole())
                        .collect(Collectors.toList()))
                .orElse(List.of());
        return UserInfo.from(user.getId(), user.getUsername(), roles);
    }
}
