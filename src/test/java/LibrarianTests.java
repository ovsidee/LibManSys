import dao.LibrarianDao;
import dao.UserDao;
import entity.Librarian;
import entity.User;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

/**
 * Test suite for {@link Librarian} entity and its interaction with {@link User}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibrarianTests {

    private static EntityManagerFactory emf;
    private static UserDao userDao;
    private static LibrarianDao librarianDao;
    private static Long librarianId;

    /**
     * Sets up the test environment, initializing the DAO objects.
     */
    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        userDao = new UserDao(emf);
        librarianDao = new LibrarianDao(emf);
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
     * Tests the creation of a librarian entity linked to a user.
     */
    @Test
    @Order(1)
    void testCreateLibrarian() {
        // Create a user, then attach librarian
        User user = new User("LibUser", "libuser@example.com", "999999", "Librarian Street");
        userDao.create(user);

        Librarian librarian = new Librarian(user, LocalDate.now(), "Head Librarian");
        librarian = librarianDao.create(librarian);

        Assertions.assertNotNull(librarian.getId());
        librarianId = librarian.getId();
    }
    /**
     * Tests reading a librarian entity from the database.
     */
    @Test
    @Order(2)
    void testReadLibrarian() {
        Librarian l = librarianDao.findById(librarianId);
        Assertions.assertNotNull(l);
        Assertions.assertNotNull(l.getUser());
    }

    /**
     * Tests updating a librarian's position field.
     */
    @Test
    @Order(3)
    void testUpdateLibrarian() {
        Librarian l = librarianDao.findById(librarianId);
        l.setPosition("Assistant Librarian");
        librarianDao.update(l);

        Librarian updated = librarianDao.findById(librarianId);
        Assertions.assertEquals("Assistant Librarian", updated.getPosition());
    }

    /**
     * Tests deleting a librarian entity from the database.
     */
    @Test
    @Order(4)
    void testDeleteLibrarian() {
        librarianDao.delete(librarianId);
        Librarian deleted = librarianDao.findById(librarianId);
        Assertions.assertNull(deleted);
    }
}
