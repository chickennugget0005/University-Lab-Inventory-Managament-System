package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import src.controller.AppController;
import src.model.ChangeLogEntry;
import src.model.Member;


// Displays the locally stored change log in a read-only Swing window.

// R8.10: The system shall record successful user management changes in the system's change log changes, including the acting Student ID and the time of the action.
public class ChangeLogScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    // Opens the log for a logged-in administrator.
    // The controller enforces permissions before any records are displayed.
    public ChangeLogScreen(AppController controller, Member administrator) {
        // R8.1: The system shall display user management functions only to authorized Administrators.
        controller.requireAdmin(administrator);

        setTitle("Change Log");
        setSize(650, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        StringBuilder message = new StringBuilder(
            "ULIMS Change Log\n" + "----------------\n"
        );

        try {
            // Fetch records through AppController rather than reading files in the GUI.
            java.util.List<ChangeLogEntry> entries = controller.getChangeLogEntries(administrator);
            if (entries.isEmpty()) {
                message.append("No logged changes yet.\n");
            } else {
                for (ChangeLogEntry entry : entries) {
                    message.append(entry.toString()).append("\n");
                }
            }
        } catch (IllegalStateException ex) {
            message.append("Unable to read the change log: ").append(ex.getMessage());
        }

        area.setText(message.toString());
        area.setCaretPosition(0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(610, 360));
        add(panel);
    }
}
