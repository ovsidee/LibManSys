package dao;

import entity.Book;
import entity.Copy;

import javax.persistence.*;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing {@link Book} entities.
 * Provides CRUD operations for {@link Book} entities using JPA/Hibernate.
 */
public class BookDao implements Dao<Book> {

    private final EntityManagerFactory emf;

    /**
     * Constructor to initialize the {@link BookDao} with an {@link EntityManagerFactory}.
     *
     * @param emf the {@link EntityManagerFactory} to be used for database operations.
     */
    public BookDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link Book} entity in the database.
     *
     * @param book the {@link Book} entity to be created.
     * @return the persisted {@link Book} entity with an auto-generated ID.
     */
    @Override
    public Book create(Book book) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book); // ID will be auto-generated
            em.getTransaction().commit();
            return book;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Book} entity by its ID.
     *
     * @param id the ID of the {@link Book} to find.
     * @return the found {@link Book} entity or {@code null} if no entity is found.
     */
    @Override
    public Book findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all {@link Book} entities from the database.
     *
     * @return a {@link List} of all {@link Book} entities.
     */
    @Override
    public List<Book> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing {@link Book} entity in the database.
     *
     * @param book the {@link Book} entity with updated values.
     * @return the updated {@link Book} entity.
     */
    @Override
    public Book update(Book book) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book merged = em.merge(book);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }
    /**
     * Deletes a {@link Book} entity by its ID. Ensures that a {@link Book} cannot
     * be deleted if it still has associated {@link Copy} entities.
     *
     * @param bookId the ID of the {@link Book} to delete.
     * @throws IllegalStateException if the {@link Book} has associated {@link Copy} entities.
     */
    @Override
    public void delete(Long bookId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, bookId);
            if (book != null) {
                // If the Book still has child Copies, throw an exception
                if (!book.getCopies().isEmpty()) {
                    throw new IllegalStateException("Cannot delete Book that still has Copies");
                }
                em.remove(book);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}