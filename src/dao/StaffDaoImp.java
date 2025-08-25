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

    private static final String CHECK_OPERATOR_AVAILABILITY_SQL = """
            SELECT s.*
            FROM staff s
            WHERE s.type_of_staff = 'OPERATOR'
              AND NOT EXISTS (
                SELECT 1
                FROM run r
                WHERE r.id_staff = s.id_staff
                  AND (
                    (r.time_departure <= ? AND (r.time_departure + INTERVAL '1 hour') > ?)
                    OR (ABS(EXTRACT(EPOCH FROM (r.time_departure - ?)) / 60) < 15)
                    OR (SELECT SUM(EXTRACT(EPOCH FROM (r.time_arrival - r.time_departure)) / 3600)
                        FROM run r2
                        WHERE r2.id_staff = s.id_staff
                        AND DATE(r2.time_departure) = DATE(?)
                    ) > 10
                  )
              )
              AND EXISTS (
                SELECT 1
                FROM run r2
                WHERE r2.id_staff = s.id_staff
                  AND r2.id_last_station = ?
                  AND r2.time_arrival <= ?
                  AND (r2.time_arrival + INTERVAL '15 minutes') <= ?
              )""";
    @Override
    public List<Staff> checkOperatorAvailability( int firstStation, Timestamp timeDeparture) throws SQLException {
        List<Staff> availableStaff = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_OPERATOR_AVAILABILITY_SQL)) {
            stmt.setTimestamp(1, timeDeparture);
            stmt.setTimestamp(2, timeDeparture);
            stmt.setTimestamp(3, timeDeparture);
            stmt.setTimestamp(4, timeDeparture);
            stmt.setInt(5, firstStation);
            stmt.setTimestamp(6, timeDeparture);
            stmt.setTimestamp(7, timeDeparture);
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
