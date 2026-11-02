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
import javax.swing.JTextField;

import src.controller.AppController;
import src.model.Lab;
import src.storage.LabFileManager;

/**
 * Collects and saves the laboratory name before transitioning to login.
 */
public class LabSetupScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the setup form.
     *
     * @param controller controller passed to the login screen after setup
     */
    public LabSetupScreen(AppController controller) {
        // Collect the lab's top-level identity before sending the user to authentication.
        setTitle("Lab Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 280);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Initial Laboratory Setup");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        main.add(title, gbc);

        JLabel nameLabel = new JLabel("Lab Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        main.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(240, 38));
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridx = 1;
        main.add(nameField, gbc);

        JButton continueButton = new JButton("Continue to Login");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        continueButton.setPreferredSize(new Dimension(200, 48));
        continueButton.addActionListener(e -> {
            // Do not continue with a blank name, because the saved lab identifies the inventory.
            String labName = nameField.getText() == null ? "" : nameField.getText().trim();
            if (labName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a lab name.", "Setup Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Lab lab = new Lab(labName);
            // Only show login after setup is saved so the next launch can restore the lab.
            if (LabFileManager.saveLab(lab)) {
                dispose();
                new LoginScreen(controller).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "The lab configuration could not be saved.", "Setup Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        main.add(continueButton, gbc);

        add(main, BorderLayout.CENTER);
    }
}
