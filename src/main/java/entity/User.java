package entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an application user in the library system.
 * This entity can represent both non-librarians and librarians (via the `Librarian` entity).
 * Users can borrow copies of books, which are tracked through the `Borrowing` entity.
 */
@Entity
@Table(name = "Users")
public class User {
    /**
     * The unique identifier for the user.
     * Generated automatically by database h2.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The unique email address of the user.
     */
    @Column(unique = true)
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The address of the user.
     */
    private String address;

    /**
     * A list of borrowings associated with this user.
     * - **Relationship**: One-to-Many with the `Borrowing` entity.
     * - **Mapped By**: The `user` field in the `Borrowing` entity.
     * - **Fetch Type**: EAGER, meaning all borrowings will be fetched immediately when the user is loaded.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Borrowing> borrowings = new ArrayList<>();

    /**
     * Default constructor for JPA.
     */
    public User() {}

    /**
     * Constructs a new user with the specified attributes.
     *
     * @param name        the name of the user
     * @param email       the unique email address of the user
     * @param phoneNumber the user's phone number
     * @param address     the address of the user
     */
    public User(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    //---------------------------------------------------------------------setters
    /**
     * Sets the name of the user.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the address of the user.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //---------------------------------------------------------------------getters
    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return the ID of the user
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the email of the user.
     *
     * @return the unique email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the list of borrowings associated with the user.
     *
     * @return a list of `Borrowing` entities associated with this user
     */
    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    /**
     * Gets the address of the user.
     *
     * @return the address of the user
     */
    public String getAddress() {
        return address;
    }
}