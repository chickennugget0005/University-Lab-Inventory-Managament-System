package src.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// Manages authorized laboratory members in memory for UC8 and UC9.
// The controller verifies administrator permissions and saves the changes. This class focuses on validation and account/role rules, avoiding direct file I/O.

public class MemberManager {
    private final List<Member> members;

    // Work on copies so a rejected operation cannot modify the live member list.
    // The controller persists only the validated result.
    public MemberManager(List<Member> initialMembers) {
        if (initialMembers == null) {
            throw new IllegalArgumentException("Member list cannot be null.");
        }
        members = new ArrayList<>();
        for (Member member : initialMembers) {
            if (member == null) {
                throw new IllegalArgumentException("Member list contains a null member.");
            }
            members.add(copy(member));
        }
    }

    // Return defensive copies; the GUI cannot change someone's role by editing a Member object that it obtained for display.
    public List<Member> getMembers() {
        List<Member> snapshot = new ArrayList<>();
        for (Member member : members) {
            snapshot.add(copy(member));
        }
        return Collections.unmodifiableList(snapshot);
    }
    // R8.5: The system shall allow an Administrator to remove an existing user's authorization using their Student ID. 
    // R8.6: The system shall prevent an Administrator from removing a Student ID that is not currently authorized. 
    // R9.3: The system shall only allow promotion of users which currently possess member permissions.
    // R9.4: The system shall only allow demotion of users which currently possess administrator permissions. 
    public Member findMember(String studentId) {
        Member member = findInternal(studentId);
        return member == null ? null : copy(member);
    }

    // R8.3: The system shall allow an Administrator to manually authorize a new user using their Student ID. 
    // R8.4: The system shall prevent an Administrator from adding a Student ID that is already authorized. 
    public Member addMember(String studentId, String name, boolean administrator) {
        String id = requiredId(studentId);
        String displayName = requiredName(name);
        if (findInternal(id) != null) {
            throw new IllegalArgumentException("That Student ID is already authorized.");
        }
        Member member = new Member(id, displayName, administrator);
        members.add(member);
        return copy(member);
    }

    // R8.5: The system shall allow an Administrator to remove an existing user's authorization using their Student ID. 
    // R8.6: The system shall prevent an Administrator from removing a Student ID that is not currently authorized. 
    // Extra safeguard: Keep at least one laboratory administrator.
    public void removeMember(String studentId) {
        Member member = requiredMember(studentId);
        if (member.isAdmin() && countAdministrators() <= 1) {
            throw new IllegalStateException("Cannot remove the last administrator.");
        }
        members.remove(member);
    }

    // R9.3: The system shall only allow promotion of users which currently possess member permissions. 
    // An absent ID and an already-administrator ID are invalid requests.
    public void promoteMember(String studentId) {
        Member member = requiredMember(studentId);
        if (member.isAdmin()) {
            throw new IllegalArgumentException("The selected user is already an administrator.");
        }
        member.setAdminStatus(true);
    }

    // R9.4: The system shall only allow demotion of users which currently possess administrator permissions. 
    // R9.5: The system shall prevent the demotion of an administrator if after the action there are zero administrators remaining. 
    public void demoteMember(String studentId) {
        Member member = requiredMember(studentId);
        if (!member.isAdmin()) {
            throw new IllegalArgumentException("The selected user is already a member.");
        }
        if (countAdministrators() <= 1) {
            throw new IllegalStateException("Cannot demote the last administrator.");
        }
        member.setAdminStatus(false);
    }

    // Reuse a single administrator count for both deletion and demotion checks.
    private int countAdministrators() {
        int count = 0;
        for (Member member : members) {
            if (member.isAdmin()) {
                count++;
            }
        }
        return count;
    }

    // R8.6:  The system shall prevent an Administrator from removing a Student ID that is not currently authorized. 
    // R9.3: The system shall only allow promotion of users which currently possess member permissions.
    // R9.4: The system shall only allow demotion of users which currently possess administrator permissions. 
    private Member requiredMember(String studentId) {
        String id = requiredId(studentId);
        Member member = findInternal(id);
        if (member == null) {
            throw new IllegalArgumentException("That Student ID is not authorized.");
        }
        return member;
    }

    // Compare IDs without case sensitivity to match MemberFileManager.login lookup.
    private Member findInternal(String studentId) {
        if (studentId == null) {
            return null;
        }
        String id = studentId.trim();
        if (id.isEmpty()) {
            return null;
        }
        for (Member member : members) {
            if (member.getStudentID().equalsIgnoreCase(id)) {
                return member;
            }
        }
        return null;
    }

    // R8.3: The system shall allow an Administrator to manually authorize a new user using their Student ID. 
    // R8.4: The system shall prevent an Administrator from adding a Student ID that is already authorized. 
    // Don't allow delimiters/newlines that would corrupt the pipe-delimited file.
    private String requiredId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        String id = studentId.trim();
        if (containsInvalidFileCharacter(id)) {
            throw new IllegalArgumentException("Student ID contains invalid characters.");
        }
        return id;
    }

    // Require a readable member name; protect the same pipe-delimited file format.
    private String requiredName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        String result = name.trim();
        if (containsInvalidFileCharacter(result)) {
            throw new IllegalArgumentException("Name contains invalid characters.");
        }
        return result;
    }

    private boolean containsInvalidFileCharacter(String input) {
        return input.indexOf('|') >= 0 || input.indexOf('\r') >= 0
                || input.indexOf('\n') >= 0 || input.indexOf('\0') >= 0;
    }

    // Keep manager-owned records separate from caller-owned Member objects.
    private Member copy(Member member) {
        return new Member(member.getStudentID(), member.getName(), member.isAdmin());
    }
}
