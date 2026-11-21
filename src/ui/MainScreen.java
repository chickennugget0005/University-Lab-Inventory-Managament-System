package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import src.controller.AppController;
import src.model.Container;
import src.model.ContainerCollection;
import src.model.Item;
import src.model.Lab;
import src.model.Member;
import src.model.Room;

//Main inventory workspace for authenticated members and administrators.
public class MainScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainScreen(Member member, AppController controller) {
        setTitle("ULIMS - Inventory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 650);
        setMinimumSize(new Dimension(850, 480));
        setLocationRelativeTo(null);

        // Show the logged-in identity and role so access differences are visible.
        String role = controller.hasAdminPermission(member) ? "Administrator" : "Member";
        JLabel heading = new JLabel(controller.getCurrentLab().getName() + " | " + role
                + " | " + member.getStudentID()
                + (controller.isDemoMode() ? " | SAMPLE INVENTORY MODE" : ""));
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 12, 4, 12));
        
        // Use the same detail panel for search results and tree selections. This never needs to read inventory files
        ItemDetailPanel detailPanel = new ItemDetailPanel();
        SearchPanel searchPanel = new SearchPanel(controller, detailPanel);
        JTree tree = new JTree(buildTree(controller.getCurrentLab()));
        tree.addTreeSelectionListener(event -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) return;
            Object value = node.getUserObject();
            if (value instanceof InventoryNode) {
                Object object = ((InventoryNode) value).object;
                if (object instanceof Item) detailPanel.setItem((Item) object);
                else detailPanel.clear();
            }
        });
        // Expand the hierarchy tree
        for (int index = 0; index < tree.getRowCount(); index++) tree.expandRow(index);
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder("Laboratory Structure"));
        treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        treePanel.setPreferredSize(new Dimension(280, 400));

        JSplitPane searchAndDetails = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchPanel, detailPanel);
        searchAndDetails.setResizeWeight(0.48);
        JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, searchAndDetails);
        body.setResizeWeight(0.25);
        add(body, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // R2.5: The system shall allow an authorized User to access ULIMS with Administrator-only functionalities disabled. 
        // Check the Admin role again when opening an admin window, not only when drawing it.
        // FINISH ASAP THE TWO SCREENS ARE PREVIEWS AND ARE NOT FINISHED
        if (controller.hasAdminPermission(member)) {
            JButton manage = new JButton("Manage Users (Preview)");
            manage.addActionListener(event -> {
                controller.requireAdmin(member);
                new UserManagementScreen(controller, member).setVisible(true);
            });
            controls.add(manage);

            JButton log = new JButton("Change Log (Preview)");
            log.addActionListener(event -> {
                controller.requireAdmin(member);
                new ChangeLogScreen(controller, member).setVisible(true);
            });
            controls.add(log);
        }
        JButton logout = new JButton("Logout");
        logout.addActionListener(event -> {
            // Dispose child windows so admin previews cannot stay open after logout.
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                if (window != this && (window instanceof UserManagementScreen
                        || window instanceof ChangeLogScreen)) window.dispose();
            }
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen(controller).setVisible(true));
        });
        controls.add(logout);
        JPanel header = new JPanel(new BorderLayout());
        header.add(heading, BorderLayout.NORTH);
        header.add(controls, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JLabel footer = new JLabel("  Inventory items: " + controller.getAllItems().size()
                + (controller.isDemoMode() ? " (temporary sample data; not saved)" : ""));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));
        add(footer, BorderLayout.SOUTH);
    }

    // Build the real model hierarchy without relying on unfinished model toString methods.
    private DefaultMutableTreeNode buildTree(Lab lab) {
        DefaultMutableTreeNode root = node(lab.getName(), lab);
        for (Room room : lab.getRooms()) {
            DefaultMutableTreeNode roomNode = node(room.getName(), room);
            root.add(roomNode);
            for (ContainerCollection collection : room.getCollections()) {
                DefaultMutableTreeNode collectionNode = node(collection.getName(), collection);
                roomNode.add(collectionNode);
                for (Container container : collection.getContainers()) addContainer(collectionNode, container);
            }
            for (Container container : room.getContainers()) addContainer(roomNode, container);
        }
        return root;
    }

    private void addContainer(DefaultMutableTreeNode parent, Container container) {
        DefaultMutableTreeNode containerNode = node(container.getName(), container);
        parent.add(containerNode);
        for (Item item : container.getItems()) containerNode.add(node(item.getName(), item));
    }

    private DefaultMutableTreeNode node(String label, Object object) {
        return new DefaultMutableTreeNode(new InventoryNode(label, object));
    }

    // Keep a reference to the underlying object so selecting an item opens its details.
    // The label is separate because the team's Item.toString currently returns a placeholder.
    private static class InventoryNode {
        private final String label;
        private final Object object;
        InventoryNode(String label, Object object) { this.label = label; this.object = object; }
        @Override public String toString() { return label; }
    }
}
