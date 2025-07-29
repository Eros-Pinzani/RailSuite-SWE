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
    StaffDaoImp(){}

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
}
