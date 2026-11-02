package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import src.controller.AppController;
import src.model.Member;

/**
 * Collects a Student ID and delegates authentication to {@link AppController}.
 */
public class LoginScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Controller that owns authentication and member lookup. */
    private final AppController controller;
    /** Student ID value submitted for authentication. */
    private final JTextField studentIdField;
    /** Button that submits the entered Student ID. */
    private final JButton loginButton;
    /** Inline feedback for failed login attempts. */
    private final JLabel statusLabel;

    /**
     * Creates the login form.
     *
     * @param controller controller used to authenticate submitted IDs
     */
    public LoginScreen(AppController controller) {
        this.controller = controller;
        // A fixed, roomy layout keeps the main login controls easy to use on touch screens.
        setTitle("ULIMS Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setMinimumSize(new Dimension(480, 300));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new java.awt.Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("University Lab Inventory Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in with your Student ID", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 1;
        panel.add(subtitle, gbc);

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(idLabel, gbc);

        studentIdField = new JTextField();
        studentIdField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        studentIdField.setPreferredSize(new Dimension(250, 42));
        studentIdField.setToolTipText("Enter your Student ID");
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(180, 48));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.addActionListener(e -> attemptLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new java.awt.Color(168, 0, 0));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusLabel, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        studentIdField.addActionListener(e -> attemptLogin());
        // Keep construction separate from showing the window so callers control navigation.
        setVisible(false);
    }

    /**
     * Validates the entered ID through the controller and navigates only on success.
     */
    private void attemptLogin() {
        // Trim input once so whitespace does not affect validation or member lookup.
        String studentId = studentIdField.getText() == null ? "" : studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            showError("Student ID is required.");
            studentIdField.requestFocusInWindow();
            return;
        }

        try {
            Member member = controller.login(studentId);
            if (member == null) {
                showError("The Student ID is invalid or unauthorized.");
                studentIdField.requestFocusInWindow();
                return;
            }

            // Replace the login window only after the controller confirms authorization.
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(() -> new MainScreen(member, controller).setVisible(true));
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Displays a login failure in both the form and a dialog.
     *
     * @param message error text to display
     */
    private void showError(String message) {
        // Keep the error visible in the form and in a dialog so it is hard to miss.
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
