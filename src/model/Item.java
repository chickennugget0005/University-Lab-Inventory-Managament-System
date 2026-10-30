package src.model;

public abstract class Item {
    //attributes
    private int itemID;
    private String name;
    private String description;
    private Container parentContainer;

    //constructors
    //constructor with all attributes for restoring a lab from a save file
    public Item(int itemID, String name, String description, Container parentContainer) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.parentContainer = parentContainer;
    }

    //constructor for creating a new lab with default attributes
    public Item(int itemID, String name, Container parentContainer) {
        this.itemID = itemID;
        this.name = name;
        this.description = "";
        this.parentContainer = parentContainer;
    }

    //getters
    public abstract boolean isReusable(); //each item must be defined as reusable or consumable at creation

    public String getPath() { //add the name of this object to the inventory path in a filepath style format
        return String.format("%s/%s/", parentContainer.getPath(), name);
    }

    public String toString() {
        //TODO implement this depending on output / fileIO requirements
        return "placeholder";
    }

    public int getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Container getParentContianer() {
        return parentContainer;
    }

    //setters
    public boolean setName(String name) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        }

        return false;
    }

    public boolean setDescription(String description) {
        if(description != null && !description.isEmpty()) {
            this.description = description;
            return true;
        }

        return false;
    }
}
