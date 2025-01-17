import dao.BorrowingDao;
import dao.BookDao;
import dao.CopyDao;
import dao.UserDao;
import entity.Book;
import entity.Borrowing;
import entity.Copy;
import entity.User;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link User} entity, including CRUD operations and relationships with borrowings.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    private static EntityManagerFactory emf;
    private static UserDao userDao;
    private static BorrowingDao borrowingDao;
    private static BookDao bookDao;
    private static CopyDao copyDao;

    private static Long userId; // ID of the user for testing
    private static Long bookId; // ID of the book for borrowing test
    private static Long copyId; // ID of the copy for borrowing test
    private static Long borrowingId; // ID of the borrowing for cleanup

    /**
     * Initializes the test environment by creating the necessary DAOs.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        userDao = new UserDao(emf);
        borrowingDao = new BorrowingDao(emf);
        bookDao = new BookDao(emf);
        copyDao = new CopyDao(emf);
    }

    /**
     * Cleans up resources after all tests are executed.
     */
    @AfterAll
    static void tearDown() {
        emf.close();
    }

    /**
     * Tests the creation of a {@link User} entity.
     */
    @Order(1)
    @Test
    public void testCreateUser() {
        User user = new User("Alice", "alice@example.com", "1234567", "Wonderland");
        user = userDao.create(user);
        userId = user.getId();

        assertNotNull(user);
        assertNotNull(userId);
        assertEquals("Alice", user.getName());
    }

    /**
     * Tests updating a {@link User} entity's name.
     */
    @Order(2)
    @Test
    public void testUpdateUser() {
        User toUpdate = userDao.findById(userId);
        toUpdate.setName("Tomasz");
        userDao.update(toUpdate);

        User updated = userDao.findById(userId);
        assertEquals("Tomasz", updated.getName());
    }

    /**
     * Tests that deleting a {@link User} with existing borrowings fails.
     */
    @Test
    @Order(3)
    void testDeleteUserWithExistingBorrowingsShouldFail() {
        // Create a book and a copy
        Book book = new Book("Test Book", "Author", "Publisher", 2023, "ISBN12345");
        book = bookDao.create(book);
        bookId = book.getId();

        Copy copy = new Copy(book, 1, "Available");
        copy = copyDao.create(copy);
        copyId = copy.getId();

        // Create a borrowing
        User user = userDao.findById(userId);
        Borrowing borrowing = new Borrowing(user, copy, LocalDate.now(), null);
        borrowing = borrowingDao.create(borrowing);
        borrowingId = borrowing.getId();

        // Mark the copy as "Borrowed"
        copy.setStatus("Borrowed");
        copyDao.update(copy);

        // Attempt to delete the user and expect an exception
        assertThrows(IllegalStateException.class, () -> {
            userDao.delete(userId);
        });

        // Confirm the user still exists
        User stillThere = userDao.findById(userId);
        assertNotNull(stillThere, "User should still exist because delete should have failed");
    }

    /**
     * Tests the successful deletion of a {@link User} after resolving related borrowings.
     */
    @Order(4)
    @Test
    public void testDeleteUser() {
        // Delete the borrowing first
        Borrowing borrowing = borrowingDao.findById(borrowingId);
        borrowingDao.delete(borrowing.getId());

        // Mark the copy as "Available" after returning
        Copy copy = copyDao.findById(copyId);
        copy.setStatus("Available");
        copyDao.update(copy);

        // Now delete the user
        User toDelete = userDao.findById(userId);
        assertNotNull(toDelete);

        userDao.delete(toDelete.getId());
        assertNull(userDao.findById(toDelete.getId()), "User should be deleted");
    }
}