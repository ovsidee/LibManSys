import dao.BookDao;
import dao.CopyDao;
import entity.Book;
import entity.Copy;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test suite for {@link Copy} entity and its interactions with {@link Book}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CopiesTests {

    private static EntityManagerFactory emf;
    private static BookDao bookDao;
    private static CopyDao copyDao;
    private static Long bookId;
    private static Long copyId;

    /**
     * Sets up the test environment by creating a book for testing copies.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        bookDao = new BookDao(emf);
        copyDao = new CopyDao(emf);

        // Create a book for testing copies
        Book book = new Book("Copies Book", "Copies Author", "CopiesPub", 2020, "ISBN-COPIES");
        book = bookDao.create(book);
        bookId = book.getId();
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
     * Tests creating a new copy for a book.
     */
    @Test
    @Order(1)
    void testCreateCopy() {
        Book book = bookDao.findById(bookId);
        Copy copy = new Copy(book, 1, "Available");
        copy = copyDao.create(copy);
        Assertions.assertNotNull(copy.getId());
        copyId = copy.getId();
    }

    /**
     * Tests reading a copy from the database.
     */
    @Test
    @Order(2)
    void testReadCopy() {
        Copy c = copyDao.findById(copyId);
        Assertions.assertNotNull(c);
        Assertions.assertEquals("Available", c.getStatus());
    }

    /**
     * Tests updating a copy's status.
     */
    @Test
    @Order(3)
    void testUpdateCopy() {
        Copy c = copyDao.findById(copyId);
        c.setStatus("Withdrawn");
        copyDao.update(c);

        c = copyDao.findById(copyId);
        Assertions.assertEquals("Withdrawn", c.getStatus());
    }

    /**
     * Tests that multiple copies can be associated with the same book.
     */
    @Test
    @Order(4)
    void testMultipleCopiesForSameBook() {
        // Book can have multiple copies
        Book b = bookDao.findById(bookId);

        Copy c1 = new Copy(b, 2, "Available");
        Copy c2 = new Copy(b, 3, "Available");
        copyDao.create(c1);
        copyDao.create(c2);

        // Confirm the book has multiple copies
        Book reloaded = bookDao.findById(bookId);
        Assertions.assertTrue(reloaded.getCopies().size() >= 3);
    }

    /**
     * Tests that a borrowed copy cannot be deleted.
     */
    @Test
    @Order(5)
    void testDeleteBorrowedCopyShouldFail() {
        // Create a new copy that is marked "Borrowed"
        Book b = bookDao.findById(bookId);
        Copy borrowedCopy = new Copy(b, 4, "Borrowed");
        borrowedCopy = copyDao.create(borrowedCopy);

        Long borrowedCopyId = borrowedCopy.getId();

        // Attempt to delete -> expect an exception because status = "Borrowed"
        assertThrows(IllegalStateException.class, () -> {
            copyDao.delete(borrowedCopyId);
        });

        // Confirm the copy was NOT deleted
        Copy stillThere = copyDao.findById(borrowedCopyId);
        Assertions.assertNotNull(stillThere, "Borrowed copy should still exist, deletion blocked");
    }

    /**
     * Tests deleting a copy that is not borrowed.
     */
    @Test
    @Order(6)
    public void testDeleteCopy() {
        // Delete the first copy we created
        copyDao.delete(copyId);

        // Verify the entity is deleted
        EntityManager em = emf.createEntityManager();
        try {
            em.clear(); // Clear the persistence context
            Copy deleted = em.find(Copy.class, copyId);
            Assertions.assertNull(deleted, "Copy should be null after deletion.");
        } finally {
            em.close();
        }
    }
}
