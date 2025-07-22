package dao;

import domain.StaffPool;

import java.sql.*;
import java.util.List;

public class StaffPoolDaoImp implements StaffPoolDao {
    private static final String SELECT_BY_ID =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE id_staff = ?";
    private static final String UPDATE =
            "UPDATE staff_pool SET id_station = ?, id_convoy = ?, shift_start = ?, shift_end = ?, status = ?" +
            "WHERE id_staff = ?";
    private static final String SELECT_BY_STATION =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE id_station = ?";
    private static final String SELECT_BY_STATUS =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE status = ?";
    private static final String SELECT_BY_STATUS_AND_STATION =
            "SELECT id_staff, id_station, id_convoy, shift_start, shift_end, status FROM staff_pool WHERE status = ? AND id_station = ?";

    @Override
    public StaffPool findById(int idStaff) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idStaff);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idStation = rs.getInt("id_station");
                    int idConvoy = rs.getInt("id_convoy");
                    Timestamp shiftStart = rs.getTimestamp("shift_start");
                    Timestamp shiftEnd = rs.getTimestamp("shift_end");
                    StaffPool.ShiftStatus shiftStatus = StaffPool.ShiftStatus.valueOf(rs.getString("status"));
                    return StaffPool.of(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus);
                }
            }
        }
        return null;
    }

    @Override
    public List<StaffPool> findByStation(int idStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION)) {
            ps.setInt(1, idStation);
            return getStaffPools(idStation, ps);
        }
    }

    @Override
    public void update(StaffPool staffPool) throws SQLException {
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
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                List<StaffPool> result = new java.util.ArrayList<>();
                while (rs.next()) {
                    int idStaff = rs.getInt("id_staff");
                    int idStation = rs.getInt("id_station");
                    int idConvoy = rs.getInt("id_convoy");
                    Timestamp shiftStart = rs.getTimestamp("shift_start");
                    Timestamp shiftEnd = rs.getTimestamp("shift_end");
                    StaffPool.ShiftStatus shiftStatus = StaffPool.ShiftStatus.valueOf(rs.getString("status"));
                    result.add(StaffPool.of(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus));
                }
                return result;
            }
        }
    }

    @Override
    public List<StaffPool> findByStatusAndStation(ShiftStatus status, int idStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS_AND_STATION)) {
            ps.setString(1, status.name());
            ps.setInt(2, idStation);
            return getStaffPools(idStation, ps);
        }
    }

    private List<StaffPool> getStaffPools(int idStation, PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<StaffPool> result = new java.util.ArrayList<>();
            while (rs.next()) {
                int idStaff = rs.getInt("id_staff");
                int idConvoy = rs.getInt("id_convoy");
                Timestamp shiftStart = rs.getTimestamp("shift_start");
                Timestamp shiftEnd = rs.getTimestamp("shift_end");
                StaffPool.ShiftStatus shiftStatus = StaffPool.ShiftStatus.valueOf(rs.getString("status"));
                result.add(StaffPool.of(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus));
            }
            return result;
        }
    }


}
