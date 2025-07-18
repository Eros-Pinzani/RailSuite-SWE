package dao;

import domain.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class StaffDaoImp implements StaffDao {
    private static Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff.TypeOfStaff typeOfStaff = Staff.TypeOfStaff.valueOf(rs.getString("type_of_staff").trim().toUpperCase());
        return Staff.of(
            rs.getInt("id_staff"),
            rs.getString("name"),
            rs.getString("surname"),
            rs.getString("address"),
            rs.getString("email"),
            rs.getString("password"),
            typeOfStaff
        );
    }

    @Override
    public Staff findById(int id) throws SQLException {
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
        String sql = "SELECT * FROM staff";
        try {
            return getStaffList(sql);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all staff", e);
        }
    }

    @Override
    public List<Staff> findByType(Staff.TypeOfStaff type) throws SQLException {
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
