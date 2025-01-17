package dao;

import entity.Borrowing;
import entity.Copy;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Borrowing} entities.
 * Provides CRUD operations for {@link Borrowing} entities using JPA/Hibernate.
 */
public class BorrowingDao implements Dao<Borrowing> {

    private final EntityManagerFactory emf;
    /**
     * Constructor to initialize the {@link BorrowingDao} with an {@link EntityManagerFactory}.
     *
     * @param emf the {@link EntityManagerFactory} to be used for database operations.
     */
    public BorrowingDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link Borrowing} entity in the database.
     *
     * @param borrowing the {@link Borrowing} entity to be created.
     * @return the persisted {@link Borrowing} entity with an auto-generated ID.
     */
    @Override
    public Borrowing create(Borrowing borrowing) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(borrowing); // ID will be auto-generated
            em.getTransaction().commit();
            return borrowing;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Borrowing} entity by its ID.
     *
     * @param id the ID of the {@link Borrowing} to find.
     * @return the found {@link Borrowing} entity or {@code null} if no entity is found.
     */
    @Override
    public Borrowing findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Borrowing.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link Borrowing} entities from the database.
     *
     * @return a {@link List} of all {@link Borrowing} entities.
     */
    @Override
    public List<Borrowing> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Borrowing b", Borrowing.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link Borrowing} entity in the database.
     *
     * @param borrowing the {@link Borrowing} entity with updated values.
     * @return the updated {@link Borrowing} entity.
     */
    @Override
    public Borrowing update(Borrowing borrowing) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Borrowing merged = em.merge(borrowing);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link Borrowing} entity by its ID. Ensures that the associated {@link Copy}
     * is marked as "Available" when the borrowing is deleted.
     *
     * @param borrowingId the ID of the {@link Borrowing} to delete.
     */
    @Override
    public void delete(Long borrowingId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Borrowing borrowing = em.find(Borrowing.class, borrowingId);
            if (borrowing != null) {
                // Reset the associated copy's status to "Available"
                Copy copy = borrowing.getCopy();
                if (copy != null) {
                    copy.setStatus("Available");
                    em.merge(copy);
                }

                // Delete the borrowing
                em.remove(borrowing);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
