package mapper;

import domain.CarriageDepot;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageDepotMapper {
    public static CarriageDepot toDomain(ResultSet rs) throws SQLException {
        return CarriageDepot.of(
            rs.getInt("id_depot"),
            rs.getInt("id_carriage"),
            rs.getTimestamp("time_entered"),
            rs.getTimestamp("time_exited"),
            CarriageDepot.StatusOfCarriage.valueOf(rs.getString("status_of_carriage"))
        );
    }
}

