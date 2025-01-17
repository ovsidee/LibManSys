package gui;

import dao.*;
import entity.Book;
import entity.Copy;
import entity.User;
import entity.Borrowing;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * A GUI-based application for librarians to manage the library system.
 * This interface provides functionalities to manage books, users, and borrowings.
 * Librarians can add, edit, delete, and view records using this system.
 */
public class LibrarianGUI extends JFrame {

    private final EntityManagerFactory emf;
    private final BookDao bookDao;
    private final UserDao userDao;
    private final CopyDao copyDao;
    private final BorrowingDao borrowingDao;

    private JTable booksTable;

    /**
     * Constructs the LibrarianGUI and initializes the database connection and DAOs.
     */
    public LibrarianGUI() {
        // Initialize EntityManagerFactory and DAOs
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        bookDao = new BookDao(emf);
        userDao = new UserDao(emf);
        copyDao = new CopyDao(emf);
        borrowingDao = new BorrowingDao(emf);

        // Set up the main window
        setTitle("Library Management System LIBRARIAN");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed panes
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add panels
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Borrowings", createBorrowingsPanel());

        add(tabbedPane);
    }

    /**
     * Creates a panel for managing books.
     * This panel includes functionalities for adding, editing, and deleting books.
     *
     * @return a JPanel for book management
     */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table
        DefaultTableModel booksTableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Author", "ISBN", "Publisher", "Publication Year", "Total Copies", "Available Copies"}, 0
        );


        booksTable = new JTable(booksTableModel);
        JScrollPane tableScrollPane = new JScrollPane(booksTable);

        // Load data from the database
        refreshBooksTable(booksTableModel);

        // Buttons for book actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBookButton = new JButton("Add Book");
        JButton editBookButton = new JButton("Edit Book");
        JButton deleteBookButton = new JButton("Delete Book");

        actionPanel.add(addBookButton);
        actionPanel.add(editBookButton);
        actionPanel.add(deleteBookButton);

        // Add action listeners for book buttons
        addBookButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Enter Book Title:");
            String author = JOptionPane.showInputDialog(this, "Enter Author:");
            String isbn = JOptionPane.showInputDialog(this, "Enter ISBN:");
            String publisher = JOptionPane.showInputDialog(this, "Enter Publisher:");
            String publicationYearStr = JOptionPane.showInputDialog(this, "Enter Publication Year:");
            String numCopiesStr = JOptionPane.showInputDialog(this, "Enter Number of Copies:");

            if (title != null && author != null && isbn != null && publisher != null && publicationYearStr != null && numCopiesStr != null) {
                try {
                    int publicationYear = Integer.parseInt(publicationYearStr); // Validate year as integer
                    int numCopies = Integer.parseInt(numCopiesStr); // Validate number of copies as integer

                    if (numCopies <= 0) {
                        JOptionPane.showMessageDialog(this, "Number of copies must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create the book entity
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setIsbn(isbn); // Set ISBN
                    book.setPublisher(publisher); // Set Publisher
                    book.setPublicationYear(publicationYear); // Set Publication Year

                    // Save the book in the database
                    bookDao.create(book);

                    // Create and save the specified number of copies
                    for (int i = 1; i <= numCopies; i++) {
                        Copy copy = new Copy();
                        copy.setBook(book); // Associate the copy with the book
                        copy.setCopyNumber(i); // Assign a unique copy number
                        copy.setStatus("Available"); // Default status for new copies
                        copyDao.create(copy); // Save the copy in the database
                    }

                    refreshBooksTable(booksTableModel); // Refresh the books table to reflect changes
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers for publication year and number of copies.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editBookButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long bookId = Long.parseLong(booksTableModel.getValueAt(selectedRow, 0).toString());
                Book book = bookDao.findById(bookId);
                if (book != null) {
                    String newTitle = JOptionPane.showInputDialog(this, "Edit Title:", book.getTitle());
                    String newAuthor = JOptionPane.showInputDialog(this, "Edit Author:", book.getAuthor());
                    if (newTitle != null && newAuthor != null) {
                        book.setTitle(newTitle);
                        book.setAuthor(newAuthor);
                        bookDao.update(book); // Update the book in the database
                        refreshBooksTable(booksTableModel); // Refresh the table to reflect changes
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to edit.");
            }
        });

        deleteBookButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long bookId = Long.parseLong(booksTableModel.getValueAt(selectedRow, 0).toString());
                try {
                    bookDao.delete(bookId);
                    refreshBooksTable(booksTableModel); // Refresh the table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            }
        });

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a panel for managing users.
     * This panel includes functionalities for adding, editing, and deleting users.
     *
     * @return a JPanel for user management
     */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table
        DefaultTableModel usersTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0);
        JTable usersTable = new JTable(usersTableModel);
        JScrollPane tableScrollPane = new JScrollPane(usersTable);

        // Load data from database
        refreshUsersTable(usersTableModel);

        // Buttons for user actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserButton = new JButton("Add User");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");

        actionPanel.add(addUserButton);
        actionPanel.add(editUserButton);
        actionPanel.add(deleteUserButton);

        // Add user button action
        addUserButton.addActionListener(e -> {
            try {
                // Get user inputs
                String name = JOptionPane.showInputDialog(this, "Enter User Name:");
                String email = JOptionPane.showInputDialog(this, "Enter Email:");
                String phoneInput = JOptionPane.showInputDialog(this, "Enter Phone:");

                //check number
                for (int i = 0; i <phoneInput.length(); i++) {
                    if (!Character.isDigit(phoneInput.charAt(i))) {
                        JOptionPane.showMessageDialog(this, "Phone number must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String address = JOptionPane.showInputDialog(this, "Enter Address:");

                // Validate inputs
                if (name == null || name.isEmpty() || email == null || email.isEmpty() || phoneInput == null || phoneInput.isEmpty() || address == null || address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create the user and save to the database
                User user = new User(name, email, phoneInput, address);
                userDao.create(user); // Save the user
                refreshUsersTable(usersTableModel); // Refresh the table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit user button action
        editUserButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long userId = Long.parseLong(usersTableModel.getValueAt(selectedRow, 0).toString());
                User user = userDao.findById(userId);
                if (user != null) {
                    String newName = JOptionPane.showInputDialog(this, "Edit Name:", user.getName());
                    String newEmail = JOptionPane.showInputDialog(this, "Edit Email:", user.getEmail());
                    String newPhone = JOptionPane.showInputDialog(this, "Edit Phone:", user.getPhoneNumber());
                    String newAddress = JOptionPane.showInputDialog(this, "Edit Address:", user.getAddress());
                    if (newName != null && newEmail != null && newPhone != null && newAddress != null) {
                        user.setName(newName);
                        user.setEmail(newEmail);
                        user.setPhoneNumber(newPhone);
                        user.setAddress(newAddress); // Update the address
                        userDao.update(user); // Save the changes
                        refreshUsersTable(usersTableModel); // Refresh the table
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            }
        });


        // Delete user button action
        deleteUserButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long userId = Long.parseLong(usersTableModel.getValueAt(selectedRow, 0).toString());
                try {
                    userDao.delete(userId);
                    refreshUsersTable(usersTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Cannot delete user: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            }
        });

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a panel for managing borrowings.
     * This panel includes functionalities for adding, editing, and deleting borrowings.
     *
     * @return a JPanel for borrowing management
     */
    private JPanel createBorrowingsPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Table model and table
        DefaultTableModel borrowingsTableModel = new DefaultTableModel(new String[]{"ID", "User", "Book", "Borrow Date", "Return Date"}, 0);
        JTable borrowingsTable = new JTable(borrowingsTableModel);
        JScrollPane tableScrollPane = new JScrollPane(borrowingsTable);

        // Load data from the database
        refreshBorrowingsTable(borrowingsTableModel);

        // Buttons for borrowing actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBorrowingButton = new JButton("Add Borrowing");
        JButton editBorrowingButton = new JButton("Edit Borrowing");
        JButton deleteBorrowingButton = new JButton("Delete Borrowing");

        actionPanel.add(addBorrowingButton);
        actionPanel.add(editBorrowingButton);
        actionPanel.add(deleteBorrowingButton);

        // Add borrowing button action
        addBorrowingButton.addActionListener(e -> {
            try {
                // Get user ID
                String userIdStr = JOptionPane.showInputDialog(this, "Enter User ID:");
                Long userId = Long.parseLong(userIdStr);

                // Get book ID
                String bookIdStr = JOptionPane.showInputDialog(this, "Enter Book ID:");
                Long bookId = Long.parseLong(bookIdStr);

                // Validate user and book
                User user = userDao.findById(userId);
                Book book = bookDao.findById(bookId);

                if (user == null) {
                    JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (book == null) {
                    JOptionPane.showMessageDialog(this, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Find an available copy
                Copy availableCopy = book.getCopies().stream()
                        .filter(copy -> "Available".equals(copy.getStatus()))
                        .findFirst()
                        .orElse(null);

                if (availableCopy == null) {
                    JOptionPane.showMessageDialog(this, "No available copies for this book.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get borrowing date
                String borrowDateStr = JOptionPane.showInputDialog(this, "Enter Borrowing Date (YYYY-MM-DD):");
                java.time.LocalDate borrowDate = java.time.LocalDate.parse(borrowDateStr);

                // Get optional return date
                String returnDateStr = JOptionPane.showInputDialog(this, "Enter Return Date (YYYY-MM-DD, optional):");
                java.time.LocalDate returnDate = (returnDateStr != null && !returnDateStr.isEmpty())
                        ? java.time.LocalDate.parse(returnDateStr)
                        : null;

                // Create borrowing
                Borrowing borrowing = new Borrowing(user, availableCopy, borrowDate, returnDate);
                borrowingDao.create(borrowing);

                // Update the copy's status to "Borrowed"
                availableCopy.setStatus("Borrowed");
                copyDao.update(availableCopy);

                refreshBorrowingsTable((DefaultTableModel) borrowingsTable.getModel()); // Refresh the borrowings table
                refreshBooksTable((DefaultTableModel) booksTable.getModel()); // Refresh the books table
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a numeric ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding borrowing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Edit borrowing button action
        editBorrowingButton.addActionListener(e -> {
            int selectedRow = borrowingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    Long borrowingId = Long.parseLong(borrowingsTableModel.getValueAt(selectedRow, 0).toString());
                    Borrowing borrowing = borrowingDao.findById(borrowingId);

                    if (borrowing == null) {
                        JOptionPane.showMessageDialog(this, "Borrowing not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String newReturnDate = JOptionPane.showInputDialog(this, "Enter Return Date (YYYY-MM-DD):", borrowing.getReturnDate());
                    if (newReturnDate != null) {
                        borrowing.setReturnDate(java.time.LocalDate.parse(newReturnDate));

                        // Mark the copy as available if returned
                        if (borrowing.getReturnDate() != null) {
                            Copy copy = borrowing.getCopy();
                            copy.setStatus("Available");
                            copyDao.update(copy);
                        }

                        borrowingDao.update(borrowing);
                        refreshBorrowingsTable(borrowingsTableModel);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error editing borrowing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a borrowing to edit.");
            }
        });

        // Delete borrowing button action
        deleteBorrowingButton.addActionListener(e -> {
            int selectedRow = borrowingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long borrowingId = Long.parseLong(borrowingsTableModel.getValueAt(selectedRow, 0).toString());
                try {
                    Borrowing borrowing = borrowingDao.findById(borrowingId);

                    if (borrowing != null) {
                        // Confirm deletion
                        int confirmation = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this borrowing?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            borrowingDao.delete(borrowingId); // Delete the borrowing
                            refreshBorrowingsTable(borrowingsTableModel); // Refresh the borrowings table
                            refreshBooksTable((DefaultTableModel) booksTable.getModel()); // Refresh the books table
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Borrowing not found.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting borrowing: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a borrowing to delete.");
            }
        });

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Refreshes the books table with data from the database.
     *
     * @param model the table model to refresh
     */
    private void refreshBooksTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Book> books = bookDao.findAll();
        for (Book book : books) {
            int totalCopies = book.getCopies().size(); // Count total copies
            long availableCopies = book.getCopies().stream().filter(copy -> "Available".equals(copy.getStatus())).count(); // Count available copies
            model.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublisher(),
                    book.getPublicationYear(),
                    totalCopies,
                    availableCopies // Display available copies as an additional column
            });
        }
    }

    /**
     * Refreshes the users table with data from the database.
     *
     * @param model the table model to refresh
     */
    private void refreshUsersTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = userDao.findAll();
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress() // Display the address
            });
        }
    }

    /**
     * Refreshes the borrowings table with data from the database.
     *
     * @param model the table model to refresh
     */
    private void refreshBorrowingsTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Borrowing> borrowings = borrowingDao.findAll();
        for (Borrowing borrowing : borrowings) {
            model.addRow(new Object[]{borrowing.getId(), borrowing.getUser().getName(), borrowing.getCopy().getBook().getTitle(), borrowing.getBorrowDate(), borrowing.getReturnDate()});
        }
    }
}