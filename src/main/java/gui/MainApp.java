package gui;

import javax.swing.*;

/**
 * The main entry point for the library management system application.
 * This class initializes the Java Swing application and launches the {@link RoleChooser} window,
 * allowing the user to choose their role (e.g., Librarian).
 */
public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoleChooser roleChooser = new RoleChooser();
            roleChooser.setVisible(true);
        });
    }
}
