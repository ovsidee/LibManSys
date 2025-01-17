import dao.BookDao;
import dao.PublisherDao;
import entity.Book;
import entity.Publisher;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test suite for {@link Publisher} entity and its interaction with {@link Book}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublisherTests {

    private static EntityManagerFactory emf;
    private static PublisherDao publisherDao;
    private static BookDao bookDao;

    private static Long publisherId;
    private static Long bookId;

    /**
     * Sets up the test environment, initializing the DAO objects.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        publisherDao = new PublisherDao(emf);
        bookDao = new BookDao(emf);
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
     * Tests the creation of a publisher entity.
     */
    @Test
    @Order(1)
    void testCreatePublisher() {
        Publisher publisher = new Publisher("TestPub Inc", "123 Pub St", "123-123-1234");
        publisher = publisherDao.create(publisher);
        Assertions.assertNotNull(publisher.getId());
        publisherId = publisher.getId();
    }

    /**
     * Tests associating a book with a publisher.
     */
    @Test
    @Order(2)
    void testAssociateBookWithPublisher() {
        Publisher publisher = publisherDao.findById(publisherId);
        Book book = new Book("PubLinked Book", "Pub Author", "RedundantPublisherField", 2023, "PUB-BOOK-1");
        book.setPublisherEntity(publisher);
        book = bookDao.create(book);
        bookId = book.getId();

        // Confirm Many-to-One: each Book can only have one Publisher
        Book reloaded = bookDao.findById(bookId);
        Assertions.assertEquals(publisherId, reloaded.getPublisherEntity().getId());
    }

    /**
     * Tests that deleting a publisher with associated books fails.
     */
    @Test
    @Order(3)
    void testDeletePublisherWithReferencedBooksShouldFail() {
        // The publisher from testCreatePublisher() is still referenced by the Book from testAssociateBookWithPublisher()
        // Attempt to delete it now -> expect an exception
        assertThrows(IllegalStateException.class, () -> {
            publisherDao.delete(publisherId);
        });

        // Confirm it's still there
        Publisher stillThere = publisherDao.findById(publisherId);
        Assertions.assertNotNull(stillThere);
    }

    /**
     * Tests changing the publisher of an existing book.
     */
    @Test
    @Order(4)
    void testPublisherChange() {
        // Change the publisher on the existing book
        Publisher newPub = new Publisher("NewPub Inc", "999 Another St", "999-999-999");
        newPub = publisherDao.create(newPub);
        Book book = bookDao.findById(bookId);
        book.setPublisherEntity(newPub);
        bookDao.update(book);

        Book updated = bookDao.findById(bookId);
        Assertions.assertEquals("NewPub Inc", updated.getPublisherEntity().getName());
    }

    /**
     * Tests deleting a publisher after its associated books are reassigned.
     */
    @Test
    @Order(5)
    void testDeletePublisher() {
        // The original publisher should now have no referencing books
        publisherDao.delete(publisherId);
        Publisher deleted = publisherDao.findById(publisherId);
        Assertions.assertNull(deleted, "Publisher can be deleted if no books reference it");
    }
}
