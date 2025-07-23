package mapper;

import domain.Depot;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepotMapper {
    public static Depot toDomain(ResultSet rs) throws SQLException {
        return Depot.of(rs.getInt("id_depot"));
    }
}
