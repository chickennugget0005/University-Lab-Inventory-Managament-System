package src.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import src.model.ConsumableItem;
import src.model.ReusableItem;
import src.model.Container;
import src.model.ContainerCollection;
import src.model.Item;
import src.model.Lab;
import src.model.Member;
import src.model.MemberManager;
import src.model.ChangeLogEntry;
import src.model.Room;
import src.storage.LabFileManager;
import src.storage.MemberFileManager;
import src.storage.ChangeLogFileManager;


// Connects the graphical interface to lab data and the member database.
public class AppController {
    private Lab currentLab;
    private final List<Member> members;
    // R8.1: The system shall display user management functions only to authorized Administrators. 
    // R9.1: The system shall display user management functions only to authorized Administrators.
    private final Map<Member, Boolean> activeSessions;
    private boolean setupRequired;
    // Sample inventory is opt-in and never written to the laboratory file. REMINDER: THIS IS TEMPORARY. MARIO DO NOT FORGET.
    private final boolean demoMode = Boolean.getBoolean("ulims.demo");

    public AppController() {
        members = new ArrayList<>();
        // Only members authenticated through login() can receive administrator permissions.
        activeSessions = new IdentityHashMap<>();

        // R1.1: The system shall determine whether initial setup has been completed when ULIMS is launched. Check to see if an existing lab AND at least one administrator indicate that one-time setup has been completd. Checks both just in case.
        Path labFile = Paths.get("lab_data.txt");
        Path memberFile = Paths.get("members.txt");
        boolean labExists = Files.exists(labFile);
        boolean membersExist = Files.exists(memberFile);

        if (!labExists && !membersExist) {
            // First run: Main.java shows LabSetupScreen.
            setupRequired = true;
            currentLab = null;
            return;
        }

        // A half-created lab is not a new installation. Stop so it doesnt break the system.
        //R1.8: If setup of the lab fails due to invalid data files, the system shall display an error message and refuse to proceed.
        if (!labExists || !membersExist) {
            throw new IllegalStateException(
                "Incomplete lab setup: lab_data.txt and members.txt must both exist."
            );
        }

        currentLab = LabFileManager.loadLab();
        members.addAll(MemberFileManager.readMembers());

        if (currentLab == null || members.isEmpty()) {
            throw new IllegalStateException("Saved laboratory or member data could not be loaded.");
        }

        boolean hasAdministrator = false;
        for (Member member : members) {
            if (member.isAdmin()) {
                hasAdministrator = true;
                break;
            }
        }
        if (!hasAdministrator) {
            throw new IllegalStateException("Saved member database has no administrator.");
        }

        setupRequired = false;

        // Use the saved lab and real authorized members for authentication.
        // ONLY REPLACE THE IN-MEMORY INVENTORY WHEN RUNNING A DEMO. DO NOT DO THIS ANY OTHER TIME. DEMO ONLY. DO NOT FORGET.
        if (demoMode) {
            currentLab = createDefaultLab(currentLab.getName());
        }
    }

    public boolean isDemoMode() {
        return demoMode;
    }

    // R1.1: The system shall determine whether initial setup has been completed when ULIMS is launched. 
    public boolean isSetupRequired() {
        return setupRequired;
    }

    
    // R1.2: If initial setup has not been completed, the system shall create a new lab.
    // R1.3: The system shall prompt an initial administrator ID when creating a new lab. 
    // R1.4: The system shall create an account with the initial administrator ID stored in the lab member database. 
    // R1.5: The system shall create an empty inventory structure when creating a new lab. 
    public void initializeLab(String labName, String administratorId) {
        if (!setupRequired) {
            throw new IllegalStateException("Initial setup has already been completed.");
        }

        // Say no to drugs (Say no to empty fields), so you always know who you are (so there is always an identity)
        if (labName == null || labName.trim().isEmpty()
                || administratorId == null || administratorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lab name and administrator Student ID are required.");
        }

        // The existing data files are line-based and member fields use '|'.
        // Reject extra "|" that would corrupt information
        if (administratorId.contains("|") || administratorId.contains("\n")
                || administratorId.contains("\r") || labName.contains("\n")
                || labName.contains("\r")) {
            throw new IllegalArgumentException("Lab name and Student ID contain invalid characters.");
        }

        // R1.2: If initial setup has not been completed, the system shall create a new lab. 
        // R1.5: The system shall create an empty inventory structure when creating a new lab. 
        // No sample containers, inventory items, or users are added.
        Lab newLab = new Lab(labName.trim());

        // R1.4: The system shall create an account with the initial administrator ID stored in the lab member database. 
        List<Member> initialMembers = new ArrayList<>();
        initialMembers.add(new Member(
            administratorId.trim(), "Initial Administrator", true
        ));

        // Only save after both files are considered valid imputs. If the second save fails, then delete the two files so the next launch can get a retry
        if (!LabFileManager.saveLab(newLab)) {
            throw new IllegalStateException("Could not save the new laboratory.");
        }
        if (!MemberFileManager.writeMembers(initialMembers)) {
            try {
                Files.deleteIfExists(Paths.get("lab_data.txt"));
                Files.deleteIfExists(Paths.get("members.txt"));
            } catch (IOException ignored) {
                // Original error is reported below; staff may need to fix disk permissions.
            }
            throw new IllegalStateException("Could not save the initial administrator.");
        }

        // Only commit the new state in memory once both files are saved.
        currentLab = newLab;
        members.clear();
        members.addAll(initialMembers);
        setupRequired = false;
    }

    // R2.2: The system shall require the User to enter their Student ID.
    // R2.3: The system shall verify the Student ID against the Lab Member Database to determine whether the user is authorized.
    // R8.8: The system shall permit newly authorized users to log into ULIMS using their Student ID.
    // R8.9: The system shall deny access to users whose authorization has been removed. 
    // R9.7: The system shall apply new user permissions at the next target user login to the system.
    // Session instances are tracked by identity to prevent callers from forging an administrator account simply by constructing a Member object.
    public synchronized Member login(String studentId) {
        if (setupRequired || studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        if (!MemberFileManager.isDatabaseAccessible()) {
            throw new IllegalStateException("Member database is not available.");
        }
        Member stored = MemberFileManager.findMemberByStudentId(studentId.trim());
        if (stored == null || findCurrentMember(stored.getStudentID()) == null) {
            return null;
        }
        Member loggedIn = new Member(stored.getStudentID(), stored.getName(), stored.isAdmin());
        activeSessions.put(loggedIn, loggedIn.isAdmin());
        return loggedIn;
    }

    // End the actual session, not merely the visible Swing window.
    public synchronized void logout(Member member) {
        activeSessions.remove(member);
    }

    // R2.4: The system shall determine whether the authorized Student ID has Member or Administrator permissions.
    // R8.1: The system shall display user management functions only to authorized Administrators
    // R9.1: The system shall display user management functions only to authorized Administrators
    // Demoting an active administrator removes their privileges immediately.
    public synchronized boolean hasAdminPermission(Member member) {
        if (member == null || !Boolean.TRUE.equals(activeSessions.get(member))) {
            return false;
        }
        Member current = findCurrentMember(member.getStudentID());
        return current != null && current.isAdmin();
    }

    // Access checks should stay in the controller. DO NOT MOVE PLEASE
    // R8.1: The system shall display user management functions only to authorized Administrators. 
    // R9.1: The system shall display user management functions only to authorized Administrators
    // hiding a GUI button alone is not a security boundary.
    public void requireAdmin(Member member) {
        if (!hasAdminPermission(member)) {
            throw new SecurityException("Administrator privileges are required.");
        }
    }

    // Provide the GUI a read-only snapshot, not the controller's internal list.
    public synchronized List<Member> getMembers() {
        return new MemberManager(members).getMembers();
    }

    // R8.10: The system shall record successful user management changes in the system's change log. 
    public synchronized List<ChangeLogEntry> getChangeLogEntries(Member administrator) {
        requireAdmin(administrator);
        return Collections.unmodifiableList(ChangeLogFileManager.loadEntries());
    }

    // R8.3: The system shall allow an Administrator to manually authorize a new user using their Student ID. 
    // R8.4: The system shall prevent an Administrator from adding a Student ID that is already authorized. 
    // R8.7: The system shall update the user's authorization status in the Lab Member Database when a valid management action is confirmed.
    public synchronized Member addMember(Member administrator, String studentId, String name,
            boolean makeAdministrator) {
        final Member[] added = new Member[1];
        saveMemberChange(administrator, "USER_ADDED", "Added Student ID " + safe(studentId)
                + (makeAdministrator ? " as Administrator" : " as Member"), manager -> {
            added[0] = manager.addMember(studentId, name, makeAdministrator);
        });
        return added[0];
    }

    // R8.5:The system shall allow an Administrator to remove an existing user's authorization using their Student ID. 
    // R8.6: The system shall prevent an Administrator from removing a Student ID that is not currently authorized. 
    // R8.7: The system shall update the user's authorization status in the Lab Member Database when a valid management action is confirmed.
    // R8.9: The system shall deny access to users whose authorization has been removed.
    public synchronized void removeMember(Member administrator, String studentId) {
        saveMemberChange(administrator, "USER_REMOVED", "Removed Student ID " + safe(studentId),
            manager -> manager.removeMember(studentId));
    }

    // R9.3: The system shall only allow promotion of users which currently possess member permissions.
    // R9.6: The system shall update the lab member database with new user permissions when an action is successful. 
    // R9.7: The system shall apply new user permissions at the next target user login to the system.
    public synchronized void promoteMember(Member administrator, String studentId) {
        saveMemberChange(administrator, "USER_PROMOTED", "Promoted Student ID " + safe(studentId),
            manager -> manager.promoteMember(studentId));
    }

    // R9.4: The system shall only allow demotion of users which currently possess administrator permissions. 
    // R9.5: The system shall prevent the demotion of an administrator if after the action there are zero administrators remaining. 
    // R9.6: The system shall update the lab member database with new user permissions when an action is successful.
    // R9.7: The system shall apply new user permissions at the next target user login to the system.
    public synchronized void demoteMember(Member administrator, String studentId) {
        saveMemberChange(administrator, "USER_DEMOTED", "Demoted Student ID " + safe(studentId),
            manager -> manager.demoteMember(studentId));
    }

    // R8.1: The system shall display user management functions only to authorized Administrators. 
    // R9.1: The system shall display user management functions only to authorized Administrators. 
    // R8.7: The system shall update the user's authorization status in the Lab Member Database when a valid management action is confirmed.
    // R9.6: The system shall update the lab member database with new user permissions when an action is successful.
    // R8.10: The system shall record successful user management changes in the system's change log.
    // General: Compute an independent candidate list first. If saving the log fails, attempt to restore the previous member file before reporting error.
    private void saveMemberChange(Member administrator, String action, String details,
            Consumer<MemberManager> change) {
        requireAdmin(administrator);
        List<Member> previous = new MemberManager(members).getMembers();
        MemberManager pending = new MemberManager(members);
        change.accept(pending);
        List<Member> updated = pending.getMembers();

        if (!MemberFileManager.writeMembers(updated)) {
            throw new IllegalStateException("Could not save the member database.");
        }

        ChangeLogEntry entry = new ChangeLogEntry(OffsetDateTime.now().toString(),
                administrator.getStudentID(), action, details);
        if (!ChangeLogFileManager.appendEntry(entry)) {
            if (!MemberFileManager.writeMembers(previous)) {
                throw new IllegalStateException(
                    "Could not record the change and could not restore the member database. "
                    + "The administrator must repair the files before continuing."
                );
            }
            throw new IllegalStateException("Could not write to the change log. No change was saved.");
        }

        members.clear();
        members.addAll(updated);
    }

    // Normalize optional values included in human-readable audit details.
    private String safe(String input) {
        return input == null ? "" : input.trim();
    }

    // Match a session against the latest in-memory account and role state.
    private Member findCurrentMember(String studentId) {
        if (studentId == null) {
            return null;
        }
        for (Member member : members) {
            if (member.getStudentID().equalsIgnoreCase(studentId.trim())) {
                return member;
            }
        }
        return null;
    }

    public Lab getCurrentLab() {
        return currentLab;
    }

    // DON'T TOUCH THE DANG SEARCH APIs. THEY ARE NEEDED BY SEARCHPANEL.
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        if (currentLab == null) {
            return items;
        }
        for (Room room : currentLab.getRooms()) {
            for (Container container : room.getContainers()) {
                items.addAll(container.getItems());
            }
            for (ContainerCollection collection : room.getCollections()) {
                for (Container container : collection.getContainers()) {
                    items.addAll(container.getItems());
                }
            }
        }
        return items;
    }

    public List<Item> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllItems();
        }
        String term = query.trim().toLowerCase(Locale.ROOT);
        List<Item> matches = new ArrayList<>();
        for (Item item : getAllItems()) {
            String name = item.getName() == null ? "" : item.getName();
            String path = item.getPath() == null ? "" : item.getPath();
            if (name.toLowerCase(Locale.ROOT).contains(term)
                    || path.toLowerCase(Locale.ROOT).contains(term)) {
                matches.add(item);
            }
        }
        return matches;
    }

    // Existing helper preserved for compatibility with the rest of the project.
    // UC8/UC9 changes use saveMemberChange() so validation and logging are always applied.
    public boolean saveMembers() {
        return MemberFileManager.writeMembers(members);
    }

    // DEMO DEFAULT LAB: NOT INTENDED TO BE REALIZED. DELETE BEFORE FINISHING PROJECT
    private Lab createDefaultLab(String name) {
        Lab lab = new Lab(name);
        Room room = new Room(lab.generateID(), "Room 101", lab);
        lab.addRoom(room);

        // Include both supported storage paths so the GUI can show
        ContainerCollection supplies = new ContainerCollection(lab.generateID(), "General Supplies", room);
        room.addCollection(supplies);
        Container shelf = new Container(lab.generateID(), "Shelf A", supplies);
        supplies.addContainer(shelf);
        Container bench = new Container(lab.generateID(), "Bench 1", room);
        room.addContainer(bench);

        ConsumableItem tips = new ConsumableItem(lab.generateID(), "Pipette Tips", shelf);
        tips.setQuantity(150);
        tips.setUnit("tips");
        tips.setDescription("Disposable pipette tips for sample transfer.");
        shelf.addItem(tips);

        ConsumableItem ethanol = new ConsumableItem(lab.generateID(), "Ethanol", bench);
        ethanol.setQuantity(4.5);
        ethanol.setUnit("L");
        ethanol.setDescription("Laboratory grade ethanol.");
        bench.addItem(ethanol);

        ReusableItem cylinder = new ReusableItem(lab.generateID(), "10 mL Graduated Cylinder", shelf);
        cylinder.setDescription("Glass cylinder for measuring liquids.");
        shelf.addItem(cylinder);
        return lab;
    }
}
