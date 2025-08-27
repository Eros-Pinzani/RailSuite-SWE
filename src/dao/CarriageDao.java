package dao;

import domain.Carriage;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Carriage entities.
 * Defines methods for CRUD operations and queries on carriages.
 */
public interface CarriageDao {
    /**
     * Factory method to create a CarriageDao instance.
     * @return a CarriageDao implementation
     */
    static CarriageDao of() {
        return new CarriageDaoImp();
    }

    /**
     * Retrieves a carriage by its unique identifier.
     * @param id the id of the carriage
     * @return the Carriage object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Carriage selectCarriage(int id) throws SQLException;

    /**
     * Retrieves all carriages assigned to a specific convoy.
     * @param convoyId the id of the convoy
     * @return a list of Carriage objects assigned to the convoy
     * @throws SQLException if a database access error occurs
     */
    List<Carriage> selectCarriagesByConvoyId(int convoyId) throws SQLException;

    /**
     * Updates the convoy assignment for a carriage.
     * @param carriageId the id of the carriage
     * @param idConvoy the id of the convoy to assign, or null to unassign
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateCarriageConvoy(int carriageId, Integer idConvoy) throws SQLException;
}
