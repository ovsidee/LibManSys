package gui;

import dao.BookDao;
import dao.BorrowingDao;
import entity.Book;
import entity.Borrowing;
import entity.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GUI-based application for users.
 * This interface provides functionalities to get list of available titles.
 * View currently available book titles.
 * View their borrowing history.
 */
public class UserGUI extends JFrame {

    private final EntityManagerFactory emf;
    private final BookDao bookDao;
    private final BorrowingDao borrowingDao;

    private JTable booksTable;
    private JTable availableBooksTable;
    private JTable borrowingHistoryTable;

    /**
     * Constructs a {@code UserGUI} for a specified user.
     *
     * @param currentUser the user for whom this interface is created.
     */
    public UserGUI(User currentUser) {
        // Initialize EntityManagerFactory and DAOs
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        bookDao = new BookDao(emf);
        borrowingDao = new BorrowingDao(emf);

        // Set up the main window
        setTitle("Library Management System USER");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed panes
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add panels
        tabbedPane.addTab("All Titles", createBooksPanel());
        tabbedPane.addTab("Available Titles", createAvailableBooksPanel());
        tabbedPane.addTab("My Borrowing History", createBorrowingHistoryPanel(currentUser));

        add(tabbedPane);
    }

    /**
     * Creates the "All Titles" panel displaying all books in the library.
     *
     * @return a JPanel containing the table of all books.
     */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table (removed "ID" column)
        DefaultTableModel booksTableModel = new DefaultTableModel(
                new String[]{"Title", "Author", "Publisher", "Publication Year", "ISBN"}, 0
        );
        booksTable = new JTable(booksTableModel);
        JScrollPane tableScrollPane = new JScrollPane(booksTable);

        // Load data from database
        refreshBooksTable(booksTableModel);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the "Available Titles" panel displaying books with at least one available copy.
     *
     * @return a JPanel containing the table of available books.
     */
    private JPanel createAvailableBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table (removed "ID" column)
        DefaultTableModel availableBooksTableModel = new DefaultTableModel(
                new String[]{"Title", "Author", "Publisher", "Publication Year", "ISBN"}, 0
        );
        availableBooksTable = new JTable(availableBooksTableModel);
        JScrollPane tableScrollPane = new JScrollPane(availableBooksTable);

        // Load data from database
        refreshAvailableBooksTable(availableBooksTableModel);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the "My Borrowing History" panel displaying the borrowing history of the current user.
     *
     * @param currentUser the user whose borrowing history will be displayed.
     * @return a JPanel containing the table of the user's borrowing history.
     */
    private JPanel createBorrowingHistoryPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table (removed "ID" column)
        DefaultTableModel borrowingHistoryTableModel = new DefaultTableModel(
                new String[]{"Title", "Borrow Date", "Return Date"}, 0
        );
        borrowingHistoryTable = new JTable(borrowingHistoryTableModel);
        JScrollPane tableScrollPane = new JScrollPane(borrowingHistoryTable);

        // Load data from database
        refreshBorrowingHistoryTable(borrowingHistoryTableModel, currentUser);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Refreshes the table displaying all books.
     *
     * @param model the table model to be updated.
     */
    private void refreshBooksTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear the table
        List<Book> books = bookDao.findAll(); // Fetch all books
        for (Book book : books) {
            model.addRow(new Object[]{
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublicationYear(),
                    book.getIsbn()
            });
        }
    }

    /**
     * Refreshes the table displaying books with available copies.
     *
     * @param model the table model to be updated.
     */
    private void refreshAvailableBooksTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear the table
        List<Book> books = bookDao.findAll(); // Fetch all books
        for (Book book : books) {
            boolean isAvailable = book.getCopies().stream()
                    .anyMatch(copy -> "Available".equals(copy.getStatus())); // Check if any copy is available
            if (isAvailable) {
                model.addRow(new Object[]{
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getPublicationYear(),
                        book.getIsbn()
                });
            }
        }
    }

    /**
     * Refreshes the table displaying the borrowing history of the current user.
     *
     * @param model        the table model to be updated.
     * @param currentUser  the user whose borrowing history will be displayed.
     */
    private void refreshBorrowingHistoryTable(DefaultTableModel model, User currentUser) {
        model.setRowCount(0); // Clear the table
        List<Borrowing> borrowings = borrowingDao.findAll().stream()
                .filter(b -> b.getUser().getId().equals(currentUser.getId())) // Filter borrowings for the current user
                .collect(Collectors.toList());

        for (Borrowing borrowing : borrowings) {
            model.addRow(new Object[]{
                    borrowing.getCopy().getBook().getTitle(),
                    borrowing.getBorrowDate(),
                    borrowing.getReturnDate()
            });
        }
    }
}
