package domain;

/**
 * Interface representing a Staff entity.
 * Provides factory method and accessors for staff properties.
 */
public interface Staff {
    /** @return the unique identifier of the staff */
    int getIdStaff();
    /** @return the staff's name */
    String getName();
    /** @return the staff's surname */
    String getSurname();
    /** @return the staff's address */
    String getAddress();
    /** @return the staff's email */
    String getEmail();
    /** @return the staff's password */
    String getPassword();
    /** @return the type of staff (OPERATOR or SUPERVISOR) */
    TypeOfStaff getTypeOfStaff();

    /**
     * Enum representing the type of staff.
     */
    enum TypeOfStaff {
        OPERATOR,
        SUPERVISOR
    }

    /**
     * Factory method to create a Staff instance.
     * @param idStaff the staff id
     * @param name the staff's name
     * @param surname the staff's surname
     * @param address the staff's address
     * @param email the staff's email
     * @param password the staff's password
     * @param typeOfStaff the type of staff
     * @return a Staff instance
     */
    static Staff of(int idStaff, String name, String surname, String address, String email, String password, TypeOfStaff typeOfStaff) {
        return new StaffImp(idStaff, name, surname, address, email, password, typeOfStaff);
    }
}
