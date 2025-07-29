package dao;

import domain.ConvoyPool;
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
     * Updates a convoy pool.
     * @param convoyPool the ConvoyPool object to update
     * @throws SQLException if a database access error occurs
     */
    void updateConvoyPool(ConvoyPool convoyPool) throws SQLException;

    /**
     * Inserts a new convoy pool.
     * @param pool the ConvoyPool object to insert
     * @throws SQLException if a database access error occurs
     */
    void insertConvoyPool(domain.ConvoyPool pool) throws SQLException;

    /**
     * Retrieves all convoy pools from the database.
     * @return a list of all ConvoyPool objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyPool> getAllConvoyPools() throws SQLException;

    /**
     * Retrieves all convoy pools for a specific station.
     * @param idStation the station id
     * @return a list of ConvoyPool objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException;

    /**
     * Retrieves all convoy pools with a specific status.
     * @param status the convoy status
     * @return a list of ConvoyPool objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException;

    /**
     * Retrieves all convoy pools for a specific station and status.
     * @param idStation the station id
     * @param status the convoy status
     * @return a list of ConvoyPool objects
     * @throws SQLException if a database access error occurs
     */
    List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException;

    /**
     * Returns, for each convoy associated with a station: convoy id, status, number of carriages, and carriage types (comma separated).
     * @param idStation the station id
     * @return a list of ConvoyTableDTO objects
     * @throws SQLException if a database access error occurs
     */
    List<domain.ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException;

    /**
     * Restituisce la lista dei convogli disponibili per una nuova run su una linea, direzione, data e ora specifica.
     * La query deve:
     * - Restituire solo i convogli che sono nella stazione di testa della linea/direzione
     * - Status = 'DEPOT' o 'WAITING'
     * - Non assegnati a una run che si sovrappone temporalmente (nella stessa data)
     * - Non in manutenzione
     */
    List<domain.Convoy> findAvailableConvoysForRun(int idLine, String direction, java.time.LocalDate date, String time);
}
