package src.model;

import java.util.ArrayList;

public class Lab {
    //Attributes
    private String name;
    private int nextID;
    private ArrayList<Room> rooms;

    //Constructors
    public Lab(String name, int nextID) { //constructor for restoring a lab from a save file
        this.name = name;
        this.nextID = nextID;
        this.rooms = new ArrayList<>();
    }

    public Lab(String name) { //constructor for creating a new lab with default attributes
        this.name = name;
        this.nextID = 1;
        this.rooms = new ArrayList<>();
    }

    //Functional methods
    public int generateID() { //generate a new ID for items created within the lab, centralized so all items have unique IDs
        int selectID = nextID;
        nextID++;

        return selectID;
    }

    public boolean addRoom(Room room) { //add a new room into the arraylist when user adds rooms to lab structure
        if(room != null) {
            rooms.add(room);

            return true;
        }

        return false;
    }

    public boolean removeRoom(Room room) { //remove room from the arraylist when user removes rooms to lab structure
        if(rooms.contains(room)) {
            rooms.remove(room);

            return true;
        }

        return false;
    }

    //getters
    public String getName() {
        return name;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
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
