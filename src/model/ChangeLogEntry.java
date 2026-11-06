package src.model;

public class ChangeLogEntry {
    //attributes
    private String timeStamp;
    private String memberID;
    private String action;
    private String details;

    //constructor
    public ChangeLogEntry(String timeStamp, String memberID, String action, String details) {
        this.timeStamp = timeStamp;
        this.memberID = memberID;
        this.action = action;
        this.details = details;
    }

    //functional methods
    public String toString() {
        //TODO implement this method with changelog requirements
        return "placeholder string for the time being";
    }
}
