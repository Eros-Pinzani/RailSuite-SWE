package mapper;

import domain.Line;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LineMapper {
    /**
     * Mappa una riga del ResultSet in un oggetto Line.
     */
    public static Line toDomain(ResultSet rs) throws SQLException {
        return Line.of(
                rs.getInt("id_line"),
                rs.getString("name"),
                rs.getInt("id_first_station"),
                rs.getString("first_station_location"),
                rs.getInt("id_last_station"),
                rs.getString("last_station_location")
        );
    }
}
