package gui;

import dao.*;
import entity.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

/**
 * Initializes and inserts data into the database to populate tables with sample data.
 * This class provides pre-defined values for `User`, `Publisher`, `Book`, `Copy`, `Borrowing`,
 * and `Librarian` entities to ensure that the database is not empty when started.
 */
public class DataBaseInsert {

    /**
     * Constructor that initializes the database with sample data.
     */
    public DataBaseInsert() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibraryPU");

        UserDao userDao = new UserDao(emf);
        BookDao bookDao = new BookDao(emf);
        PublisherDao publisherDao = new PublisherDao(emf);
        CopyDao copyDao = new CopyDao(emf);
        BorrowingDao borrowingDao = new BorrowingDao(emf);
        LibrarianDao librarianDao = new LibrarianDao(emf);

        try {
            // Insert Users
            User user1 = new User("Vitalii", "s31719@pjwstk.edu.pl", "575 422 555", "Zlote Terasy");
            User user2 = new User("Artem", "artem.gatsuta@gmail.com", "095 911 40 26", "Apollo");
            User user3 = new User("Slava", "slava.larin@gmail.com", "050 058 04 55", "Dublin");
            userDao.create(user1);
            userDao.create(user2);
            userDao.create(user3);

            // Insert Publishers
            Publisher publisher1 = new Publisher("Penguin Books", "123 Book St", "123-456-789");
            Publisher publisher2 = new Publisher("HarperCollins", "456 Novel Ave", "987-654-321");
            publisherDao.create(publisher1);
            publisherDao.create(publisher2);

            // Insert Books and set their publishers
            Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Penguin Books", 1925, "9780141182636");
            Book book2 = new Book("1984", "George Orwell", "Penguin Books", 1949, "9780141036144");
            Book book3 = new Book("To Kill a Mockingbird", "Harper Lee", "HarperCollins", 1960, "9780060935467");

            book1.setPublisherEntity(publisher1);
            book2.setPublisherEntity(publisher1);
            book3.setPublisherEntity(publisher2);

            bookDao.create(book1);
            bookDao.create(book2);
            bookDao.create(book3);

            // Insert Copies for each book
            Copy copy1 = new Copy(book1, 1, "Available");
            Copy copy2 = new Copy(book2, 1, "Available");
            Copy copy3 = new Copy(book3, 1, "Available");
            Copy copy4 = new Copy(book3, 2, "Available");
            copyDao.create(copy1);
            copyDao.create(copy2);
            copyDao.create(copy3);
            copyDao.create(copy4);

            // Insert Borrowings and set the borrowed status for copies
            Borrowing borrowing1 = new Borrowing(user1, copy1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
            Borrowing borrowing2 = new Borrowing(user2, copy3, LocalDate.of(2024, 1, 5), null); // Not yet returned
            borrowingDao.create(borrowing1);
            borrowingDao.create(borrowing2);

            // Update the copy status for borrowed copies
            copy1.setStatus("Borrowed");
            copy3.setStatus("Borrowed");
            copyDao.update(copy1);
            copyDao.update(copy3);

            // Insert Librarians linked to existing users
            Librarian librarian1 = new Librarian(user3, LocalDate.of(2023, 5, 10), "Senior Librarian");
            librarianDao.create(librarian1);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while inserting initial data: " + e.getMessage());
        } finally {
            emf.close();
        }
    }
}
