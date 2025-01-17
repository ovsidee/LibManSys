package dao;

import entity.Copy;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Copy} entities.
 * Provides CRUD operations for {@link Copy} entities using JPA/Hibernate.
 */
public class CopyDao implements Dao<Copy> {

    private final EntityManagerFactory emf;

    /**
     * Constructor to initialize the {@link CopyDao} with an {@link EntityManagerFactory}.
     *
     * @param emf the {@link EntityManagerFactory} to be used for database operations.
     */
    public CopyDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link Copy} entity in the database.
     *
     * @param copy the {@link Copy} entity to be created.
     * @return the persisted {@link Copy} entity with an auto-generated ID.
     */
    @Override
    public Copy create(Copy copy) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(copy);
            em.getTransaction().commit();
            return copy;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Copy} entity by its ID.
     *
     * @param id the ID of the {@link Copy} to find.
     * @return the found {@link Copy} entity or {@code null} if no entity is found.
     */
    @Override
    public Copy findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Copy.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link Copy} entities from the database.
     *
     * @return a {@link List} of all {@link Copy} entities.
     */
    @Override
    public List<Copy> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Copy c", Copy.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link Copy} entity in the database.
     *
     * @param copy the {@link Copy} entity with updated values.
     * @return the updated {@link Copy} entity.
     */
    @Override
    public Copy update(Copy copy) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Copy merged = em.merge(copy);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link Copy} entity by its ID. Ensures that the {@link Copy} is not marked as "Borrowed"
     * before deletion.
     *
     * @param id the ID of the {@link Copy} to delete.
     * @throws IllegalStateException if the {@link Copy} is currently marked as "Borrowed".
     */
    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Copy copy = em.find(Copy.class, id);
            if (copy != null) {
                if ("Borrowed".equals(copy.getStatus())) {
                    throw new IllegalStateException("Cannot delete a Copy that is currently Borrowed");
                }
                em.remove(copy);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}