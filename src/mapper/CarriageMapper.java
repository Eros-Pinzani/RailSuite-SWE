package mapper;

import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageMapper {
    public static Carriage toDomain(ResultSet rs) throws SQLException {
        return Carriage.of(
            rs.getInt("id_carriage"),
            rs.getString("model"),
            rs.getString("model_type"),
            rs.getInt("year_produced"),
            rs.getInt("capacity")
        );
    }
}
