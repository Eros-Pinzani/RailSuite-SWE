package mapper;

import domain.Run;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RunMapper {
    public static Run toDomain(ResultSet rs) throws SQLException {
        return Run.of(
            rs.getInt("id_line"),
            rs.getInt("id_convoy"),
            rs.getInt("id_staff"),
            rs.getTime("time_departure"),
            rs.getTime("time_arrival"),
            rs.getInt("id_first_station"),
            rs.getInt("id_last_station")
        );
    }
}

