package dao;


import domain.DTO.StaffDTO;

import java.sql.*;
import java.util.List;

/**
 * Implementation of the StaffPoolDao interface.
 * Contains SQL queries and logic for accessing staff pool data.
 */
public class StaffPoolDaoImp implements StaffPoolDao {
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
    public List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate date, String time) {
        List<domain.DTO.StaffDTO> result = new java.util.ArrayList<>();
        try (Connection conn = dao.PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_AVAILABLE_OPERATORS_FOR_RUN)) {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(date, java.time.LocalTime.parse(time));
            java.sql.Timestamp departureTimestamp = java.sql.Timestamp.valueOf(dateTime);
            mapper.StaffPoolMapper.setFindAvailableOperatorsParams(ps, idStation, date, departureTimestamp);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.StaffPoolMapper.toStaffDTO(rs));
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(StaffPoolDaoImp.class.getName()).severe("Error searching for available operators: " + e.getMessage());
        }
        return result;
    }
}
