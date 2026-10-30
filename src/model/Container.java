package src.model;
import java.util.ArrayList;

public class Container {
    //attributes
    private int containerID;
    private String name;
    private ArrayList<Item> items;
    private Room parentRoom;
    private ContainerCollection parentCollection;

    //constructors
    //constructor intaking all necessary values for creation of a new room with default structure or restoration from save file
    //this one specifically is used in cases where the container is housed directly within a Room
    public Container(int containerID, String name, Room parentRoom) {
        this.containerID = containerID;
        this.name = name;
        this.parentRoom = parentRoom;
        this.parentCollection = null;
    }

    //constructor intaking all necessary values for creation of a new room with default structure or restoration from save file
    //this one specifically is used in cases where the container is housed directly within a Collection
    public Container(int containerID, String name, ContainerCollection parentCollection) {
        this.containerID = containerID;
        this.name = name;
        this.parentRoom = null;
        this.parentCollection = parentCollection;
    }

    //functional methods
    public boolean addItem(Item item) { //add a new item into the arraylist when user adds a new item into the container
        if(item != null) {
            items.add(item);

            return true;
        }

        return false;
    }

    public boolean removeContainer(Item item) { //removes a item from the arraylist when user permanently removes an item from the container 
        if(items.contains(item)) {
            items.remove(item);

            return true;
        }

        return false;
    }

    public boolean isEmpty() { //return whether or not the structure is empty, requirement for deletion
        return (items.isEmpty());
    }

    //getters
    //add the name of this object to the inventory path in a filepath style format
    //take the filepath of whatever the parent object in the inventory structure is
    public String getPath() { 
        if(parentCollection == null) {
            return String.format("%s/%s/", parentRoom.getPath(), name);
        }
        
        return String.format("%s/%s/", parentCollection.getPath(), name);
    }

    public int getContainerID() {
        return containerID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Room getParentRoom() {
        return parentRoom;
    }

    public ContainerCollection getParentCollection() {
        return parentCollection;
    }

    //setters
    public boolean setName(String name) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        }

        return false;
    }
}
