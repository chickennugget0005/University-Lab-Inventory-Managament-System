package src.model;

import java.util.ArrayList;

/**
 * A storage location containing inventory items, attached either directly to a room
 * or to a container collection.
 */
public class Container {
    private int containerID;
    private String name;
    private ArrayList<Item> items;
    private Room parentRoom;
    private ContainerCollection parentCollection;

    // This constructor supports containers placed directly in a room.
    /**
     * Creates a container directly within a room.
     *
     * @param containerID unique container identifier
     * @param name display name
     * @param parentRoom containing room
     */
    public Container(int containerID, String name, Room parentRoom) {
        this.containerID = containerID;
        this.name = name;
        this.parentRoom = parentRoom;
        this.parentCollection = null;
        this.items = new ArrayList<>();
    }

    // This constructor supports the alternate room -> collection -> container hierarchy.
    /**
     * Creates a container within a collection.
     *
     * @param containerID unique container identifier
     * @param name display name
     * @param parentCollection containing collection
     */
    public Container(int containerID, String name, ContainerCollection parentCollection) {
        this.containerID = containerID;
        this.name = name;
        this.parentRoom = null;
        this.parentCollection = parentCollection;
        this.items = new ArrayList<>();
    }

    /**
     * Adds a non-null item to this container.
     *
     * @param item item to add
     * @return {@code true} if added
     */
    public boolean addItem(Item item) {
        // Reject null entries so inventory traversal and detail views can rely on real items.
        if (item != null) {
            items.add(item);
            return true;
        }
        return false;
    }

    /**
     * Removes an item from this container.
     *
     * @param item item to remove
     * @return {@code true} if the item was present and removed
     */
    public boolean removeItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
            return true;
        }
        return false;
    }

    /**
     * Checks whether this container has no items.
     *
     * @return {@code true} when empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Builds this container's location from its parent.
     *
     * @return slash-separated storage path
     */
    public String getPath() {
        // Use whichever parent is set to build the item's full, searchable storage location.
        if (parentCollection == null) {
            return String.format("%s/%s/", parentRoom.getPath(), name);
        }
        return String.format("%s/%s/", parentCollection.getPath(), name);
    }

    /**
     * Returns this container's identifier.
     *
     * @return this container's identifier
     */
    public int getContainerID() {
        return containerID;
    }

    /**
     * Returns this container's display name.
     *
     * @return this container's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mutable item list held by this container.
     *
     * @return the items in this container
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Returns the room parent, if this container is attached directly to a room.
     *
     * @return room parent, or {@code null} when attached to a collection
     */
    public Room getParentRoom() {
        return parentRoom;
    }

    /**
     * Returns the collection parent, if this container is attached to a collection.
     *
     * @return collection parent, or {@code null} when attached directly to a room
     */
    public ContainerCollection getParentCollection() {
        return parentCollection;
    }

    /**
     * Changes the display name when the supplied value is non-null and non-empty.
     *
     * @param name replacement name
     * @return {@code true} if the name was changed
     */
    public boolean setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        }
        return false;
    }
}
