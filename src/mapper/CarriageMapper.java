package mapper;

import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageMapper {
    public static Carriage toDomain(ResultSet rs) throws SQLException {
        return Carriage.of(
            rs.getInt("id"),
            rs.getString("model"),
            rs.getString("modelType"),
            rs.getInt("yearProduced"),
            rs.getInt("capacity")
        );
    }
}
