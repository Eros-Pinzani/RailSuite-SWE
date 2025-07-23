package mapper;

import domain.Line;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LineMapper {
    public static Line toDomain(ResultSet rs, List<domain.LineStation> stations) throws SQLException {
        return Line.of(
            rs.getInt("id_line"),
            rs.getString("name"),
            stations
        );
    }
}
