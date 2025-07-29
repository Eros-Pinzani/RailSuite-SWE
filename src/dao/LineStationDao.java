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
     * Returns the LineStation relation identified by idLine and idStation.
     * @param idLine the line id
     * @param idStation the station id
     * @return the corresponding LineStation object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    LineStation findById(int idLine, int idStation) throws SQLException;

    /**
     * Returns all LineStation relations associated with a specific line.
     * @param idLine the line id
     * @return list of LineStation objects ordered by station_order
     * @throws SQLException if a database access error occurs
     */
    List<LineStation> findByLine(int idLine) throws SQLException;
}
