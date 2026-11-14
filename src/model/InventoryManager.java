package src.model;

import src.storage.*;

public class InventoryManager {
    //attributes
    private Lab lab;
    private ChangeLog changeLog;
    private LabFileManager labFileManager;

    //constructor
    public InventoryManager(Lab lab, ChangeLog changeLog, LabFileManager labFileManager) {
        this.lab = lab;
        this.changeLog = changeLog;
        this.labFileManager = labFileManager;
    }

    //functional methods
    //create a new room within the lab
    public Room createRoom(String name, String memberID) {
        Room newRoom = new Room(lab.generateID(), name, lab);
        
        if(lab.addRoom(newRoom)) { //only return object if the action succeded
            return newRoom;
        }

        return null;
    }

    //create a new collection within a room in the lab (only if said room exists)
    public ContainerCollection createCollection(Room room, String name, String memberID) {
        if(lab.getRooms().contains(room)) {
            ContainerCollection newCollection = new ContainerCollection(lab.generateID(), name, room);
            
            if (room.addCollection(newCollection)) { //only return object if the action succeded
                return newCollection; 
            }
        }

        return null;
    }

    //create a new container wihtin a room in the lab (only if said room exists)
    public Container createContainerInRoom(Room room, String name, String memberID) {
        if(lab.getRooms().contains(room)) {
            Container newContainer = new Container(lab.generateID(), name, room);

            if(room.addContainer(newContainer)) { //only return object if the action succeded
                return newContainer; 
            }
        }

        return null;
    }

    //create a new container witin a collection within a room in the lab (only if said collection exists)
    public Container createContainerInCollection(ContainerCollection containerCol, String name, String memberID) {
        if(lab.getRooms().contains(containerCol.getParentRoom()) && containerCol.getParentRoom().getCollections().contains(containerCol)) {
            Container newContainer = new Container(lab.generateID(), name, containerCol);

            if(containerCol.addContainer(newContainer)) { //only return object if the action succeded
                return newContainer; 
            }
        }

        return null;
    }

    //create a new reusable item within a container (only if said container exists)
    public ReusableItem createReusableItem(Container container, String name, String memberID) {
        Room parentRoom;
        
        //obtain the ancestor room of the container given
        if(container.getParentCollection() == null) {
            parentRoom = container.getParentRoom();
        } else {
            ContainerCollection parentCollection = container.getParentCollection();
            parentRoom = parentCollection.getParentRoom();
        }

        //determine if the item has a valid filepath to be placed in the lab and if so, add it
        if(lab.getRooms().contains(parentRoom)) {
            ReusableItem item = new ReusableItem(lab.generateID(), name, container);
            
            if(container.addItem(item)) { //only return object if the action succeded
                return item;
            }
        }

        return null;
    }

    //create a new consumable item within a container (only if said container exists)
    public ConsumableItem createConsumableItem(Container container, String name, String memberID) {
        Room parentRoom;
        
        //obtain the ancestor room of the container given
        if(container.getParentCollection() == null) {
            parentRoom = container.getParentRoom();
        } else {
            ContainerCollection parentCollection = container.getParentCollection();
            parentRoom = parentCollection.getParentRoom();
        }

        //determine if the item has a valid filepath to be placed in the lab and if so, add it
        if(lab.getRooms().contains(parentRoom)) {
            ConsumableItem item = new ConsumableItem(lab.generateID(), name, container);
            
            if(container.addItem(item)) { //only return object if the action succeded
                return item;
            }
        }

        return null;
    }

    //remove an existing room from the lab (only if it is empty)
    public Boolean removeRoom(Room room, String memberID) {
        if(room.isEmpty()) {
            return lab.removeRoom(room);
        }
        
        return false;
    }

    //remove an existing container collection from the lab (only if it is empty)
    public Boolean removeCollection(ContainerCollection containerCol, String memberID) {
        Room parentRoom = containerCol.getParentRoom();
        
        if(lab.getRooms().contains(parentRoom) && containerCol.isEmpty()) {
            return parentRoom.removeCollection(containerCol);
        }

        return false;
    }

    //remove an existing container from the lab (only if it is empty)
    public Boolean removeContainer(Container container, String memberID) {
        Room parentRoom;
        Boolean inCollection = container.getParentCollection() != null;

        //obtain the ancestor room of the container given
        if(!inCollection) {
            parentRoom = container.getParentRoom();
        } else {
            ContainerCollection parentCollection = container.getParentCollection();
            parentRoom = parentCollection.getParentRoom();
        }

        if(lab.getRooms().contains(parentRoom) && container.isEmpty()) {
            //perform the removal operation from whichever parent the collection has
            if(inCollection) {
                return container.getParentCollection().removeContainer(container);
            } else {
                return parentRoom.removeContainer(container);
            }
        }

        return false;
    }

    //remove an existing item from the lab
    public Boolean removeItem(Item item, String memberID) {
        Room parentRoom;
        Container parentContainer = item.getParentContianer();
        ContainerCollection parentCollection = parentContainer.getParentCollection();
        Boolean inCollection = parentCollection != null;

        //obtain the ancestor room of the parent container of the item given
        if(!inCollection) {
            parentRoom = parentContainer.getParentRoom();
        } else {
            parentRoom = parentCollection.getParentRoom();
        }

        if(lab.getRooms().contains(parentRoom)) { //make sure the operation is occuring within this manager's lab
            if(inCollection && parentCollection.getContainers().contains(parentContainer)) {
                //success case: there is a valid file path through a container collection and the operation is performed
                return parentContainer.removeItem(item);

            } else if (!inCollection && parentRoom.getContainers().contains(parentContainer)) {
                //success case: there is a valid file path directly within a room and the operation is performed
                return parentContainer.removeItem(item);
            }
        }
        
        return false;
    }

    //allow the user to check out reusable items
    public Boolean checkOutItem(ReusableItem item, String memberID) {
        //TODO finish implementation later in checkout requirements
        return false;
    }

    //allow the user to check in reusable items
    public Boolean checkInItem(ReusableItem item, String memberID) {
        //TODO finish implementation later in check-in requirements
        return false;
    }

    //allow the user to change the quantity of an item
    public Boolean changeQuantity(ConsumableItem item, double amount, String memberID) {
        //TODO finish implementation later in item requirements
        return false;
    }

    //getters
    public Lab getLab() {
        return lab;
    }
}
