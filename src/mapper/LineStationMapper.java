package mapper;

import domain.LineStation;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LineStationMapper {
    public static LineStation toDomain(ResultSet rs) throws SQLException {
        return LineStation.of(
            rs.getInt("id_station"),
            rs.getInt("station_order"),
            java.time.Duration.ofMinutes(rs.getInt("time_to_next_station"))
        );
    }
}
