package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import src.controller.AppController;
import src.model.Item;

/**
 * Searches controller-provided inventory and displays results beside a shared detail panel.
 */
public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    /** Controller that provides inventory search results. */
    private final AppController controller;
    /** Shared destination for details of a selected item. */
    private final ItemDetailPanel itemDetailPanel;
    /** Text entered by the user as the search query. */
    private final JTextField searchField;
    /** Button that submits the search query. */
    private final JButton searchButton;
    /** Selectable search results. */
    private final JList<Item> resultsList;
    /** Backing model for the selectable results list. */
    private final DefaultListModel<Item> resultModel;

    /**
     * Creates the search field and results list.
     *
     * @param controller source of inventory search results
     * @param itemDetailPanel destination for the selected result's details
     */
    public SearchPanel(AppController controller, ItemDetailPanel itemDetailPanel) {
        super(new BorderLayout(8, 8));
        this.controller = controller;
        this.itemDetailPanel = itemDetailPanel;
        this.resultModel = new DefaultListModel<>();
        this.resultsList = new JList<>(resultModel);

        // Keep search input and results in a single panel so users can browse without navigation menus.
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JLabel label = new JLabel("Search inventory");
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        searchField = new JTextField(24);
        searchField.setPreferredSize(new Dimension(220, 38));
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(120, 38));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        topBar.add(label);
        topBar.add(searchField);
        topBar.add(searchButton);
        add(topBar, BorderLayout.NORTH);

        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultsList.addListSelectionListener(e -> {
            // Reuse the shared detail panel so search and tree navigation show the same item view.
            if (!e.getValueIsAdjusting()) {
                Item selected = resultsList.getSelectedValue();
                if (selected != null) {
                    itemDetailPanel.setItem(selected);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setPreferredSize(new Dimension(260, 220));
        add(scrollPane, BorderLayout.CENTER);

        performSearch();
    }

    /**
     * Refreshes the list using the current query and clears details when there are no matches.
     */
    private void performSearch() {
        // Delegate matching to the controller so the panel does not own inventory search logic.
        String query = searchField.getText() == null ? "" : searchField.getText().trim();
        List<Item> matches = controller.searchItems(query);
        resultModel.clear();

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching inventory items were found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            itemDetailPanel.clear();
            return;
        }

        for (Item item : matches) {
            resultModel.addElement(item);
        }
        resultsList.setSelectedIndex(0);
    }
}
