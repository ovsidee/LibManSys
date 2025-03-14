package entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a book in the library.
 * A book can have multiple copies and may be associated with a publisher.
 */
@Entity
@Table(name = "Books")
public class Book {

    /**
     * Unique identifier for the Book.
     * Auto-generated by the database.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    /**
     * Represents title of the Book.
     */
    private String title;
    /**
     * Represents author of the Book.
     */
    private String author;
    /**
     * Represents publisher of the Book.
     */
    private String publisher;

    /**
     * Represents publication year of the Book.
     */
    private int publicationYear;

    /**
     * The International Standard Book Number (ISBN) for this book.
     * Marked as unique in the database schema.
     */
    @Column(unique = true)
    private String isbn;

    /**
     * The list of physical copies of this book available in the library.
     * - **Relationship**: One-to-Many with the `Copy` entity.
     * - **Mapped By**: The `book` field in the `Copy` entity specifies the owning side of the relationship.
     * - **Cascade**: All persistence operations (e.g., persist, merge, remove) are cascaded to the associated `Copy` entities.
     * - **Orphan Removal**: Any `Copy` entity removed from this list will also be deleted from the database.
     * - **Fetch Type**: EAGER, meaning all copies are loaded immediately when the `Book` entity is fetched.
     */
    @OneToMany(mappedBy = "book",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.EAGER)
    private List<Copy> copies = new ArrayList<>();

    /**
     * The publisher associated with this book.
     */
     @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisherEntity;

    /**
     * Default constructor for JPA.
     */
    public Book() { }

    /**
     * Constructs a new Book with the given details.
     *
     * @param title           the title of the book
     * @param author          the author of the book
     * @param publisher       the name of the publisher
     * @param publicationYear the year the book was published
     * @param isbn            the unique ISBN of the book
     */
    public Book(String title, String author, String publisher, int publicationYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
    }

    //---------------------------------------------------------------------setters
    /**
     * Sets the unique identifier of the book.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the publisher entity for this book.
     *
     * @param publisherEntity the {@link Publisher} entity to set
     */
    public void setPublisherEntity(Publisher publisherEntity) {
        this.publisherEntity = publisherEntity;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Sets the name of the publisher.
     *
     * @param publisher the publisher's name to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Sets the publication year of the book.
     *
     * @param publicationYear the year to set
     */
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    //--------------------------------------------------------------------getters
    /**
     * Gets the list of copies of this book.
     *
     * @return the list of {@link Copy} entities associated with the book
     */
    public List<Copy> getCopies() {
        return copies;
    }

    /**
     * Gets the publisher entity associated with this book.
     *
     * @return the {@link Publisher} entity
     */
    public Publisher getPublisherEntity() {
        return publisherEntity;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Gets the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the name of the publisher.
     *
     * @return the publisher's name
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets the publication year of the book.
     *
     * @return the publication year
     */
    public int getPublicationYear() {
        return publicationYear;
    }

    /**
     * Gets the unique identifier of the book.
     *
     * @return the ID of the book
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }
}
