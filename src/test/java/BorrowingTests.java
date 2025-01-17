import dao.BookDao;
import entity.Book;
import org.junit.jupiter.api.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import dao.BorrowingDao;
import dao.UserDao;
import dao.CopyDao;
import entity.Borrowing;
import entity.User;
import entity.Copy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for {@link Borrowing} entity and its interactions with {@link User}, {@link Book}, and {@link Copy}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BorrowingTests {

    private static EntityManagerFactory emf;

    // dao
    private static BorrowingDao borrowingDao;
    private static UserDao userDao;
    private static CopyDao copyDao;
    private static BookDao bookDao;

    // id
    private static Long testBorrowingId;
    private static Long testUserId;
    private static Long testCopyId;
    private static Long testBookId;
    private static Long testBookIdForSecond;

    /**
     * Sets up the test environment by creating a user, book, and copy in the database.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        borrowingDao = new BorrowingDao(emf);
        userDao = new UserDao(emf);
        copyDao = new CopyDao(emf);
        bookDao = new BookDao(emf);

        // Create a user
        User user = new User("Test User", "user@test.com", "1234567890", "Test Address");
        user = userDao.create(user);
        assertNotNull(user);
        testUserId = user.getId();

        // Create a book
        Book book = new Book("Test Title", "Test Author", "Test Publisher", 2021, "ISBN123");
        book = bookDao.create(book);
        assertNotNull(book);
        testBookId = book.getId();

        // Create a copy
        Copy copy = new Copy(book, 1, "Available");
        copy = copyDao.create(copy);
        assertNotNull(copy);
        testCopyId = copy.getId();
    }

    /**
     * Cleans up resources after all tests are executed.
     */
    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    /**
     * Tests the creation of a borrowing for a user and a copy.
     */
    @Test
    @Order(1)
    void testCreateBorrowing() {
        User user = userDao.findById(testUserId);
        Copy copy = copyDao.findById(testCopyId);
        // Create and persist borrowing
        Borrowing borrowing = new Borrowing(user, copy, LocalDate.now(), null);
        borrowing = borrowingDao.create(borrowing);

        // Assert borrowing is persisted
        assertNotNull(borrowing);
        assertNotNull(borrowing.getId());
        testBorrowingId = borrowing.getId();

        // Update copy status
        copy.setStatus("Borrowed");
        copyDao.update(copy);
    }

    /**
     * Tests finding a borrowing by its ID.
     */
    @Test
    @Order(2)
    void testFindBorrowingById() {
        Borrowing borrowing = borrowingDao.findById(testBorrowingId);
        assertNotNull(borrowing);
        assertEquals(testBorrowingId, borrowing.getId());
    }

    /**
     * Tests updating a borrowing's return date.
     */
    @Test
    @Order(3)
    void testUpdateBorrowing() {
        Borrowing borrowing = borrowingDao.findById(testBorrowingId);
        assertNotNull(borrowing);

        // Update return date
        LocalDate returnDate = LocalDate.now().plusDays(7);
        borrowing.setReturnDate(returnDate);
        borrowingDao.update(borrowing);

        // Verify update
        Borrowing updatedBorrowing = borrowingDao.findById(testBorrowingId);
        assertEquals(returnDate, updatedBorrowing.getReturnDate());
    }

    /**
     * Tests creating multiple borrowings for the same user.
     */
    @Test
    @Order(4)
    void testUserWithMultipleBorrowings() {
        User user = userDao.findById(testUserId);

        // Create a new book for the second borrowing
        Book newBook = new Book("Second Book", "Second Author", "Second Publisher", 2023, "ISBN456");
        newBook = bookDao.create(newBook);
        assertNotNull(newBook, "New book should be created");
        testBookIdForSecond = newBook.getId();

        // Create a new copy for the new book
        Copy newCopy = new Copy(newBook, 1, "Available");
        newCopy = copyDao.create(newCopy);
        assertNotNull(newCopy, "New copy should be created");

        // Borrowing 2: Create and persist a new borrowing
        Borrowing secondBorrowing = new Borrowing(user, newCopy, LocalDate.now(), null);
        secondBorrowing = borrowingDao.create(secondBorrowing);
        assertNotNull(secondBorrowing, "Second borrowing should be created");

        // Update new copy status to "Borrowed"
        newCopy.setStatus("Borrowed");
        copyDao.update(newCopy);

        // Verify the user now has multiple borrowings
        user = userDao.findById(testUserId); // Reload user to fetch relationships
        assertEquals(2, user.getBorrowings().size(), "User should have two borrowings");
    }

    /**
     * Tests deleting a borrowing.
     */
    @Test
    @Order(5)
    void testDeleteBorrowing() {
        Borrowing borrowing = borrowingDao.findById(testBorrowingId);
        assertNotNull(borrowing);

        borrowingDao.delete(borrowing.getId());

        // Verify deletion
        Borrowing deletedBorrowing = borrowingDao.findById(testBorrowingId);
        assertNull(deletedBorrowing);
    }
}