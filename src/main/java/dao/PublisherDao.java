package dao;

import entity.Publisher;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Publisher} entities.
 * Provides methods to perform CRUD operations on the database.
 */
public class PublisherDao implements Dao<Publisher> {

    private final EntityManagerFactory emf;

    /**
     * Constructs a new {@code PublisherDao} with the specified {@link EntityManagerFactory}.
     *
     * @param emf the {@link EntityManagerFactory} to use for database operations.
     */
    public PublisherDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link Publisher} entity in the database.
     *
     * @param publisher the publisher entity to be created.
     * @return the persisted {@link Publisher} entity, potentially with an auto-generated ID.
     */
    @Override
    public Publisher create(Publisher publisher) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(publisher);
            em.getTransaction().commit();
            return publisher;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Publisher} entity by its ID.
     *
     * @param id the ID of the publisher to find.
     * @return the found {@link Publisher} entity, or {@code null} if no publisher with the given ID exists.
     */
    @Override
    public Publisher findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Publisher.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link Publisher} entities from the database.
     *
     * @return a {@link List} of all {@link Publisher} entities.
     */
    @Override
    public List<Publisher> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Publisher p", Publisher.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link Publisher} entity in the database.
     *
     * @param publisher the publisher entity with updated values.
     * @return the updated {@link Publisher} entity.
     */
    @Override
    public Publisher update(Publisher publisher) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Publisher merged = em.merge(publisher);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link Publisher} entity by its ID.
     * If the publisher is referenced by any {@link entity.Book}, an {@link IllegalStateException} is thrown.
     *
     * @param publisherId the ID of the publisher to delete.
     * @throws IllegalStateException if the publisher is referenced by any {@link entity.Book}.
     */
    @Override
    public void delete(Long publisherId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Publisher publisher = em.find(Publisher.class, publisherId);
            if (publisher != null) {
                // Check if any Book references this Publisher
                long countBooks = em.createQuery(
                                "SELECT COUNT(b) FROM Book b WHERE b.publisherEntity.id = :pubId", Long.class)
                        .setParameter("pubId", publisherId)
                        .getSingleResult();

                if (countBooks > 0) {
                    throw new IllegalStateException("Cannot delete a Publisher that still has Books");
                }
                em.remove(publisher);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}
