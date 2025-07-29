package dao;

import domain.CarriageDepot;

import java.sql.SQLException;
import java.util.List;

public interface CarriageDepotDao {
    static CarriageDepotDao of() {
        return new CarriageDepotDaoImp();
    }
    CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException;
    List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException;
    void insertCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
    void updateCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
    void deleteCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
    /**
     * Aggiorna lo stato delle vetture in deposito e restituisce tutte le vetture disponibili (AVAILABLE, senza id_convoy)
     * per una stazione e tipo specifici, in UNA sola query.
     */
    List<domain.Carriage> findAvailableCarriagesForConvoy(int idStation, String modelType) throws java.sql.SQLException;
    /**
     * Elimina tutte le relazioni di deposito per una carriage specifica (usato quando la carriage viene assegnata a un convoglio)
     */
    void deleteCarriageDepotByCarriage(int idCarriage) throws SQLException;
    /**
     * Elimina la relazione dal deposito solo se la carriage è in stato AVAILABLE
     */
    void deleteCarriageDepotByCarriageIfAvailable(int idCarriage) throws SQLException;

    /**
     * Restituisce tutte le vetture associate a un convoglio, con info sullo stato in deposito (e fine manutenzione se presente)
     */
    List<domain.CarriageDepotDTO> findCarriagesWithDepotStatusByConvoy(int idConvoy) throws SQLException;

    /**
     * Restituisce tutte le vetture disponibili per aggiunta a un convoglio (stesso tipo, in depot, AVAILABLE, senza id_convoy)
     */
    List<domain.Carriage> findAvailableCarriagesForConvoyAdd(int idStation, String modelType) throws SQLException;

    /**
     * Restituisce tutti i model_type delle vetture disponibili (in depot, AVAILABLE, senza id_convoy)
     * per una stazione, in UNA sola query.
     */
    List<String> findAvailableCarriageTypesForConvoy(int idStation) throws java.sql.SQLException;

    /**
     * Restituisce tutti i modelli delle vetture disponibili (in depot, AVAILABLE, senza id_convoy)
     * per una stazione e tipo, in UNA sola query.
     */
    List<String> findAvailableCarriageModelsForConvoy(int idStation, String modelType) throws java.sql.SQLException;

    /**
     * Restituisce la riga attiva di carriage_depot (cioè con time_exited IS NULL) per una carriage.
     */
    CarriageDepot findActiveDepotByCarriage(int idCarriage) throws SQLException;
}
