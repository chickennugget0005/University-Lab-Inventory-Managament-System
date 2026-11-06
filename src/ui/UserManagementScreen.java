package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import src.controller.AppController;
import src.model.Member;

/**
 * Displays the member IDs and roles provided by the controller.
 */
public class UserManagementScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a read-only list of the controller's current members.
     *
     * @param controller source of the member list
     */
    public UserManagementScreen(AppController controller) {
        // Present controller-provided members without letting the UI read the database file.
        setTitle("User Management");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel<String> model = new DefaultListModel<>();
        List<Member> members = controller.getMembers();
        for (Member member : members) {
            // Include the role beside each ID so administrators can quickly distinguish access levels.
            model.addElement(member.getStudentID() + " - " + member.getName() + (member.isAdmin() ? " (Admin)" : " (User)"));
        }

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(460, 360));
        add(panel);
    }
}
