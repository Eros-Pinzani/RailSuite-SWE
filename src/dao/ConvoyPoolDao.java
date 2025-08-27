package dao;

import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for ConvoyPool entities.
 * Defines methods for CRUD operations and queries on convoy pools.
 */
public interface ConvoyPoolDao {
    /**
     * Factory method to create a ConvoyPoolDao instance.
     * @return a ConvoyPoolDao implementation
     */
    static ConvoyPoolDao of() {
        return new ConvoyPoolDaoImp();
    }

    /**
     * Retrieves a convoy pool by its unique identifier.
     * @param idConvoy the id of the convoy
     * @return the ConvoyPool object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException;

    /**
     * Inserts a new convoy pool.
     * @param pool the ConvoyPool object to insert
     * @throws SQLException if a database access error occurs
     */
    void insertConvoyPool(domain.ConvoyPool pool) throws SQLException;

    /**
     * Returns, for each convoy associated with a station: convoy id, status, number of carriages, and carriage types (comma separated).
     * @param idStation the station id
     * @return a list of ConvoyTableDTO objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException;

    /**
     * Checks if a convoy is in the pool and updates its status if necessary.
     * @param idConvoy the id of the convoy
     * @return true if the convoy was found and its status was updated, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean checkAndUpdateConvoyStatus(int idConvoy) throws SQLException;

    /**
     * Checks the availability of convoys at a specific station.
     * @param idStation the station id
     * @return a list of ConvoyTableDTO objects representing available convoys
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyTableDTO> checkConvoyAvailability(int idStation) throws SQLException;

}
