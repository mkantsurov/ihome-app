package technology.positivehome.ihome.server.persistence.repository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import technology.positivehome.ihome.server.model.EntityComparisonResult;
import technology.positivehome.ihome.server.model.SessionInfo;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

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
    java.util.List<UserEntity> findAll();

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
