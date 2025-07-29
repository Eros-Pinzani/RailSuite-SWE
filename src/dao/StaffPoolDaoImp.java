package dao;

import domain.StaffPool;

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

    StaffPoolDaoImp() {}

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

}
