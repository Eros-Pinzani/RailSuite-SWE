package businessLogic.service;

import dao.RunDao;
import domain.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Service for managing train runs.
 * Provides business logic for filtering and searching runs using DAO classes.
 */
public class ManageRunService {
    private final RunDao runDao = RunDao.of();
    private final List<Run> runs;

    /**
     * Constructs the ManageRunService and loads all Run data from the database.
     * Throws a RuntimeException if data cannot be loaded.
     */
    public ManageRunService() {
        try {
            runs = RunDao.of().selectAllRunRaws();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an unmodifiable list of all Run objects loaded at service initialization.
     * @return list of Run objects
     */
    public List<Run> getAllRunRaws() {
        return Collections.unmodifiableList(runs);
    }

    /**
     * Filters Run objects based on the provided parameters. Any parameter can be null.
     * @param lineName the line name to filter (nullable)
     * @param convoyId the convoy id to filter (nullable)
     * @param staffNameSurname the staff name and surname to filter (nullable)
     * @param firstStationName the first station name to filter (nullable)
     * @param timeDeparture the departure time to filter (nullable)
     * @return filtered list of Run objects
     */
    public List<Run> filterRunRaws(String lineName, String convoyId, String staffNameSurname, String firstStationName, java.sql.Timestamp timeDeparture) {
        return runs.stream()
                .filter(r -> lineName == null || (r.getLineName() != null && r.getLineName().equals(lineName)))
                .filter(r -> convoyId == null || (r.getIdConvoy() != null && String.valueOf(r.getIdConvoy()).equals(convoyId)))
                .filter(r -> staffNameSurname == null || (r.getStaffNameSurname() != null && r.getStaffNameSurname().equals(staffNameSurname)))
                .filter(r -> firstStationName == null || (r.getFirstStationName() != null && r.getFirstStationName().equals(firstStationName)))
                .filter(r -> timeDeparture == null || (r.getTimeDeparture() != null && r.getTimeDeparture().equals(timeDeparture)))
                .toList();
    }

    /**
     * Searches for Run objects using the provided filters and a date range (entire day).
     * Uses a single DAO call, which returns an empty list if no runs are found.
     * @param lineName the line name to filter (nullable)
     * @param convoyId the convoy id to filter (nullable)
     * @param staffNameSurname the staff name and surname to filter (nullable)
     * @param firstStationName the first station name to filter (nullable)
     * @param dayStart the start timestamp of the day (inclusive)
     * @param dayEnd the end timestamp of the day (inclusive)
     * @return list of Run objects matching the filters and date range
     * @throws SQLException if a database access error occurs
     */
    public List<Run> searchRunsByDay(String lineName, String convoyId, String staffNameSurname, String firstStationName, Timestamp dayStart, Timestamp dayEnd) throws SQLException {
        List<Run> runs = runDao.searchRunsByDay(lineName, convoyId, staffNameSurname, firstStationName, dayStart, dayEnd);
        return runs == null ? List.of() : runs;
    }
}
