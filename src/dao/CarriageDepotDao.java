package dao;

import domain.Carriage;
import domain.CarriageDepot;
import domain.DTO.CarriageDepotDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for CarriageDepot entities.
 * Defines methods for CRUD operations and advanced queries on carriage depot relations.
 */
public interface CarriageDepotDao {
    /**
     * Factory method to create a CarriageDepotDao instance.
     * @return a CarriageDepotDao implementation
     */
    static CarriageDepotDao of() {
        return new CarriageDepotDaoImp();
    }

    /**
     * Retrieves a CarriageDepot relation by depot and carriage id.
     * @param idDepot the depot id
     * @param idCarriage the carriage id
     * @return the CarriageDepot object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException;

    /**
     * Retrieves all CarriageDepot relations for a given depot.
     * @param idDepot the depot id
     * @return a list of CarriageDepot objects
     * @throws SQLException if a database access error occurs
     */
    List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException;

    /**
     * Inserts a new CarriageDepot relation.
     * @param carriageDepot the CarriageDepot object to insert
     * @throws SQLException if a database access error occurs
     */
    void insertCarriageDepot(CarriageDepot carriageDepot) throws SQLException;

    /**
     * Updates an existing CarriageDepot relation.
     * @param carriageDepot the CarriageDepot object to update
     * @throws SQLException if a database access error occurs
     */
    void updateCarriageDepot(CarriageDepot carriageDepot) throws SQLException;

    /**
     * Deletes a CarriageDepot relation.
     * @param carriageDepot the CarriageDepot object to delete
     * @throws SQLException if a database access error occurs
     */
    void deleteCarriageDepot(CarriageDepot carriageDepot) throws SQLException;

    /**
     * Updates the status of carriages in depot and returns all available carriages (AVAILABLE, without id_convoy)
     * for a specific station and type, in a single query.
     * @param idStation the station id
     * @param modelType the model type
     * @return a list of available Carriage objects
     * @throws SQLException if a database access error occurs
     */
    List<domain.Carriage> findAvailableCarriagesForConvoy(int idStation, String modelType) throws SQLException;

    /**
     * Deletes all depot relations for a specific carriage (used when the carriage is assigned to a convoy).
     * @param idCarriage the carriage id
     * @throws SQLException if a database access error occurs
     */
    void deleteCarriageDepotByCarriage(int idCarriage) throws SQLException;

    /**
     * Deletes the depot relation only if the carriage is in AVAILABLE state.
     * @param idCarriage the carriage id
     * @throws SQLException if a database access error occurs
     */
    void deleteCarriageDepotByCarriageIfAvailable(int idCarriage) throws SQLException;

    /**
     * Returns all carriages associated with a convoy, with depot status info (and end of maintenance if present).
     * @param idConvoy the convoy id
     * @return a list of CarriageDepotDTO objects
     * @throws SQLException if a database access error occurs
     */
    List<CarriageDepotDTO> findCarriagesWithDepotStatusByConvoy(int idConvoy) throws SQLException;

    /**
     * Returns all carriages available for addition to a convoy (same type, in depot, AVAILABLE, without id_convoy).
     * @param idStation the station id
     * @param modelType the model type
     * @return a list of available Carriage objects
     * @throws SQLException if a database access error occurs
     */
    List<domain.Carriage> findAvailableCarriagesForConvoyAdd(int idStation, String modelType) throws SQLException;

    /**
     * Returns all model types of available carriages (in depot, AVAILABLE, without id_convoy) for a station, in a single query.
     * @param idStation the station id
     * @return a list of model type names
     * @throws SQLException if a database access error occurs
     */
    List<String> findAvailableCarriageTypesForConvoy(int idStation) throws java.sql.SQLException;

    /**
     * Returns all models of available carriages (in depot, AVAILABLE, without id_convoy) for a station and type, in a single query.
     * @param idStation the station id
     * @param modelType the model type
     * @return a list of model names
     * @throws SQLException if a database access error occurs
     */
    List<String> findAvailableCarriageModelsForConvoy(int idStation, String modelType) throws java.sql.SQLException;

    /**
     * Returns the active row of carriage_depot (with time_exited IS NULL) for a carriage.
     * @param idCarriage the carriage id
     * @return the CarriageDepot object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    CarriageDepot findActiveDepotByCarriage(int idCarriage) throws SQLException;

    /**
     * Retrieves all carriages associated with a convoy by its position.
     * @param idConvoy the convoy id
     * @return a list of Carriage objects
     * @throws SQLException if a database access error occurs
     */
    List<Carriage> getCarriagesByConvoyPosition(int idConvoy) throws SQLException;
}
