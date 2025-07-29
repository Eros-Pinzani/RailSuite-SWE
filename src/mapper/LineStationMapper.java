package mapper;

import domain.LineStation;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LineStationMapper {
    /**
     * Maps a ResultSet row to a LineStation domain object.
     * @param rs the ResultSet containing line station data
     * @return a LineStation domain object
     * @throws SQLException if a database access error occurs
     */
    public static LineStation toDomain(ResultSet rs) throws SQLException {
        String intervalStr = rs.getString("time_to_next_station");
        java.time.Duration duration = null;
        if (intervalStr != null) {
            // Handle hh:mm:ss format
            String[] parts = intervalStr.split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = (int) Double.parseDouble(parts[2]);
                duration = java.time.Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            }
        }
        return LineStation.of(
            rs.getInt("id_station"),
            rs.getInt("station_order"),
            duration
        );
    }
}
