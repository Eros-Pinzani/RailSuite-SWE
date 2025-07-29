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

    @Override
    public List<domain.Staff> findAvailableOperatorsForRun(int idLine, String direction, java.time.LocalDate date, String time) {
        List<domain.Staff> result = new java.util.ArrayList<>();
        try {
            // Calcolo la stazione di testa in base a linea/direzione
            java.util.List<domain.LineStation> stations = dao.LineStationDao.of().findByLine(idLine);
            if (stations == null || stations.isEmpty()) return result;
            int headStationId = direction.equalsIgnoreCase("Andata") ? stations.getFirst().getStationId() : stations.getLast().getStationId();
            // Query ottimizzata: operatori OPERATOR, nella stazione di testa, status AVAILABLE, senza run sovrapposte, rispetto limiti temporali
            String sql = "SELECT s.* FROM staff_pool sp " +
                    "JOIN staff s ON s.id_staff = sp.id_staff " +
                    "WHERE sp.id_station = ? " +
                    "AND sp.status = 'AVAILABLE' " +
                    "AND s.type_of_staff = 'OPERATOR' " +
                    // Esclude operatori con run sovrapposte
                    "AND s.id_staff NOT IN ( " +
                    "    SELECT r.id_staff FROM run r " +
                    "    WHERE r.time_departure <= ?::time AND r.time_arrival >= ?::time AND r.id_staff = sp.id_staff " +
                    "    AND r.time_departure::date = ?::date " +
                    ") " +
                    // Limite: almeno 15 minuti tra servizi e max 12h totali nella giornata
                    "AND ( " +
                    "    (SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (r.time_arrival - r.time_departure))/3600),0) FROM run r WHERE r.id_staff = sp.id_staff AND r.time_departure::date = ?::date) < 12 " +
                    "    AND NOT EXISTS ( " +
                    "        SELECT 1 FROM run r WHERE r.id_staff = sp.id_staff AND r.time_departure::date = ?::date " +
                    "        AND (ABS(EXTRACT(EPOCH FROM (?::time - r.time_arrival))/60) < 15 OR ABS(EXTRACT(EPOCH FROM (r.time_departure - ?::time))/60) < 15) " +
                    "    ) " +
                    ") ";
            try (Connection conn = dao.PostgresConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, headStationId);
                ps.setString(2, time);
                ps.setString(3, time);
                ps.setDate(4, java.sql.Date.valueOf(date));
                ps.setDate(5, java.sql.Date.valueOf(date));
                ps.setDate(6, java.sql.Date.valueOf(date));
                ps.setString(7, time);
                ps.setString(8, time);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(mapper.StaffMapper.toDomain(rs));
                    }
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(StaffPoolDaoImp.class.getName()).severe("Errore ricerca operatori disponibili: " + e.getMessage());
        }
        return result;
    }
}
