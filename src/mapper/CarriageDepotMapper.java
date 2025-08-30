package mapper;

import domain.CarriageDepot;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageDepotMapper {
    /**
     * Maps a ResultSet row to a CarriageDepot domain object.
     * Handles possible null for time_exited and status_of_carriage.
     * @param rs the ResultSet containing carriage depot data
     * @return a CarriageDepot domain object
     * @throws SQLException if a database access error occurs
     */
    public static CarriageDepot toDomain(ResultSet rs) throws SQLException {
        java.sql.Timestamp timeExited = null;
        try {
            timeExited = rs.getTimestamp("time_exited");
        } catch (SQLException e) {
            // time_exited non presente o null, lasciamo null
        }
        CarriageDepot.StatusOfCarriage status = null;
        try {
            String statusStr = rs.getString("status_of_carriage");
            if (statusStr != null) {
                status = CarriageDepot.StatusOfCarriage.valueOf(statusStr);
            }
        } catch (Exception e) {
            // status_of_carriage non valido o null, lasciamo null
        }
        return CarriageDepot.of(
            rs.getInt("id_depot"),
            rs.getInt("id_carriage"),
            rs.getTimestamp("time_entered"),
            timeExited,
            status
        );
    }
    /**
     * Popola un PreparedStatement con i dati di un oggetto CarriageDepot.
     * @param stmt il PreparedStatement da popolare
     * @param depot l'oggetto CarriageDepot contenente i dati
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatement(PreparedStatement stmt, CarriageDepot depot) throws SQLException {
        stmt.setInt(1, depot.getIdDepot());
        stmt.setInt(2, depot.getIdCarriage());
        stmt.setTimestamp(3, depot.getTimeEntered());
        stmt.setTimestamp(4, depot.getTimeExited());
        stmt.setString(5, depot.getStatusOfCarriage().name());
    }
    /**
     * Popola un PreparedStatement per l'update di status e time_exited.
     * @param stmt il PreparedStatement da popolare
     * @param status nuovo stato della carrozza
     * @param timeExited timestamp di uscita
     * @param idDepot id del deposito
     * @param idCarriage id della carrozza
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatementForUpdateStatusAndExitTime(PreparedStatement stmt, String status, java.sql.Timestamp timeExited, int idDepot, int idCarriage) throws SQLException {
        stmt.setString(1, status);
        stmt.setTimestamp(2, timeExited);
        stmt.setInt(3, idDepot);
        stmt.setInt(4, idCarriage);
    }
    /**
     * Popola un PreparedStatement per la query findAvailableCarriagesForConvoy.
     * @param stmt il PreparedStatement da popolare
     * @param idStation id della stazione/deposito
     * @param modelType tipo di modello
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatementForFindAvailableCarriagesForConvoy(PreparedStatement stmt, int idStation, String modelType) throws SQLException {
        stmt.setInt(1, idStation);
        stmt.setInt(2, idStation);
        stmt.setString(3, modelType);
    }

    /**
     * Popola un PreparedStatement per la query findAvailableCarriageTypesForConvoy.
     * @param stmt il PreparedStatement da popolare
     * @param idStation id della stazione/deposito
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatementForFindAvailableCarriageTypesForConvoy(PreparedStatement stmt, int idStation) throws SQLException {
        stmt.setInt(1, idStation);
        stmt.setInt(2, idStation);
    }

    /**
     * Popola un PreparedStatement per la query findAvailableCarriageModelsForConvoy.
     * @param stmt il PreparedStatement da popolare
     * @param idStation id della stazione/deposito
     * @param modelType tipo di modello
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatementForFindAvailableCarriageModelsForConvoy(PreparedStatement stmt, int idStation, String modelType) throws SQLException {
        stmt.setInt(1, idStation);
        stmt.setString(2, modelType);
    }

    /**
     * Popola un PreparedStatement per la query getCarriagesByConvoyPosition.
     * @param stmt il PreparedStatement da popolare
     * @param idConvoy id del convoglio
     * @throws SQLException se si verifica un errore di accesso al database
     */
    public static void toPreparedStatementForGetCarriagesByConvoyPosition(PreparedStatement stmt, int idConvoy) throws SQLException {
        stmt.setInt(1, idConvoy);
        stmt.setInt(2, idConvoy);
    }
}
