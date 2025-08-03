package dao;

import java.sql.SQLException;
import java.util.List;

import domain.StaffPool;

/**
 * Data Access Object interface for StaffPool entities.
 * Defines methods for queries and updates on staff pool records.
 */
public interface StaffPoolDao {
    enum ShiftStatus {
        AVAILABLE,
        ON_RUN,
        RELAX
    }

    static StaffPoolDao of() {
        return new StaffPoolDaoImp();
    }

    /**
     * Returns the StaffPool entry for the given staff id.
     * @param idStaff the staff id
     * @return the corresponding StaffPool object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    StaffPool findById(int idStaff) throws SQLException;

    /**
     * Returns all StaffPool entries for a given station.
     * @param idStation the station id
     * @return list of StaffPool objects
     * @throws SQLException if a database access error occurs
     */
    List<StaffPool> findByStation(int idStation) throws SQLException;

    /**
     * Updates the given StaffPool entry in the database.
     * @param staffPool the StaffPool object to update
     * @throws SQLException if a database access error occurs
     */
    void update(StaffPool staffPool) throws SQLException;

    /**
     * Returns all StaffPool entries with the given shift status.
     * @param status the shift status
     * @return list of StaffPool objects
     * @throws SQLException if a database access error occurs
     */
    List<StaffPool> findByStatus(ShiftStatus status) throws SQLException;

    /**
     * Returns all StaffPool entries with the given shift status and station.
     * @param status the shift status
     * @param idStation the station id
     * @return list of StaffPool objects
     * @throws SQLException if a database access error occurs
     */
    List<StaffPool> findByStatusAndStation(ShiftStatus status, int idStation) throws SQLException;

    /**
     * Finds available operators for a run at a specific station on a given date and time.
     * @param idStation the station id
     * @param date the date of the run
     * @param time the time of the run
     * @return list of available StaffDTO objects
     */
    List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate date, String time);
}
