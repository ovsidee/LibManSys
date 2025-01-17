package dao;

import entity.Librarian;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Librarian} entities.
 * Provides methods to perform CRUD operations on the database.
 */
public class LibrarianDao implements Dao<Librarian> {

    private final EntityManagerFactory emf;

    /**
     * Constructs a new {@code LibrarianDao} with the specified {@link EntityManagerFactory}.
     *
     * @param emf the {@link EntityManagerFactory} to use for database operations.
     */
    public LibrarianDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link Librarian} entity in the database.
     *
     * @param librarian the librarian entity to be created.
     * @return the persisted {@link Librarian} entity, potentially with an auto-generated ID.
     */
    @Override
    public Librarian create(Librarian librarian) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(librarian);
            em.getTransaction().commit();
            return librarian;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Librarian} entity by its ID.
     *
     * @param id the ID of the librarian to find.
     * @return the found {@link Librarian} entity, or {@code null} if no librarian with the given ID exists.
     */
    @Override
    public Librarian findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Librarian.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link Librarian} entities from the database.
     *
     * @return a {@link List} of all {@link Librarian} entities.
     */
    @Override
    public List<Librarian> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Librarian l", Librarian.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link Librarian} entity in the database.
     *
     * @param librarian the librarian entity with updated values.
     * @return the updated {@link Librarian} entity.
     */
    @Override
    public Librarian update(Librarian librarian) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Librarian merged = em.merge(librarian);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link Librarian} entity by its ID.
     *
     * @param librarianId the ID of the librarian to delete.
     */
    @Override
    public void delete(Long librarianId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Librarian librarian = em.find(Librarian.class, librarianId);
            if (librarian != null) {
                em.remove(librarian);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
