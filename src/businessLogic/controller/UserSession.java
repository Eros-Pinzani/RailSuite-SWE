package businessLogic.controller;

import domain.Staff;

/**
 * UserSession is a singleton class that manages the session for a
 * logged-in user, providing access to the user's information and
 * allowing for session management.
 */
public class UserSession {
    private static UserSession instance;
    private Staff staff;

    private UserSession() {}

    /**
     * Returns the singleton instance of UserSession.
     * @return the UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Sets the staff member for the current session.
     * @param staff the logged-in staff member
     */
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    /**
     * Returns the staff member for the current session.
     * @return the logged-in staff member
     */
    public Staff getStaff() {
        return staff;
    }

    /**
     * Clears the current session, removing the staff member.
     */
    public void clear() {
        staff = null;
    }
}
