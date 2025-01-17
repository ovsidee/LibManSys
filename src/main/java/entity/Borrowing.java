package entity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Represents a record that a user borrowed a particular copy of a book.
 * Each borrowing links a user to a copy of a book with associated dates for borrowing and returning.
 * The user who borrowed the book.
 * Many borrowings can reference one user.
 * The date when the book was returned.
 * Can be null if the book has not been returned yet.
 */
@Entity
@Table(name = "Borrowings")
public class Borrowing {

    /**
     * Unique identifier for the Borrowing.
     * Auto-generated by the database.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * The user who borrowed the book.
     * Many borrowings can reference one user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     *  Many borrowings can reference one copy
     */
    @ManyToOne
    @JoinColumn(name = "copy_id")
    private Copy copy;
    /**
     * Represents borrow date of Borrowing.
     */
    private LocalDate borrowDate;
    /**
     * Represents return date of Borrowing.
     * Can be *null*
     */
    private LocalDate returnDate;

    /**
     * Default constructor for JPA.
     */
    public Borrowing() {
    }

    /**
     * Constructs a new Borrowing instance with the given details.
     *
     * @param user       the user who borrowed the book
     * @param copy       the copy of the book being borrowed
     * @param borrowDate the date the book was borrowed
     * @param returnDate the date the book was returned (nullable)
     */
    public Borrowing(User user, Copy copy, LocalDate borrowDate, LocalDate returnDate) {
        this.user = user;
        this.copy = copy;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    //---------------------------------------------------------------------setters
    /**
     * Sets the copy of the book being borrowed.
     *
     * @param copy the {@link Copy} to set
     */
    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    /**
     * Sets the date the book was returned.
     *
     * @param returnDate the return date to set
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Sets the unique identifier for the borrowing.
     *
     * @param nextAvailableId the ID to set
     */
    public void setId(Long nextAvailableId) {
        this.id = nextAvailableId;
    }

    /**
     * Sets the user who borrowed the book.
     *
     * @param user the {@link User} to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    //---------------------------------------------------------------------getters
    /**
     * Gets the date the book was borrowed.
     *
     * @return the borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Gets the unique identifier of the borrowing.
     *
     * @return the ID of the borrowing
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the user who borrowed the book.
     *
     * @return the {@link User} associated with the borrowing
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the copy of the book being borrowed.
     *
     * @return the {@link Copy} associated with the borrowing
     */
    public Copy getCopy() {
        return copy;
    }

    /**
     * Gets the date the book was returned.
     *
     * @return the return date, or null if the book has not been returned
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }
}
