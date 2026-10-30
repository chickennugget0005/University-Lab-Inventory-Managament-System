package src.model;

public class ReusableItem extends Item {
    //attributes
    private String holderID;
    private String checkOutTime;
    
    //constructors
    //constructor with all attributes for restoring items in inventory from a save file
    public ReusableItem(int itemID, String name, String description, Container parentContainer, String holderID, String checkOutTime) {
        super(itemID, name, description, parentContainer);
        this.holderID = holderID;
        this.checkOutTime = checkOutTime;
    }

    //constructor for creating a new reusable item with default attributes
    public ReusableItem(int itemID, String name, Container parentContainer) {
        super(itemID, name, parentContainer);
        this.holderID = "";
        this.checkOutTime = "";
    }

    //functional methods
    public boolean checkOut(String memberID) {
        //TODO implement this method as part of requirement implementation
        return false;
    }

    public boolean checkIn() {
        //TODO implement this method as part of requirement implementation
        return false;
    }

    //getters
    public boolean isCheckedOut() { //only return not checked out when both holderID and checkOutTime have been cleared
        return !(checkOutTime.isEmpty() && holderID.isEmpty());
    }

    @Override 
    public boolean isReusable() {
        return true;
    }

    public String getHolderID() {
        return holderID;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    //setters
    public boolean setHolderID() {
        //TODO leave for now, I dont know if we want / need setters for these
        return false;
    }

    public boolean setCheckOutTime() {
        //TODO leave for now, I dont know if we want / need setters for these
        return false;
    }
}
