package dao;

import domain.Run;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

/**
 * Data Access Object interface for Run entities.
 * Defines methods for CRUD operations and queries on runs.
 */
public interface RunDao {
    static RunDao of() {
        return new RunDaoImp();
    }

    /**
     * Returns the run associated with a specific line and convoy.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @return the corresponding Run object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Run selectRunByLineAndConvoy(int idLine, int idConvoy) throws SQLException;

    /**
     * Returns the run identified by line, convoy, and staff.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @param idStaff the staff id
     * @return the corresponding Run object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Run selectRun(int idLine, int idConvoy, int idStaff) throws SQLException;

    /**
     * Returns the run associated with a specific staff and convoy.
     * @param idStaff the staff id
     * @param idConvoy the convoy id
     * @return the corresponding Run object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Run selectRunByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException;

    /**
     * Returns the run associated with a specific staff and line.
     * @param idStaff the staff id
     * @param idLine the line id
     * @return the corresponding Run object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Run selectRunByStaffAndLine(int idStaff, int idLine) throws SQLException;

    /**
     * Returns all runs in the system.
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectAllRuns() throws SQLException;

    /**
     * Removes a run identified by line and convoy.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @return true if the removal was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean removeRun(int idLine, int idConvoy) throws SQLException;

    /**
     * Creates a new run with the specified parameters.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @param idStaff the staff id
     * @param timeDeparture departure time
     * @param timeArrival arrival time
     * @param idFirstStation first station id
     * @param idLastStation last station id
     * @return the created Run object
     * @throws SQLException if a database access error occurs
     */
    Run createRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException;

    /**
     * Updates an existing run with the specified parameters.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @param idStaff the staff id
     * @param timeDeparture departure time
     * @param timeArrival arrival time
     * @param idFirstStation first station id
     * @param idLastStation last station id
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException;

    /**
     * Returns all runs associated with a specific staff.
     * @param idStaff the staff id
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByStaff(int idStaff) throws SQLException;

    /**
     * Returns all runs associated with a specific line.
     * @param idLine the line id
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByLine(int idLine) throws SQLException;

    /**
     * Returns all runs associated with a specific convoy.
     * @param idConvoy the convoy id
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByConvoy(int idConvoy) throws SQLException;

    /**
     * Returns all runs that start from a specific station.
     * @param idFirstStation the first station id
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByFirstStation(int idFirstStation) throws SQLException;

    /**
     * Returns all runs that end at a specific station.
     * @param idLastStation the last station id
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByLastStation(int idLastStation) throws SQLException;

    /**
     * Returns all runs that start from a specific station and have a given departure time.
     * @param idFirstStation the first station id
     * @param timeDeparture the departure time
     * @return list of Run objects
     * @throws SQLException if a database access error occurs
     */
    List<Run> selectRunsByFirstStationAndDeparture(int idFirstStation, Time timeDeparture) throws SQLException;

}
