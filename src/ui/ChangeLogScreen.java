package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Displays the current placeholder change-log message.
 */
public class ChangeLogScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Creates a read-only change-log window. */
    public ChangeLogScreen() {
        // Keep the log read-only so this screen cannot alter audit history.
        setTitle("Change Log");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setText(
            "ULIMS Change Log\n" +
            "----------------\n" +
            "No inventory changes have been logged yet.\n" +
            "This screen is available to authorized administrators."
        );

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(560, 360));
        add(panel);
    }
}
