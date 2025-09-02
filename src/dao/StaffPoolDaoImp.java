package dao;


import java.sql.*;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementation of the StaffPoolDao interface.
 * Contains SQL queries and logic for accessing staff pool data.
 */
public class StaffPoolDaoImp implements StaffPoolDao {
    private static final String SELECT_AVAILABLE_OPERATORS_FOR_RUN = """
            (SELECT s.id_staff, s.name, s.surname
             FROM staff_pool sp JOIN staff s ON s.id_staff = sp.id_staff
             WHERE sp.id_station = ? AND s.type_of_staff = 'OPERATOR'
                  AND NOT EXISTS (SELECT 1
                                  FROM run r
                                  WHERE r.id_staff = s.id_staff AND DATE(r.time_departure) = ?)
             ORDER BY s.surname, s.name)
            UNION
            (SELECT s.id_staff, s.name, s.surname
             FROM staff_pool sp JOIN staff s ON s.id_staff = sp.id_staff
             WHERE sp.id_station = ? AND s.type_of_staff = 'OPERATOR'
                AND NOT EXISTS (SELECT 1
                                FROM run r
                                WHERE r.id_staff = s.id_staff AND DATE(r.time_departure) = ?
                                    AND (r.time_departure = ?::timestamp
                                    OR r.time_departure = (SELECT MIN(r2.time_departure)
                                                            FROM run r2
                                                            WHERE r2.id_staff = s.id_staff
                                                                AND DATE(r2.time_departure) = ?)
                                    AND (?::timestamp - r.time_departure) >= INTERVAL '8 hours'
                                    OR (?::timestamp - r.time_arrival) < INTERVAL '15 minutes'
                                    OR r.time_departure > ?::timestamp
            )))
            """;

    StaffPoolDaoImp() {
    }


    @Override
    public List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate date, String time) {
        List<domain.DTO.StaffDTO> result = new java.util.ArrayList<>();
        try (Connection conn = dao.PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_AVAILABLE_OPERATORS_FOR_RUN)) {
            LocalTime localTime = LocalTime.parse(time);
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(date, localTime);
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
