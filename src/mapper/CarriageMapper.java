package mapper;

import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageMapper {
    public static Carriage toDomain(ResultSet rs) throws SQLException {
        Integer idConvoy = null;
        try {
            idConvoy = rs.getObject("id_convoy") != null ? rs.getInt("id_convoy") : null;
        } catch (SQLException | IllegalArgumentException e) {
            //
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
