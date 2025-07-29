package mapper;

import domain.StaffPool;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffPoolMapper {
    /**
     * Maps a ResultSet row to a StaffPool domain object.
     * @param rs the ResultSet containing staff pool data
     * @return a StaffPool domain object
     * @throws SQLException if a database access error occurs
     */
    public static StaffPool toDomain(ResultSet rs) throws SQLException {
        return StaffPool.of(
            rs.getInt("id_staff"),
            rs.getInt("id_station"),
            rs.getInt("id_convoy"),
            rs.getTimestamp("shift_start"),
            rs.getTimestamp("shift_end"),
            StaffPool.ShiftStatus.valueOf(rs.getString("status").trim().toUpperCase())
        );
    }
}
