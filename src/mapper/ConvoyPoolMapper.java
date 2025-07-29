package mapper;

import domain.ConvoyPool;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvoyPoolMapper {
    /**
     * Maps a ResultSet row to a ConvoyPool domain object.
     * @param rs the ResultSet containing convoy pool data
     * @return a ConvoyPool domain object
     * @throws SQLException if a database access error occurs
     */
    public static ConvoyPool toDomain(ResultSet rs) throws SQLException {
        return ConvoyPool.of(
            rs.getInt("id_convoy"),
            rs.getInt("id_station"),
            ConvoyPool.ConvoyStatus.valueOf(rs.getString("status").trim().toUpperCase())
        );
    }
}
