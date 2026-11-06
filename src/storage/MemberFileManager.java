package src.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.model.Member;

/**
 * Handles member records in the pipe-delimited {@code members.txt} file.
 */
public class MemberFileManager {
    private static final Path MEMBERS_FILE = Paths.get("members.txt");

    /** Creates the file manager. */
    public MemberFileManager() {
    }

    /**
     * Checks whether the member file exists or can be created, and is readable and writable.
     *
     * @return {@code true} when the file is accessible
     */
    public static boolean isDatabaseAccessible() {
        try {
            // Create an empty database file if needed, but do not grant access to unknown IDs.
            if (!Files.exists(MEMBERS_FILE)) {
                Files.createFile(MEMBERS_FILE);
            }
            return Files.isReadable(MEMBERS_FILE) && Files.isWritable(MEMBERS_FILE);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads valid-looking member records from the member file.
     *
     * @return parsed members, or an empty list when the file is missing or an I/O error occurs
     */
    public static List<Member> readMembers() {
        if (!Files.exists(MEMBERS_FILE)) {
            return new ArrayList<>();
        }

        try {
            // Each record stores ID, name, and admin status separated by vertical bars.
            List<String> lines = Files.readAllLines(MEMBERS_FILE, StandardCharsets.UTF_8);
            List<Member> members = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split("\\|");
                if (values.length >= 3) {
                    String studentId = values[0].trim();
                    String name = values[1].trim();
                    boolean isAdmin = Boolean.parseBoolean(values[2].trim());
                    members.add(new Member(studentId, name, isAdmin));
                }
            }
            return members;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Replaces the member file with the supplied records.
     *
     * @param members members to persist
     * @return {@code true} if written; {@code false} for null input or an I/O failure
     */
    public static boolean writeMembers(List<Member> members) {
        if (members == null) {
            return false;
        }

        try {
            // Rewrite the file as a complete snapshot to keep the stored member list consistent.
            List<String> lines = new ArrayList<>();
            for (Member member : members) {
                String line = member.getStudentID() + "|" + member.getName() + "|" + member.isAdmin();
                lines.add(line);
            }
            Files.write(
                MEMBERS_FILE,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Finds a member by case-insensitive Student ID.
     *
     * @param studentId ID to look up
     * @return the matching member, or {@code null} for blank or unknown IDs
     */
    public static Member findMemberByStudentId(String studentId) {
        if (studentId == null) {
            return null;
        }

        String target = studentId.trim();
        if (target.isEmpty()) {
            return null;
        }

        // IDs are case-insensitive to avoid rejecting a valid ID due only to letter casing.
        for (Member member : readMembers()) {
            if (member.getStudentID().equalsIgnoreCase(target)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Returns the parsed member records as an unmodifiable list.
     *
     * @return read-only list of non-null members
     */
    public static List<Member> getAuthorizedMembers() {
        List<Member> authorized = new ArrayList<>();
        for (Member member : readMembers()) {
            if (member != null) {
                authorized.add(member);
            }
        }
        return Collections.unmodifiableList(authorized);
    }
}
