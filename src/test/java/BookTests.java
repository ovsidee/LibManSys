import dao.BookDao;
import dao.CopyDao;
import entity.Book;
import entity.Copy;
import org.junit.jupiter.api.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link Book} entity and its relationship with {@link Copy}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookTests {

    private static EntityManagerFactory emf;
    private static BookDao bookDao;
    private static CopyDao copyDao;
    private static Long testBookId;
    private static Long copy1Id;
    private static Long copy2Id;

    /**
     * Sets up the test environment by initializing the EntityManagerFactory and DAOs.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        bookDao = new BookDao(emf);
        copyDao = new CopyDao(emf);
    }

    /**
     * Cleans up resources after all tests.
     */
    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    /**
     * Tests the creation of a book and its associated copies.
     */
    @Test
    @Order(1)
    void testCreateBookWithCopies() {
        Book book = new Book("Test Title", "Test Author", "Test Publisher", 2021, "ISBN123");
        Book createdBook = bookDao.create(book);

        // Assert book is persisted
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        testBookId = createdBook.getId();

        // Create and persist copies
        Copy copy1 = new Copy(createdBook, 1, "Available");
        Copy copy2 = new Copy(createdBook, 2, "Available");
        copy1 = copyDao.create(copy1);
        copy2 = copyDao.create(copy2);

        copy1Id = copy1.getId();
        copy2Id = copy2.getId();

        // Verify the copies are correctly associated
        Book fetchedBook = bookDao.findById(testBookId);
        assertNotNull(fetchedBook);
        assertEquals(2, fetchedBook.getCopies().size(), "Copies count mismatch");
    }

    /**
     * Tests reading a book and verifying its details.
     */
    @Test
    @Order(2)
    void testReadBook() {
        Book foundBook = bookDao.findById(testBookId);
        assertNotNull(foundBook, "Book not found");
        assertEquals("Test Title", foundBook.getTitle(), "Book title mismatch");
        assertEquals(2, foundBook.getCopies().size(), "Copies count mismatch");
    }

    /**
     * Tests updating a book's title.
     */
    @Test
    @Order(3)
    void testUpdateBook() {
        Book foundBook = bookDao.findById(testBookId);
        assertNotNull(foundBook, "Book not found");

        foundBook.setTitle("Updated Title");
        Book updatedBook = bookDao.update(foundBook);

        assertEquals("Updated Title", updatedBook.getTitle(), "Book title not updated");
    }

    /**
     * Tests that attempting to delete a book with associated copies fails.
     */
    @Test
    @Order(4)
    void testDeleteBookWithExistingCopiesShouldFail() {
        // Attempt to delete the book while copies still exist
        // Because we haven't removed them, we expect an exception
        assertThrows(IllegalStateException.class, () -> {
            bookDao.delete(testBookId);
        });

        // Confirm the book is still in the database
        Book stillThere = bookDao.findById(testBookId);
        assertNotNull(stillThere, "Book should still exist because delete should have failed");
    }

    /**
     * Tests the successful deletion of a book after removing its associated copies.
     */
    @Test
    @Order(5)
    void testDeleteBook() {
        // First delete copies, so that "delete only if not in relationship" is satisfied:
        copyDao.delete(copy1Id);
        copyDao.delete(copy2Id);

        bookDao.delete(testBookId);
        Book foundBook = bookDao.findById(testBookId);
        assertNull(foundBook, "Book should be null after deletion");
    }
}