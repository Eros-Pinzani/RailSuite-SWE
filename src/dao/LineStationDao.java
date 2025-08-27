package dao;

import domain.LineStation;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for LineStation entities.
 * Defines methods for queries on line-station relationships.
 */
public interface LineStationDao {
    static LineStationDao of() {
        return new LineStationDaoImp();
    }

    /**
     * Returns all LineStation relations associated with a specific line.
     * @param idLine the line id
     * @return list of LineStation objects ordered by station_order
     * @throws SQLException if a database access error occurs
     */
    List<LineStation> findByLine(int idLine) throws SQLException;

    /**
     * Returns the timetable for a run, including each station with arrival and departure times.
     * @param idLine the line id
     * @param idStartStation the starting station id
     * @param departureTime the departure time in HH:mm format
     * @return list of StationArrAndDepDTO objects representing the timetable
     * @throws SQLException if a database access error occurs
     */
    List<domain.DTO.TimeTableDTO.StationArrAndDepDTO> findTimeTableForRun(int idLine, int idStartStation, String departureTime) throws SQLException;
}
