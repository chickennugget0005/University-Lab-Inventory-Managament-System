package src.model;
import java.util.ArrayList;

public class ContainerCollection {
    //attributes
    private int collectionID;
    private String name;
    private ArrayList<Container> containers;
    private Room parentRoom;

    //constructors
    //constructor intaking all necessary values for creation of a new room with default structure or restoration from save file
    public ContainerCollection(int collectionID, String name, Room parentRoom) {
        this.collectionID = collectionID;
        this.name = name;
        this.containers = new ArrayList<>();
        this.parentRoom = parentRoom;
    }

    //functional methods
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
        return (containers.isEmpty());
    }

    //getters
    public String getPath() { //add the name of this object to the inventory path in a filepath style format
        return String.format("%s/%s/", parentRoom.getPath(), name);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Container> getContainers() {
        return containers;
    }

    public Room getParentRoom() {
        return parentRoom;
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
