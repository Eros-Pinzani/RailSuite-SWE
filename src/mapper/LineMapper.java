package mapper;

import domain.Line;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LineMapper {
    /**
     * Maps a ResultSet row and a list of LineStation to a Line domain object.
     * @param rs the ResultSet containing line data
     * @param stations the list of stations for the line
     * @return a Line domain object
     * @throws SQLException if a database access error occurs
     */
    public static Line toDomain(ResultSet rs, List<domain.LineStation> stations) throws SQLException {
        return Line.of(
            rs.getInt("id_line"),
            rs.getString("name"),
            stations
        );
    }
}
