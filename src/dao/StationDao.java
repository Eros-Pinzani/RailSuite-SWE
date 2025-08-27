package dao;

import domain.Station;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Station entities.
 * Defines methods for queries on stations.
 */
public interface StationDao {
    static StationDao of() {
        return new StationDaoImp();
    }

    /**
     * Returns all head stations in the system.
     * @return list of Station objects that are head stations
     * @throws SQLException if a database access error occurs
     */
    List<Station> findAllHeadStations() throws SQLException;
}