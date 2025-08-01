package dao;

import domain.Staff;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Staff entities.
 * Defines methods for CRUD operations and queries on staff members.
 */
public interface StaffDao {
    static StaffDao of() {
        return new StaffDaoImp();
    }

    /**
     * Returns the staff member identified by id.
     * @param id the staff member id
     * @return the corresponding Staff object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Staff findById(int id) throws SQLException;

    /**
     * Returns the staff member identified by email.
     * @param email the staff member's email
     * @return the corresponding Staff object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Staff findByEmail(String email) throws SQLException;

    /**
     * Returns all staff members in the system.
     * @return list of Staff objects
     * @throws SQLException if a database access error occurs
     */
    List<Staff> findAll() throws SQLException;

    /**
     * Returns all staff members of a given type.
     * @param type the type of staff (e.g., OPERATOR, SUPERVISOR)
     * @return list of Staff objects
     * @throws SQLException if a database access error occurs
     */
    List<Staff> findByType(Staff.TypeOfStaff type) throws SQLException;
}
