package src.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import src.model.ConsumableItem;
import src.model.Item;
import src.model.ReusableItem;

/**
 * Read-only Swing panel that renders common and type-specific item information.
 */
public class ItemDetailPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    /** Displays the selected item's name or the empty-state heading. */
    private final JLabel titleLabel;
    /** Displays the selected item's read-only details. */
    private final JTextArea detailArea;

    /** Creates an initially empty detail view. */
    public ItemDetailPanel() {
        super(new BorderLayout(8, 8));
        // Keep details read-only here; inventory changes belong in controller operations.
        setBorder(BorderFactory.createTitledBorder("Item Details"));
        setPreferredSize(new Dimension(280, 260));

        titleLabel = new JLabel("No item selected");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(new JScrollPane(detailArea), BorderLayout.CENTER);

        clear();
    }

    /**
     * Displays details for the supplied item.
     *
     * @param item item to display; null clears the panel
     */
    public void setItem(Item item) {
        if (item == null) {
            clear();
            return;
        }

        titleLabel.setText(item.getName());
        StringBuilder details = new StringBuilder();
        // Show common fields first, then type-specific properties for each inventory item.
        details.append("Location: ").append(item.getPath()).append("\n");
        details.append("ID: ").append(item.getItemID()).append("\n");
        details.append("Description: ").append(item.getDescription() == null || item.getDescription().isBlank() ? "No description available" : item.getDescription()).append("\n\n");

        if (item instanceof ConsumableItem) {
            ConsumableItem consumable = (ConsumableItem) item;
            details.append("Type: Consumable\n");
            details.append("Quantity: ").append(consumable.getQuantity()).append(" ").append(consumable.getUnit()).append("\n");
        } else if (item instanceof ReusableItem) {
            ReusableItem reusable = (ReusableItem) item;
            details.append("Type: Reusable\n");
            details.append("Checked out: ").append(reusable.isCheckedOut() ? "Yes" : "No").append("\n");
            if (reusable.isCheckedOut()) {
                details.append("Holder ID: ").append(reusable.getHolderID()).append("\n");
                details.append("Check-out time: ").append(reusable.getCheckOutTime()).append("\n");
            }
        } else {
            details.append("Type: Item\n");
        }

        detailArea.setText(details.toString());
    }

    /** Clears the item details and restores the empty-state message. */
    public void clear() {
        titleLabel.setText("No item selected");
        detailArea.setText("Select an item to view its details.");
    }
}
