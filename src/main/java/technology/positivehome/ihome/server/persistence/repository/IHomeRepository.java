package technology.positivehome.ihome.server.persistence.repository;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Generic repository interface that declares base methods that should be present in the repositories
 */
public interface IHomeRepository<T, K extends Serializable> {

    /**
     * Gets entity by ID.
     * @param id key to search
     * @return corresponding entity from the database
     * @throws org.springframework.dao.EmptyResultDataAccessException in case if corresponding entity not found
     */
    T getById(@Nonnull K id);

    /**
     * Persists a new entity
     * @param entity that should be created in the DB
     * @return entity key
     */
    @Transactional(propagation = Propagation.REQUIRED)
    K create(@Nonnull T entity);

    /**
     * Updates entity and logs
     * @param entity that should be updated in the DB
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void update(@Nonnull T entity);

    /**
     * Removes entity from the database
     * @param id entity ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void remove(@Nonnull K id);
}
