package dao;

import domain.StaffPool;
import domain.DTO.StaffDTO;

import java.sql.*;
import java.util.List;

/**
 * Implementation of the StaffPoolDao interface.
 * Contains SQL queries and logic for accessing staff pool data.
 */
public class StaffPoolDaoImp implements StaffPoolDao {
    // SQL query to select a StaffPool entry by staff id
    private static final String SELECT_BY_ID =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE id_staff = ?";
    // SQL query to update a StaffPool entry
    private static final String UPDATE =
            "UPDATE staff_pool SET id_station = ?, id_convoy = ?, shift_start = ?, shift_end = ?, status = ?" +
                    "WHERE id_staff = ?";
    // SQL query to select StaffPool entries by station
    private static final String SELECT_BY_STATION =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE id_station = ?";
    // SQL query to select StaffPool entries by status
    private static final String SELECT_BY_STATUS =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE status = ?";
    // SQL query to select StaffPool entries by status and station
    private static final String SELECT_BY_STATUS_AND_STATION =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE status = ? AND id_station = ?";
    private static final String SELECT_AVAILABLE_OPERATORS_FOR_RUN = """
            SELECT
                s.id_staff,
                s.name,
                s.surname
            FROM staff_pool sp
            JOIN staff s ON s.id_staff = sp.id_staff
            WHERE sp.id_station = ?
              AND s.type_of_staff = 'OPERATOR'
              AND NOT EXISTS (
                    SELECT 1
                    FROM run r
                    WHERE r.id_staff = s.id_staff
                      AND DATE(r.time_departure) = ?
                      AND (
                            (r.time_departure <= (?::timestamp + INTERVAL '15 minutes')
                             AND r.time_arrival >= (?::timestamp - INTERVAL '15 minutes'))
                         OR
                            (r.time_departure <= ((?::timestamp + INTERVAL '1 hour') + INTERVAL '15 minutes')
                             AND r.time_arrival >= ((?::timestamp + INTERVAL '1 hour') - INTERVAL '15 minutes'))
                      )
                )
              AND (
                    SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (r.time_arrival - r.time_departure)))/3600, 0)
                    FROM run r
                    WHERE r.id_staff = s.id_staff
                      AND DATE(r.time_departure) = ?
                ) < 12
            ORDER BY s.surname, s.name;
            """;

    StaffPoolDaoImp() {
    }

    @Override
    public StaffPool findById(int idStaff) throws SQLException {
        // Executes the query to get a StaffPool entry by staff id
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idStaff);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.StaffPoolMapper.toDomain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<StaffPool> findByStation(int idStation) throws SQLException {
        // Executes the query to get all StaffPool entries for a station
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION)) {
            ps.setInt(1, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                List<StaffPool> result = new java.util.ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.StaffPoolMapper.toDomain(rs));
                }
                return result;
            }
        }
    }

    @Override
    public void update(StaffPool staffPool) throws SQLException {
        // Executes the query to update a StaffPool entry
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setInt(1, staffPool.getIdStation());
            ps.setInt(2, staffPool.getIdConvoy());
            ps.setTimestamp(3, staffPool.getShiftStart());
            ps.setTimestamp(4, staffPool.getShiftEnd());
            ps.setString(5, staffPool.getShiftStatus().name());
            ps.setInt(6, staffPool.getIdStaff());
            ps.executeUpdate();
        }
    }

    @Override
    public List<StaffPool> findByStatus(ShiftStatus status) throws SQLException {
        // Executes the query to get all StaffPool entries by status
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                List<StaffPool> result = new java.util.ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.StaffPoolMapper.toDomain(rs));
                }
                return result;
            }
        }
    }

    @Override
    public List<StaffPool> findByStatusAndStation(ShiftStatus status, int idStation) throws SQLException {
        // Executes the query to get all StaffPool entries by status and station
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS_AND_STATION)) {
            ps.setString(1, status.name());
            ps.setInt(2, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                List<StaffPool> result = new java.util.ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.StaffPoolMapper.toDomain(rs));
                }
                return result;
            }
        }
    }

    @Override
    public List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate date, String time) {
        List<domain.DTO.StaffDTO> result = new java.util.ArrayList<>();

        try (Connection conn = dao.PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_AVAILABLE_OPERATORS_FOR_RUN)) {
            ps.setInt(1, idStation);
            ps.setDate(2, java.sql.Date.valueOf(date));
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(date, java.time.LocalTime.parse(time));
            java.sql.Timestamp departureTimestamp = java.sql.Timestamp.valueOf(dateTime);
            ps.setTimestamp(3, departureTimestamp);
            ps.setTimestamp(4, departureTimestamp);
            ps.setTimestamp(5, departureTimestamp);
            ps.setDate(6, java.sql.Date.valueOf(date));
            ps.setDate(7, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            new StaffDTO(
                                    rs.getInt("id_staff"),
                                    rs.getString("name"),
                                    rs.getString("surname")
                            )
                    );
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(StaffPoolDaoImp.class.getName()).severe("Error searching for available operators: " + e.getMessage());
        }
        return result;
    }
}
