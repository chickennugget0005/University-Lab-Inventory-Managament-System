package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import src.controller.AppController;
import src.model.Member;


//Administrator-only Swing interface for UC8 (add/remove authorized users) and UC9 (promote/demote users). Business rules remain in the model/controller.
//After every successful edit, the shown list is rebuilt from the controller so the interface reflects the saved states for the role and authorizations.
public class UserManagementScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    private final AppController controller;
    private final Member administrator;
    private final DefaultListModel<String> listModel;
    private final JList<String> userList;
    private List<Member> displayedMembers;

    // R8.1: The system shall display user management functions only to authorized Administrators. 
    // R9.1:The system shall display user management functions only to authorized Administrators.
    // R8.2:The system shall provide a user management interface that allows Administrators to add users and remove users. 
    // R9.2: The system shall provide a user management interface that allows Administrators to promote users and demote users.
    public UserManagementScreen(AppController controller, Member administrator) {
        controller.requireAdmin(administrator);
        this.controller = controller;
        this.administrator = administrator;
        this.listModel = new DefaultListModel<>();
        this.userList = new JList<>(listModel);
        this.displayedMembers = new ArrayList<>();

        setTitle("ULIMS - Manage Users");
        setSize(690, 470);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel heading = new JLabel("Authorized Laboratory Members");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 14, 4, 14));
        add(heading, BorderLayout.NORTH);

        userList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(userList);
        scroll.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 9, 9));
        JButton add = button("Add User");
        JButton remove = button("Remove User");
        JButton promote = button("Promote");
        JButton demote = button("Demote");
        JButton refresh = button("Refresh");
        actions.add(add);
        actions.add(remove);
        actions.add(promote);
        actions.add(demote);
        actions.add(refresh);
        add(actions, BorderLayout.SOUTH);

        add.addActionListener(e -> addUser());
        remove.addActionListener(e -> modifySelected("remove"));
        promote.addActionListener(e -> modifySelected("promote"));
        demote.addActionListener(e -> modifySelected("demote"));
        refresh.addActionListener(e -> refreshUsers());

        refreshUsers();
    }

    // This allows the button size to change
    private JButton button(String title) {
        JButton button = new JButton(title);
        button.setPreferredSize(new Dimension(115, 42));
        return button;
    }

    // R8.7: The system shall update the user's authorization status in the Lab Member Database when a valid management action is confirmed. 
    // R9.6: The system shall update the lab member database with new user permissions when an action is successful. 
    // Recheck access in case an administrator has been demoted elsewhere.
    private void refreshUsers() {
        try {
            controller.requireAdmin(administrator);
            displayedMembers = controller.getMembers();
            listModel.clear();
            for (Member member : displayedMembers) {
                listModel.addElement(member.getStudentID() + " | " + member.getName()
                        + (member.isAdmin() ? " | Administrator" : " | Member"));
            }
        } catch (SecurityException ex) {
            showError(ex.getMessage());
            dispose();
        }
    }

    // R8.2: The system shall provide a user management interface that allows Administrators to add users and remove users. 
    // R8.3: The system shall allow an Administrator to manually authorize a new user using their Student ID. 
    // R8.4: The system shall prevent an Administrator from adding a Student ID that is already authorized. 
    // R8.12: The system shall notify the Administrator when a user management action is prevented from being completed by the system. 
    // R8.11: The system shall display confirmation when a user management action is successfully completed. 
    private void addUser() {
        JTextField studentId = new JTextField();
        JTextField name = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[] {"Member", "Administrator"});

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Student ID:"));
        form.add(studentId);
        form.add(new JLabel("Name:"));
        form.add(name);
        form.add(new JLabel("Role:"));
        form.add(role);

        int choice = JOptionPane.showConfirmDialog(this, form, "Authorize User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            controller.addMember(administrator, studentId.getText(), name.getText(),
                    role.getSelectedIndex() == 1);
            refreshUsers();
            showSuccess("User authorized successfully.");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            showError(ex.getMessage());
        }
    }

    // R8.5: The system shall allow an Administrator to remove an existing user's authorization using their Student ID. 
    // R9.2:The system shall provide a user management interface that allows Administrators to promote users and demote users. 
    // R8.6: The system shall prevent an Administrator from removing a Student ID that is not currently authorized. 
    // R9.3: The system shall only allow promotion of users which currently possess member permissions.
    // R9.4:The system shall only allow demotion of users which currently possess administrator permissions. 
    // R9.5:The system shall prevent the demotion of an administrator if after the action there are zero administrators remaining.
    // R8.11: The system shall display confirmation when a user management action is successfully completed.
    // R8.12: The system shall notify the Administrator when a user management action is prevented from being completed by the system.
    // R9.8: The system shall display confirmation when a user management action is successfully completed. 
    // R9.9:The system shall notify the Administrator when a user management action is prevented from being completed by the system. 
    // User confirmation must occur BEFORE any destructive/role change is saved.
    private void modifySelected(String action) {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= displayedMembers.size()) {
            showError("Select a user first.");
            return;
        }
        Member selected = displayedMembers.get(selectedIndex);
        String id = selected.getStudentID();
        String verb = action.equals("remove") ? "remove" : action.equals("promote") ? "promote" : "demote";
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + verb + " Student ID " + id + "?",
                "Confirm " + verb, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            switch (action) {
                case "remove":
                    controller.removeMember(administrator, id);
                    break;
                case "promote":
                    controller.promoteMember(administrator, id);
                    break;
                case "demote":
                    controller.demoteMember(administrator, id);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported action.");
            }
            if (!controller.hasAdminPermission(administrator)) {
                showSuccess("User permissions updated. Your administrator session has ended.");
                dispose();
                return;
            }
            refreshUsers();
            showSuccess("User management action completed successfully.");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            showError(ex.getMessage());
        }
    }

    // R8.12: The system shall notify the Administrator when a user management action is prevented from being completed by the system.
    // R9.9: The system shall notify the Administrator when a user management action is prevented from being completed by the system. 
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "User Management Error", JOptionPane.ERROR_MESSAGE);
    }

    // R8.11: The system shall display confirmation when a user management action is successfully completed.
    // R9.8: The system shall display confirmation when a user management action is successfully completed.
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "User Management", JOptionPane.INFORMATION_MESSAGE);
    }
}
