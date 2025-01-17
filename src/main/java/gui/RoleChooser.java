package gui;

import dao.UserDao;
import entity.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import java.awt.*;

/**
 * The {@code RoleChooser} class provides a graphical interface for users to select their role in the library system.
 * Users can choose between:
 * Librarian via  {@link LibrarianGUI}.
 * User via {@link UserGUI}.
 */
public class RoleChooser extends JFrame {

    /**
     * Initializes the RoleChooser window and its components.
     * Provides buttons for selecting either "Librarian" or "User" mode.
     */
    public RoleChooser() {
        // Set up the RoleChooser window
        setTitle("Select Role");
        setSize(500, 180); // Adjust size for a more compact view
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create buttons for Librarian and User
        JButton librarianButton = new JButton("Librarian");
        JButton userButton = new JButton("User");

        // Set button sizes
        librarianButton.setPreferredSize(new Dimension(150, 100));
        userButton.setPreferredSize(new Dimension(150, 100));

        // Adjust font size for better appearance
        Font buttonFont = new Font("Arial", Font.PLAIN, 20);
        librarianButton.setFont(buttonFont);
        userButton.setFont(buttonFont);

        // Set up the layout
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)); // Center the buttons
        panel.add(librarianButton);
        panel.add(userButton);

        add(panel);

        // Add action listeners for buttons
        librarianButton.addActionListener(e -> {
            new DataBaseInsert(); // Populate the database (can be skipped if already populated)
            new LibrarianGUI().setVisible(true); // Launch the librarian GUI
            dispose(); // Close the RoleChooser
        });

        userButton.addActionListener(e -> {
            new DataBaseInsert();
            // Prompt the user for their ID
            String userIdInput = JOptionPane.showInputDialog(this, "Please enter your User ID:");
            if (userIdInput != null) {
                try {
                    Long userId = Long.parseLong(userIdInput); // Parse the ID
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibraryPU");
                    UserDao userDao = new UserDao(emf);

                    // Validate the user
                    User user = userDao.findById(userId);
                    if (user != null) {
                        new UserGUI(user).setVisible(true); // Launch the UserGUI with the user
                        dispose(); // Close the RoleChooser
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid ID. Please enter a numeric ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

}
