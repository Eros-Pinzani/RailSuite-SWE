package mapper;

import domain.StaffPool;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class StaffPoolMapper {
    public static StaffPool toDomain(ResultSet rs) throws SQLException {
        return StaffPool.of(
            rs.getInt("id_staff"),
            rs.getInt("id_station"),
            rs.getInt("id_convoy"),
            rs.getTimestamp("shift_start"),
            rs.getTimestamp("shift_end"),
            StaffPool.ShiftStatus.valueOf(rs.getString("shift_status").trim().toUpperCase())
        );
    }
}

