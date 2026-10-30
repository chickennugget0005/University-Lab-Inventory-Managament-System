package src.model;
import java.util.ArrayList;

public class Room {
    //Attributes
    private int roomID;
    private String name;
    private ArrayList<ContainerCollection> collections;
    private ArrayList<Container> containers;
    private Lab parentLab;

    //constructors
    //constructor intaking all necessary values for creation of a new room with default structure or restoration from save file
    public Room(int roomID, String name, Lab parentLab) {
        this.roomID = roomID;
        this.name = name;
        this.collections = new ArrayList<>();
        this.containers = new ArrayList<>();
        this.parentLab = parentLab;
    }

    //functional methods
    public boolean addCollection(ContainerCollection containerCol) { //add a new container collection into the arraylist when user adds container collections to lab structure
        if(containerCol != null) {
            collections.add(containerCol);

            return true;
        }

        return false;
    }

    public boolean removeCollection(ContainerCollection containerCol) { //remove a container collection from the arraylist when user wants to change lab structure
        if(collections.contains(containerCol)) {
            collections.remove(containerCol);

            return true;
        }

        return false;
    }

    public boolean addContainer(Container container) { //add a new container into the arraylist when user adds container to lab structure
        if(container != null) {
            containers.add(container);

            return true;
        }

        return false;
    }

    public boolean removeContainer(Container container) { //remove a container from the arraylist when user wants to change lab structure
        if(containers.contains(container)) {
            containers.remove(container);

            return true;
        }

        return false;
    }

    public boolean isEmpty() { //return whether or not the structure is empty, requirement for deletion
        return (collections.isEmpty() && containers.isEmpty());
    }

    //getters
    public String getPath() { //add the name of this object to the inventory path in a filepath style format
        return String.format("%s/%s/", parentLab.getName(), name);
    }

    public int getRoomID() {
        return roomID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ContainerCollection> getCollections() {
        return collections;
    }

    public ArrayList<Container> getContainers() {
        return containers;
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
