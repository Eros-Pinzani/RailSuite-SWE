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
     * Returns the station identified by id.
     * @param id the station id
     * @return the corresponding Station object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Station findById(int id) throws SQLException;

    /**
     * Returns the station identified by location.
     * @param location the location string
     * @return the corresponding Station object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Station findByLocation(String location) throws SQLException;

    /**
     * Returns all stations in the system.
     * @return list of Station objects
     * @throws SQLException if a database access error occurs
     */
    List<Station> findAll() throws SQLException;

    /**
     * Returns all head stations in the system.
     * @return list of Station objects that are head stations
     * @throws SQLException if a database access error occurs
     */
    List<Station> findAllHeadStations() throws SQLException;
}