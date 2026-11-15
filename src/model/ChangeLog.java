package src.model;

import java.util.ArrayList;

public class ChangeLog {
    //attributes
    private ArrayList<ChangeLogEntry> entries;

    //constructors
    public ChangeLog() {
        entries = new ArrayList<>();
    }

    //functional methods
    public boolean logAction(String memberID, String action, String details) {
        //TODO finish implementing this method as part of requirements
        return true;
    }

    //getters
    public ArrayList<ChangeLogEntry> getEntries() {
        return entries;
    }
}
