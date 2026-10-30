package src.model;

public class ConsumableItem extends Item {
    //attributes
    private double quantity;
    private String unit;

    //constructors
    //constructor with all attributes for restoring items in inventory from a save file
    public ConsumableItem(int itemID, String name, String description, Container parentContainer, double quantity, String unit) {
        super(itemID, name, description, parentContainer);
        this.quantity = quantity;
        this.unit = unit;
    }

    //constructor for creating a new consumable item with default attributes
    public ConsumableItem(int itemID, String name, Container parentContainer) {
        super(itemID, name, parentContainer);
        this.quantity = 0;
        this.unit = "";
    }

    //functional methods
    //increment quantity, if quantity would be reduced below 0 return false and only set to 0
    public boolean changeQuantity(double amount) { 
        if(amount + quantity >= 0) {
            quantity += amount;

            return true;
        }

        quantity = 0;

        return false;
    }

    //getters
    @Override 
    public boolean isReusable() {
        return false;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    //setters
    public boolean setQuantity(double quantity) { //do not allow negative quantities to be set
        if(quantity >= 0) {
            this.quantity = quantity;
            
            return true;
        }

        return false;
    }

    public boolean setUnit(String unit) {
        if(unit != null) {
            this.unit = unit;

            return true;
        }

        return false;
    }
}
