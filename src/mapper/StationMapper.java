package mapper;

import domain.Station;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StationMapper {
    /**
     * Maps a ResultSet row to a Station domain object.
     * @param rs the ResultSet containing station data
     * @return a Station domain object
     * @throws SQLException if a database access error occurs
     */
    public static Station toDomain(ResultSet rs) throws SQLException {
        return Station.of(
            rs.getInt("id_station"),
            rs.getString("location"),
            rs.getInt("num_bins"),
            rs.getString("service_description"),
            rs.getBoolean("is_head")
        );
    }
}
