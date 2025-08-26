package dao;

import domain.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the StaffDao interface.
 * Contains SQL queries and logic for accessing staff data.
 */
class StaffDaoImp implements StaffDao {
    StaffDaoImp() {
    }

    private static Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        return mapper.StaffMapper.toDomain(rs);
    }

    @Override
    public Staff findById(int id) throws SQLException {
        // SQL query to get a staff member by id
        String sql = "SELECT * FROM staff WHERE id_staff = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding staff by ID: " + id, e);
        }
        return null;
    }

    @Override
    public Staff findByEmail(String email) throws SQLException {
        // SQL query to get a staff member by email
        String sql = "SELECT * FROM staff WHERE email = ? LIMIT 1";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding staff by email: " + email, e);
        }
        return null;
    }

    @Override
    public List<Staff> findAll() throws SQLException {
        // SQL query to get all staff members
        String sql = "SELECT * FROM staff";
        try {
            return getStaffList(sql);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all staff", e);
        }
    }

    @Override
    public List<Staff> findByType(Staff.TypeOfStaff type) throws SQLException {
        // SQL query to get all staff members by type
        String sql = "SELECT * FROM staff WHERE type_of_staff LIKE ?";
        try {
            return getStaffListWithType(sql, type);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving staff by type", e);
        }
    }

    private List<Staff> getStaffList(String sql) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving staff list", e);
        }
        return staffList;
    }

    private List<Staff> getStaffListWithType(String sql, Staff.TypeOfStaff type) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + type.name() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving staff list by type", e);
        }
        return staffList;
    }

    private void updateForCheckOperatorAvailability(int idStaff, int idLine, Timestamp timeDeparture) throws SQLException {
        String dateString = timeDeparture.toLocalDateTime().toLocalDate().toString();
        String timestampString = timeDeparture.toString();
        String noConstraintsSql = String.format("""
                CREATE OR REPLACE VIEW staff_no_run_no_shift AS
                SELECT s.*
                FROM staff s
                         JOIN staff_pool sp ON s.id_staff = sp.id_staff
                    AND sp.shift_end <= '%s'
                WHERE s.type_of_staff = 'OPERATOR'
                  AND s.id_staff <> %d
                ORDER BY s.id_staff;
                """, timestampString, idStaff);
        try (Connection conn = PostgresConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(noConstraintsSql);
        }
        // Costruisco la query staff_with_run_constraints come stringa, inserendo tutti i valori direttamente
        String constrintsSql = String.format("""
                CREATE OR REPLACE VIEW staff_with_run_constraints AS
                SELECT s.*
                FROM staff s
                         LEFT JOIN staff_pool sp ON s.id_staff = sp.id_staff
                    AND sp.shift_start <= '%1$s'
                    AND sp.shift_end >= '%1$s'
                WHERE s.type_of_staff = 'OPERATOR'
                  AND s.id_staff <> %2$d
                  AND EXISTS (
                    SELECT 1
                    FROM run r2
                    WHERE r2.id_staff = s.id_staff
                      AND r2.time_arrival <= '%1$s'
                      AND DATE(r2.time_arrival) = '%3$s'
                )
                  AND (
                    COALESCE(
                            (
                                SELECT r2.id_last_station
                                FROM run r2
                                WHERE r2.id_staff = s.id_staff
                                  AND r2.time_arrival <= '%1$s'
                                  AND DATE(r2.time_arrival) = '%3$s'
                                ORDER BY r2.time_arrival DESC
                                LIMIT 1
                            ),
                            sp.id_station
                    ) = (
                        SELECT r.id_first_station
                        FROM run r
                        WHERE r.id_staff = %2$d
                          AND r.id_line = %4$d
                          AND r.time_departure = '%1$s'
                        LIMIT 1
                    )
                    )
                  AND NOT EXISTS (
                    SELECT 1
                    FROM run r
                    WHERE r.id_staff = s.id_staff
                      AND (r.time_departure, r.time_arrival) OVERLAPS (
                                                                       '%1$s',
                                                                       (
                                                                           SELECT r_target.time_arrival
                                                                           FROM run r_target
                                                                           WHERE r_target.id_staff = %2$d
                                                                             AND r_target.id_line = %4$d
                                                                             AND r_target.time_departure = '%1$s'
                                                                           LIMIT 1
                                                                       )
                        )
                )
                  AND (
                          SELECT EXTRACT(EPOCH FROM ('%1$s' - COALESCE(MAX(r2.time_arrival), '%1$s'))) / 60
                          FROM run r2
                          WHERE r2.id_staff = s.id_staff
                            AND r2.time_arrival <= '%1$s'
                            AND DATE(r2.time_arrival) = '%3$s'
                      ) >= 15
                  AND (
                          SELECT EXTRACT(EPOCH FROM (
                              LEAST(
                                      COALESCE(MAX(r3.time_arrival),
                                               (
                                                   SELECT r_target.time_arrival
                                                   FROM run r_target
                                                   WHERE r_target.id_staff = %2$d
                                                     AND r_target.id_line = %4$d
                                                     AND r_target.time_departure = '%1$s'
                                                   LIMIT 1
                                               )
                                      ),
                                      (
                                          SELECT r_target.time_arrival
                                          FROM run r_target
                                          WHERE r_target.id_staff = %2$d
                                            AND r_target.id_line = %4$d
                                            AND r_target.time_departure = '%1$s'
                                          LIMIT 1
                                      )
                              )
                                  - GREATEST(
                                      COALESCE(MIN(r3.time_departure), '%1$s'),
                                      '%1$s'
                                    )
                              )) / 3600
                          FROM (
                                   SELECT r3.time_departure, r3.time_arrival
                                   FROM run r3
                                   WHERE r3.id_staff = s.id_staff
                                     AND DATE(r3.time_departure) = '%3$s'
                                   UNION ALL
                                   SELECT
                                       '%1$s' AS time_departure,
                                       (
                                           SELECT r_target.time_arrival
                                           FROM run r_target
                                           WHERE r_target.id_staff = %2$d
                                             AND r_target.id_line = %4$d
                                             AND r_target.time_departure = '%1$s'
                                           LIMIT 1
                                       ) AS time_arrival
                               ) r3
                      ) <= 10
                  AND (
                          SELECT CASE
                                     WHEN MIN(r4.time_departure) IS NULL THEN 99999
                                     ELSE EXTRACT(EPOCH FROM (
                                         MIN(r4.time_departure) - GREATEST(
                                                 (
                                                     SELECT COALESCE(MAX(r5.time_arrival),
                                                                     (
                                                                         SELECT r_target.time_arrival
                                                                         FROM run r_target
                                                                         WHERE r_target.id_staff = %2$d
                                                                           AND r_target.id_line = %4$d
                                                                           AND r_target.time_departure = '%1$s'
                                                                         LIMIT 1
                                                                     )
                                                            )
                                                     FROM run r5
                                                     WHERE r5.id_staff = s.id_staff
                                                       AND DATE(r5.time_arrival) = '%3$s'
                                                 ),
                                                 (
                                                     SELECT r_target.time_arrival
                                                     FROM run r_target
                                                     WHERE r_target.id_staff = %2$d
                                                       AND r_target.id_line = %4$d
                                                       AND r_target.time_departure = '%1$s'
                                                     LIMIT 1
                                                 )
                                                                  )
                                         )) / 3600
                                     END
                          FROM run r4
                          WHERE r4.id_staff = s.id_staff
g                            AND DATE(r4.time_departure) = DATE '%3$s' + INTERVAL '1 day'
                      ) >= 14
                ORDER BY s.id_staff;""",
                timestampString, idStaff, dateString, idLine);
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(constrintsSql);
        }
    }

    @Override
    public List<Staff> checkOperatorAvailability(int idStaff, int idLine, Timestamp timeDeparture) throws SQLException {
        updateForCheckOperatorAvailability(idStaff, idLine, timeDeparture);
        String sql = """
                SELECT * FROM staff_with_run_constraints
                UNION ALL
                SELECT * FROM staff_no_run_no_shift
                ORDER BY id_staff;
                """;

        List<Staff> availableStaff = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableStaff.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel controllo disponibilità operatori", e);
        }
        return availableStaff;
    }
}
