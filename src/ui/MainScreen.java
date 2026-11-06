package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import src.controller.AppController;
import src.model.Member;

/**
 * Temporary post-login screen showing the authenticated ID, role, and logout action.
 */
public class MainScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the temporary authenticated screen.
     *
     * @param member member returned by successful authentication
     * @param controller controller used to display the member's role and handle re-login
     */
    public MainScreen(Member member, AppController controller) {
        // This placeholder confirms the authenticated identity and permission level.
        setTitle("ULIMS - Temporary Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 240);
        setMinimumSize(new Dimension(560, 200));
        setLocationRelativeTo(null);

        String permission = controller.hasAdminPermission(member)
            ? "admin permissions"
            : "user permissions";
        JLabel message = new JLabel(
            "Temp screen - Logged into \"" + member.getStudentID() + "\" as \"" + permission + "\"",
            SwingConstants.CENTER
        );
        message.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(message, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(150, 48));
        logoutButton.addActionListener(event -> {
            // Dispose the authenticated screen before returning to a fresh login form.
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen(controller).setVisible(true));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
