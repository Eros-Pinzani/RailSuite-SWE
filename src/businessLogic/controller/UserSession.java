package businessLogic.controller;

import domain.Staff;

public class UserSession {
    private static UserSession instance;
    private Staff staff;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Staff getStaff() {
        return staff;
    }

    public void clear() {
        staff = null;
    }
}
