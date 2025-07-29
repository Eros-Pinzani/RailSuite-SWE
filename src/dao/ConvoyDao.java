package dao;

import domain.Carriage;
import domain.Convoy;
import java.sql.SQLException;
import java.util.List;
import businessLogic.service.ConvoyDetailsService;

/**
 * Data Access Object interface for Convoy entities.
 * Defines methods for CRUD operations and queries on convoys and their assignments.
 */
public interface ConvoyDao {
    /**
     * Factory method to create a ConvoyDao instance.
     * @return a ConvoyDao implementation
     */
    static ConvoyDao of() {
        return new ConvoyDaoImp();
    }

    /**
     * Retrieves a convoy by its unique identifier.
     * @param id the id of the convoy
     * @return the Convoy object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Convoy selectConvoy(int id) throws SQLException;

    /**
     * Retrieves all convoys from the database.
     * @return a list of all Convoy objects
     * @throws SQLException if a database access error occurs
     */
    List<Convoy> selectAllConvoys() throws SQLException;

    /**
     * Removes a convoy by its id.
     * @param id the id of the convoy
     * @return true if removed, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean removeConvoy(int id) throws SQLException;

    /**
     * Adds a carriage to a convoy.
     * @param convoyId the id of the convoy
     * @param carriage the carriage to add
     * @return true if added, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException;

    /**
     * Removes a carriage from a convoy.
     * @param convoyId the id of the convoy
     * @param carriage the carriage to remove
     * @return true if removed, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException;

    /**
     * Finds the convoy id for a given carriage id.
     * @param carriageId the carriage id
     * @return the convoy id, or null if not assigned
     * @throws SQLException if a database access error occurs
     */
    Integer findConvoyIdByCarriageId(int carriageId) throws SQLException;

    /**
     * Creates a new convoy and assigns carriages to it.
     * @param carriages the list of carriages to assign
     * @return the created Convoy object
     * @throws SQLException if a database access error occurs
     */
    Convoy createConvoy(List<Carriage> carriages) throws SQLException;

    /**
     * Retrieves detailed information about a convoy by its id.
     * @param id the id of the convoy
     * @return a ConvoyDetailsRaw object with details
     * @throws SQLException if a database access error occurs
     */
    ConvoyDetailsService.ConvoyDetailsRaw selectConvoyDetailsById(int id) throws SQLException;

    /**
     * Row representing a convoy assigned to a staff member.
     */
    class ConvoyAssignedRow {
        public final int convoyId;
        public final String departureStation;
        public final String departureTime;
        public final String arrivalStation;
        public final String arrivalTime;
        public ConvoyAssignedRow(int convoyId, String departureStation, String departureTime, String arrivalStation, String arrivalTime) {
            this.convoyId = convoyId;
            this.departureStation = departureStation;
            this.departureTime = departureTime;
            this.arrivalStation = arrivalStation;
            this.arrivalTime = arrivalTime;
        }
    }

    /**
     * Retrieves all convoys assigned to a specific staff member.
     * @param staffId the staff id
     * @return a list of ConvoyAssignedRow objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyAssignedRow> selectAssignedConvoysRowsByStaff(int staffId) throws SQLException;
}
