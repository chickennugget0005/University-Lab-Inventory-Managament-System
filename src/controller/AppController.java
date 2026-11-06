package src.controller;

<<<<<<< HEAD
import src.model.*;
import src.storage.*;

public class AppController {
    //attributes
    private Member currentMember;
    private LabFileManager labFileManager;

=======
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.model.Container;
import src.model.ContainerCollection;
import src.model.ConsumableItem;
import src.model.Item;
import src.model.Lab;
import src.model.Member;
import src.model.ReusableItem;
import src.model.Room;
import src.storage.LabFileManager;
import src.storage.MamberFileManager;

/**
 * Coordinates authentication, inventory queries, and persistence between the UI and data layers.
 */
public class AppController {
    private Lab currentLab;
    private final List<Member> members;

    /**
     * Loads the saved lab and member records, creating fallback data when no records exist.
     */
    public AppController() {
        // Load saved application data once so screens can use the same controller-owned state.
        currentLab = LabFileManager.loadLab();
        if (currentLab == null) {
            currentLab = createDefaultLab();
            LabFileManager.saveLab(currentLab);
        }

        members = new ArrayList<>(MamberFileManager.readMembers());
        if (members.isEmpty()) {
            members.add(new Member("ADMIN001", "Lab Administrator", true));
            members.add(new Member("USER001", "Student User", false));
            MamberFileManager.writeMembers(members);
        }
    }

    /**
     * Authenticates a Student ID against the member file.
     *
     * @param studentId ID entered by the user
     * @return the matching member, or {@code null} for a blank or unknown ID
     * @throws IllegalStateException if the member database is inaccessible
     */
    public Member login(String studentId) {
        // Reject blank IDs before consulting storage; unavailable storage must fail closed.
        if (studentId == null) {
            return null;
        }

        String normalized = studentId.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        if (!MamberFileManager.isDatabaseAccessible()) {
            throw new IllegalStateException("Member database is not available.");
        }

        return MamberFileManager.findMemberByStudentId(normalized);
    }

    /**
     * Checks the administrator role attached to an authenticated member.
     *
     * @param member member whose role is checked
     * @return {@code true} only when the member is non-null and has administrator status
     */
    public boolean hasAdminPermission(Member member) {
        return member != null && member.isAdmin();
    }

    /**
     * Enforces the administrator role for a privileged controller operation.
     *
     * @param member member requesting the operation
     * @throws SecurityException if the member is null or is not an administrator
     */
    public void requireAdmin(Member member) {
        // Keep privileged actions protected in the controller, not only hidden in the UI.
        if (!hasAdminPermission(member)) {
            throw new SecurityException("Administrator privileges are required for this function.");
        }
    }

    /**
     * Returns an unmodifiable snapshot of the members loaded by this controller.
     *
     * @return a read-only list of members
     */
    public List<Member> getMembers() {
        return Collections.unmodifiableList(new ArrayList<>(members));
    }

    /**
     * Returns the current in-memory laboratory model.
     *
     * @return current lab
     */
    public Lab getCurrentLab() {
        return currentLab;
    }

    /**
     * Collects items from room-level containers and containers in room collections.
     *
     * @return all items currently reachable in the lab model
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        // Containers can be attached directly to rooms or nested inside a collection.
        for (Room room : currentLab.getRooms()) {
            for (Container container : room.getContainers()) {
                if (container.getItems() != null) {
                    items.addAll(container.getItems());
                }
            }
            for (ContainerCollection collection : room.getCollections()) {
                for (Container container : collection.getContainers()) {
                    if (container.getItems() != null) {
                        items.addAll(container.getItems());
                    }
                }
            }
        }
        return items;
    }

    /**
     * Finds items whose name or storage path contains the query, ignoring letter case.
     *
     * @param query text to match; null or blank returns every item
     * @return matching items, or all items for a blank query
     */
    public List<Item> searchItems(String query) {
        // An empty query represents browsing all items; otherwise match names and paths.
        if (query == null || query.trim().isEmpty()) {
            return getAllItems();
        }

        String matchText = query.trim().toLowerCase();
        List<Item> matches = new ArrayList<>();
        for (Item item : getAllItems()) {
            String name = item.getName() == null ? "" : item.getName();
            String location = item.getPath() == null ? "" : item.getPath();
            if (name.toLowerCase().contains(matchText) || location.toLowerCase().contains(matchText)) {
                matches.add(item);
            }
        }
        return matches;
    }

    /**
     * Persists this controller's member list.
     *
     * @return {@code true} if the member file was written successfully
     */
    public boolean saveMembers() {
        return MamberFileManager.writeMembers(members);
    }

    /**
     * Builds the initial in-memory sample lab hierarchy.
     *
     * @return the sample lab and its initial items
     */
    private Lab createDefaultLab() {
        // Provide an initial hierarchy so the UI has inventory data before setup tools exist.
        Lab lab = new Lab("Chemistry Lab");
        Room room = new Room(1, "Room 101", lab);
        lab.addRoom(room);

        ContainerCollection general = new ContainerCollection(1, "General Supplies", room);
        room.addCollection(general);

        Container shelfA = new Container(1, "Shelf A", general);
        general.addContainer(shelfA);

        Container bench = new Container(2, "Bench 1", room);
        room.addContainer(bench);

        ConsumableItem pipetteTips = new ConsumableItem(1, "Pipette Tips", shelfA);
        pipetteTips.setQuantity(150);
        pipetteTips.setUnit("tips");
        pipetteTips.setDescription("Disposable pipette tips for sample transfer.");
        shelfA.addItem(pipetteTips);

        ConsumableItem ethanol = new ConsumableItem(2, "Ethanol", bench);
        ethanol.setQuantity(4.5);
        ethanol.setUnit("L");
        ethanol.setDescription("Laboratory grade ethanol");
        bench.addItem(ethanol);

        ReusableItem graduatedCylinder = new ReusableItem(3, "10 mL Graduated Cylinder", shelfA);
        graduatedCylinder.setDescription("Glass cylinder for measuring liquids.");
        shelfA.addItem(graduatedCylinder);

        return lab;
    }
>>>>>>> 908d63aa72f698cb4cb353f0fbd5ac91ed245812
}
