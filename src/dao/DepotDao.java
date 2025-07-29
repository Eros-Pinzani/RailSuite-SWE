package dao;

import domain.Depot;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Depot entities.
 * Defines methods for CRUD operations and queries on depots.
 */
public interface DepotDao {
    /**
     * Factory method to create a DepotDao instance.
     * @return a DepotDao implementation
     */
    static DepotDao of() {
        return new DepotDaoImp();
    }

    /**
     * Retrieves a depot by its unique identifier.
     * @param idDepot the id of the depot
     * @return the Depot object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Depot getDepot(int idDepot) throws SQLException;

    /**
     * Retrieves all depots from the database.
     * @return a list of all Depot objects
     * @throws SQLException if a database access error occurs
     */
    List<Depot> getAllDepots() throws SQLException;

    /**
     * Inserts a new depot.
     * @param idDepot the id of the depot to insert
     * @throws SQLException if a database access error occurs
     */
    void insertDepot(int idDepot) throws SQLException;

    /**
     * Deletes a depot by its id.
     * @param idDepot the id of the depot to delete
     * @throws SQLException if a database access error occurs
     */
    void deleteDepot(int idDepot) throws SQLException;
}
