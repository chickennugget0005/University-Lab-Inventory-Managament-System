package src.model;

// Stores the details of one completed action recorded by ULIMS.
// R8.10: The system shall record successful user management changes in the system's change log
public class ChangeLogEntry {
    // Each record is immutable after creation so its stored values stay consistent.
    private final String timeStamp;
    private final String memberID;
    private final String action;
    private final String details;

    public ChangeLogEntry(String timeStamp, String memberID, String action, String details) {
        this.timeStamp = timeStamp;
        this.memberID = memberID;
        this.action = action;
        this.details = details;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    // R8.10: The system shall record successful user management changes in the system's change log
    // Quoting every field preserves embedded commas and double quotes.
    public String toCsvLine() {
        return csv(timeStamp) + "," + csv(memberID) + "," + csv(action) + "," + csv(details);
    }

    // Keep each record on one line and escape embedded CSV quotation marks.
    private String csv(String value) {
        String safe = value == null ? "" : value.replace("\r", " ").replace("\n", " ");
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    // Present an easy-to-read version in the administrator-only change-log GUI.
    @Override
    public String toString() {
        return timeStamp + " | " + memberID + " | " + action + " | " + details;
    }
}
