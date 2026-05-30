package technology.positivehome.ihome.server.persistence.repository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import technology.positivehome.ihome.server.model.EntityComparisonResult;
import technology.positivehome.ihome.server.model.SessionInfo;

import java.io.Serializable;

/**
 * Generic repository interface that declares base methods that should be present in the every AVS repository,
 * created for entities which we need to audit.
 * Note: there is {@link GenericIHomeRepository} which is used to work with generic db data (which not require audit)
 */
public interface AuditableIHomeRepository<T extends Serializable, K extends Serializable> {

    /**
     * Gets entity by ID.
     * @param id key to search
     * @return corresponding entity from the database
     * @throws org.springframework.dao.EmptyResultDataAccessException in case if corresponding entity not found
     */
    T getById(@Nonnull K id);

    /**
     * Persists a new entity
     *
     * @param sessionInfo  user/system session info used to create corresponding logs
     * @param rootEntityId related orderId for orders, userId for users, etc. Can be null if entity is not related with order or user, etc.
     * @param entity       that should be created in the DB
     * @return entity key
     */
    @Transactional(propagation = Propagation.REQUIRED)
    K create(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull T entity);

    /**
     * Updates entity and logs
     *
     * @param sessionInfo user/system session info used to create corresponding logs
     * @param rootEntityId related orderId for orders, userId for users, etc. Can be null if entity is not related with order or user, etc.
     * @param entity      that should be updated in the DB
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    EntityComparisonResult update(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull T entity);

    /**
     * Removes entity from the database
     * @param sessionInfo user/system session info used to create corresponding logs
     * @param rootEntityId related orderId for orders, userId for users, etc. Can be null if entity is not related with order or user, etc.
     * @param id entity ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void remove(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull K id);

}
