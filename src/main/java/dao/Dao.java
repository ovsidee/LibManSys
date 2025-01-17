package dao;

import java.util.List;

/**
 * Generic Data Access Object (DAO) interface that defines basic CRUD operations.
 *
 * @param <T> the type of the entity that this DAO will manage.
 */
public interface Dao<T> {
    /**
     * Persists a new entity in the database.
     *
     * @param entity the entity to be created.
     * @return the persisted entity, with an auto-generated ID.
     */
    T create(T entity);

    /**
     * Finds an entity by its ID.
     *
     * @param id the ID of the entity to find.
     * @return the found entity, or {@code null} if no entity with the given ID exists.
     */
    T findById(Long id);

    /**
     * Retrieves all entities of this type from the database.
     *
     * @return a {@link List} containing all entities of this type.
     */
    List<T> findAll();

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity with updated values.
     * @return the updated entity.
     */
    T update(T entity);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete.
     * @throws IllegalStateException if the entity cannot be deleted due to existing relationships or constraints.
     */
    void delete(Long id);
}
