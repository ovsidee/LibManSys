package entity;

import javax.persistence.*;

/**
 * Represents a physical copy of a book in the library.
 * Each copy is associated with a specific book and has unique properties such as copy number and status.
 */
@Entity
@Table(name = "Copies")
public class Copy {
    /**
     * Unique identifier for the copy.
     * Auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The book associated with this copy.
     * Many copies can reference one book.
     * CascadeType.REMOVE ensures that when a book is deleted, its associated copies are also removed.
     */
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * A unique number assigned to each copy of a book.
     * This helps differentiate between multiple copies of the same book.
     */
    private Integer copyNumber; // Unique number per book
    /**
     * The current status of the copy.
     * Possible values include "Available", "Borrowed", etc.
     */
    private String status; // "Available", "Borrowed", etc.

    /**
     * Default constructor for JPA.
     */
    public Copy() { }

    /**
     * Constructs a new Copy instance with the given details.
     *
     * @param book       the book this copy is associated with
     * @param copyNumber the unique number for this copy
     * @param status     the current status of the copy
     */
    public Copy(Book book, Integer copyNumber, String status) {
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = status;
    }

    //---------------------------------------------------------------------setters
    /**
     * Sets the book associated with this copy.
     *
     * @param book the {@link Book} entity to associate
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Sets the unique copy number for this copy.
     *
     * @param copyNumber the copy number to set
     */
    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    /**
     * Sets the current status of the copy.
     *
     * @param status the status to set (e.g., "Available", "Borrowed")
     */
    public void setStatus(String status) {
        this.status = status;
    }

    //---------------------------------------------------------------------getters
    /**
     * Gets the unique identifier of the copy.
     *
     * @return the ID of the copy
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the book associated with this copy.
     *
     * @return the {@link Book} entity linked to this copy
     */
    public Book getBook() {
        return book;
    }

    /**
     * Gets the current status of the copy.
     *
     * @return the status (e.g., "Available", "Borrowed")
     */
    public String getStatus() {
        return status;
    }
}

