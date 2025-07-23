package mapper;

import domain.ConvoyPool;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvoyPoolMapper {
    public static ConvoyPool toDomain(ResultSet rs) throws SQLException {
        return ConvoyPool.of(
            rs.getInt("id_convoy"),
            rs.getInt("id_station"),
            ConvoyPool.ConvoyStatus.valueOf(rs.getString("status").trim().toUpperCase())
        );
    }
}

