package src.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import src.controller.AppController;
import src.model.Item;

//Search interface shared by members and administrators (UC3).
public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final AppController controller;
    private final ItemDetailPanel detailPanel;
    private final JTextField searchField = new JTextField(16);
    private final DefaultListModel<Item> results = new DefaultListModel<>();
    private final JList<Item> resultsList = new JList<>(results);
    private final JLabel status = new JLabel(" ");

    public SearchPanel(AppController controller, ItemDetailPanel detailPanel) {
        super(new BorderLayout(6, 6));
        this.controller = controller;
        this.detailPanel = detailPanel;
        setBorder(BorderFactory.createTitledBorder("Search Inventory"));
        setPreferredSize(new Dimension(330, 420));

        // R3.2: The system shall allow Users and Administrators to search for inventory items using item names, partial names, or location information. 
        JPanel input = new JPanel(new BorderLayout(5, 5));
        input.add(new JLabel("Item name or location"), BorderLayout.NORTH);
        input.add(searchField, BorderLayout.CENTER);
        JButton search = new JButton("Search");
        input.add(search, BorderLayout.EAST);
        search.addActionListener(event -> performSearch());
        searchField.addActionListener(event -> performSearch());
        add(input, BorderLayout.NORTH);

        // R3.4: The system shall display a list of matching inventory items and their associated container information.
        resultsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof Item) {
                    Item item = (Item) value;
                    String location = item.getParentContianer().getPath();
                    setText(item.getName() + " — " + location);
                    setToolTipText(location);
                }
                return this;
            }
        });
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // R3.5: The system shall allow Users and Administrators to select an item from the search results. 
        // R3.6: The system shall display the selected item's information, including its name, quantity, unit of measurement, and location within the laboratory structure. 
        resultsList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                Item chosen = resultsList.getSelectedValue();
                if (chosen != null) detailPanel.setItem(chosen);
            }
        });
        add(new JScrollPane(resultsList), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        performSearch();
    }

    private void performSearch() {
        // R3.3: The system shall search the inventory database for items matching the provided search criteria. 
        results.clear();
        detailPanel.clear();
        try {
            List<Item> matches = controller.searchItems(searchField.getText());
            for (Item item : matches) results.addElement(item);
            // R3.7: The system shall notify the User or Administrator when no matching items are found or when the search cannot be completed.
            if (matches.isEmpty()) status.setText("No matching inventory items found.");
            else status.setText(matches.size() + " matching item(s)");
        } catch (RuntimeException ex) {
            status.setText("Search could not be completed.");
        }
    }
}
