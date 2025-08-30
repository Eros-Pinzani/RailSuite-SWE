package mapper;

import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageMapper {
    /**
     * Maps a ResultSet row to a Carriage domain object.
     * Handles possible null for id_convoy.
     * @param rs the ResultSet containing carriage data
     * @return a Carriage domain object
     * @throws SQLException if a database access error occurs
     */
    public static Carriage toDomain(ResultSet rs) throws SQLException {
        Integer idConvoy = null;
        try {
            idConvoy = rs.getObject("id_convoy") != null ? rs.getInt("id_convoy") : null;
        } catch (SQLException | IllegalArgumentException e) {
            // id_convoy non presente o non valido, lasciamo null
        }
        return Carriage.of(
            rs.getInt("id_carriage"),
            rs.getString("model"),
            rs.getString("model_type"),
            rs.getInt("year_produced"),
            rs.getInt("capacity"),
            idConvoy
        );
    }
}