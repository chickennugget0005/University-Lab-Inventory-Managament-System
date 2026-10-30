package src.model;

public class Member {
    //attributes
    private String studentID;
    private String name;
    private boolean isAdmin;

    //constructor
    public Member(String studentID, String name, boolean isAdmin) {
        this.studentID = studentID;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    //functional methods

    //getters
    public String getStudentID() {
        return studentID;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    //setters
    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean setName(String name) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        }

        return false;
    }
}
