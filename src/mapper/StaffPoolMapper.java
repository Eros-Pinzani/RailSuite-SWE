package mapper;

import domain.StaffPool;
import domain.DTO.StaffDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

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

    /**
     * Maps a ResultSet row to a StaffDTO object.
     * @param rs the ResultSet containing staff data
     * @return a StaffDTO object
     * @throws SQLException if a database access error occurs
     */
    public static StaffDTO toStaffDTO(ResultSet rs) throws SQLException {
        return new StaffDTO(
            rs.getInt("id_staff"),
            rs.getString("name"),
            rs.getString("surname")
        );
    }

    /**
     * Sets the parameters for the PreparedStatement to find available operators.
     * @param ps the PreparedStatement
     * @param idStation the station ID
     * @param date the date
     * @param departureTimestamp the departure timestamp
     * @throws SQLException if a database access error occurs
     */
    public static void setFindAvailableOperatorsParams(PreparedStatement ps, int idStation, java.time.LocalDate date, java.sql.Timestamp departureTimestamp) throws SQLException {
        ps.setInt(1, idStation);
        ps.setDate(2, java.sql.Date.valueOf(date));
        ps.setTimestamp(3, departureTimestamp);
        ps.setTimestamp(4, departureTimestamp);
        ps.setTimestamp(5, departureTimestamp);
        ps.setDate(6, java.sql.Date.valueOf(date));
        ps.setDate(7, java.sql.Date.valueOf(date));
    }
}
