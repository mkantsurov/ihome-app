package technology.positivehome.ihome.server.persistence.repository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import technology.positivehome.ihome.server.model.EntityComparisonResult;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.model.SessionInfo;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for persisting and retrieving User entities.
 * Implements {@link AuditableIHomeRepository} to support audit logging for user operations.
 */
public interface UserRepository extends AuditableIHomeRepository<UserEntity, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the UserEntity if found, or empty if not found
     */
    @Nonnull
    Optional<UserEntity> findByUsername(@Nonnull String username);

    /**
     * Finds a user entity by their unique identifier.
     *
     * @param userId the user ID to search for
     * @return an {@link Optional} containing the {@link UserEntity} if found, or empty if no user exists with the given ID
     */
    Optional<UserEntity> findById(long userId);

    /**
     * Searches for users whose username matches the given pattern (case-insensitive).
     *
     * @param usernamePattern the username pattern to search for (supports SQL LIKE wildcards: %, _)
     * @param page            the page index (0-based)
     * @param size            the page size
     * @return a list of matching UserEntity records for the requested page
     */
    @Nonnull
    List<UserEntity> findByUsernamePattern(@Nonnull String usernamePattern, int page, int size);

    /**
     * Counts how many users match the given username pattern (case-insensitive).
     *
     * @param usernamePattern the username pattern to search for
     * @return the total count of matching users
     */
    long countByUsernamePattern(@Nonnull String usernamePattern);

    /**
     * Searches for users using the filter-based {@link UserSearchQueryExecutor}.
     * Supports filtering by {@link technology.positivehome.ihome.model.constant.SearchField#USERNAME}
     * and {@link technology.positivehome.ihome.model.constant.SearchField#ROLE}.
     *
     * @param filters the list of search parameters (nullable)
     * @param page    the page index (0-based, nullable for no pagination)
     * @param size    the page size (nullable for no pagination)
     * @return a list of matching UserEntity records
     */
    @Nonnull
    List<UserEntity> searchUsers(@Nullable List<SearchParam> filters, @Nullable Integer page, @Nullable Integer size);

    /**
     * Counts users matching the given filter-based search criteria.
     *
     * @param filters the list of search parameters (nullable)
     * @return the total count of matching users
     */
    long countUsers(@Nullable List<SearchParam> filters);

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(@Nonnull String username);

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all UserEntity records
     */
    List<UserEntity> findAll();

    /**
     * Creates a new user entity with audit logging.
     *
     * @param sessionInfo  the session info for audit logging
     * @param rootEntityId can be null for top-level entity, or the parent entity ID
     * @param entity       the user entity to persist
     * @return the generated user ID
     */
    @Override
    @Nonnull
    Long create(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull UserEntity entity);

    /**
     * Updates an existing user with audit logging.
     *
     * @param sessionInfo  the session info for audit logging
     * @param rootEntityId can be null for top-level entity, or the parent entity ID
     * @param entity       the user entity with updated values
     * @return comparison result before/after update
     */
    @Override
    EntityComparisonResult update(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull UserEntity entity);

    /**
     * Removes a user with audit logging.
     *
     * @param sessionInfo  the session info for audit logging
     * @param rootEntityId can be null for top-level entity, or the parent entity ID
     * @param id           the user ID to remove
     */
    @Override
    void remove(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull Long id);
}
