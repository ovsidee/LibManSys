package dao;

import entity.User;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) for managing {@link User} entities.
 * Provides CRUD operations and ensures database interactions
 * are handled via JPA.
 */
public class UserDao implements Dao<User> {

    private final EntityManagerFactory emf;

    /**
     * Constructs a new UserDao with the provided EntityManagerFactory.
     *
     * @param emf the factory to create EntityManager instances
     */
    public UserDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Creates and persists a new {@link User} entity in the database.
     *
     * @param user the User entity to persist
     * @return the persisted User entity
     */
    @Override
    public User create(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user); // ID will be auto-generated
            em.getTransaction().commit();
            return user;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link User} entity by its ID.
     *
     * @param id the ID of the User to find
     * @return the User entity, or {@code null} if not found
     */
    @Override
    public User findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link User} entities from the database.
     *
     * @return a list of all Users
     */
    @Override
    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link User} entity in the database.
     *
     * @param user the User entity with updated fields
     * @return the updated User entity
     */
    @Override
    public User update(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User merged = em.merge(user);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link User} entity by its ID.
     * Ensures the User has no associated Borrowings before deletion.
     *
     * @param userId the ID of the User to delete
     * @throws IllegalStateException if the User has existing Borrowings
     */
    @Override
    public void delete(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                if (!user.getBorrowings().isEmpty()) {
                    throw new IllegalStateException("Cannot delete User with existing Borrowings");
                }
                em.remove(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}