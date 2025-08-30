package mapper;

import domain.LineStation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.Duration;

public class LineStationMapper {
    /**
     * Parses a PostgreSQL interval string into a Duration object.
     * @param intervalStr the interval string to parse
     * @return the corresponding Duration object, or null if the input is null
     */
    public static Duration parseDurationFromPgInterval(String intervalStr) {
        if (intervalStr == null) return null;
        String[] parts = intervalStr.split(":");
        int hours = 0, minutes = 0, seconds = 0;
        if (parts.length == 3) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
            seconds = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
        } else if (parts.length == 1) {
            hours = Integer.parseInt(parts[0]);
        }
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * Maps a ResultSet row to a LineStation domain object.
     * @param rs the ResultSet containing line station data
     * @return a LineStation domain object
     * @throws SQLException if a database access error occurs
     */
    public static LineStation toDomain(ResultSet rs) throws SQLException {
        String intervalStr = rs.getString("time_to_next_station");
        Duration duration = parseDurationFromPgInterval(intervalStr);
        return LineStation.of(
            rs.getInt("id_station"),
            rs.getInt("station_order"),
            duration
        );
    }

    /**
     * Sets the line ID in a PreparedStatement.
     * @param stmt the PreparedStatement to set the parameter in
     * @param idLine the line ID to set
     * @throws SQLException if a database access error occurs
     */
    public static void setIdLine(PreparedStatement stmt, int idLine) throws SQLException {
        stmt.setInt(1, idLine);
    }

    /**
     * Sets the parameters for the findTimeTableForRun query.
     * @param stmt the PreparedStatement to set the parameters in
     * @param idLine the line ID
     * @param idStartStation the start station ID
     * @throws SQLException if a database access error occurs
     */
    public static void setFindTimeTableForRunParams(PreparedStatement stmt, int idLine, int idStartStation) throws SQLException {
        stmt.setInt(1, idLine);
        stmt.setInt(2, idStartStation);
        stmt.setInt(3, idLine);
    }
}
