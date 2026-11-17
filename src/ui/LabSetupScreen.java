package src.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import src.controller.AppController;

// First-launch screen for R1.2-R1.4.
public class LabSetupScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    public LabSetupScreen(AppController controller) {
        setTitle("ULIMS - First-Time Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // R1.3: The system shall prompt an initial administrator ID when creating a new lab. 
        JPanel fields = new JPanel(new GridLayout(0, 1, 8, 8));
        fields.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        JLabel heading = new JLabel("Create Your Laboratory");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        JTextField labNameField = new JTextField();
        JTextField adminIdField = new JTextField();

        fields.add(heading);
        fields.add(new JLabel("Laboratory name:"));
        fields.add(labNameField);
        fields.add(new JLabel("Initial administrator Student ID:"));
        fields.add(adminIdField);

        JButton createButton = new JButton("Create Lab and Continue to Login");
        createButton.addActionListener(event -> {
            try {
                // This keeps file handling in one place and is easier to test.
                controller.initializeLab(
                    labNameField.getText(), adminIdField.getText()
                );

                // Only open login if BOTH the lab and administrator were saved.
                dispose();
                new LoginScreen(controller).setVisible(true);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                    this, ex.getMessage(), "Setup Failed", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        add(fields, BorderLayout.CENTER);
        add(createButton, BorderLayout.SOUTH);
    }
}
