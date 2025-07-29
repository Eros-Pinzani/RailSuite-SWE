package dao;

import domain.ConvoyPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ConvoyPoolDao interface.
 * Contains SQL queries and logic for accessing convoy pool data.
 */
public class ConvoyPoolDaoImp implements ConvoyPoolDao {
    /**
     * SQL query to select a convoy pool by id.
     */
    private static final String SELECT_BY_ID =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_convoy = ?";
    /**
     * SQL query to update a convoy pool.
     */
    private static final String UPDATE =
            "UPDATE convoy_pool SET id_station = ?, status = ? WHERE id_convoy = ?";
    /**
     * SQL query to select all convoy pools.
     */
    private static final String SELECT_ALL =
            "SELECT id_convoy, id_station, status FROM convoy_pool";
    /**
     * SQL query to select convoy pools by station.
     */
    private static final String SELECT_BY_STATION =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_station = ?";
    /**
     * SQL query to select convoy pools by status.
     */
    private static final String SELECT_BY_STATUS =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE status = ?";
    /**
     * SQL query to select convoy pools by station and status.
     */
    private static final String SELECT_BY_STATION_AND_STATUS =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_station = ? AND status = ?";
    /**
     * SQL query to select table data by station.
     */
    private static final String SELECT_TABLE_DATA_BY_STATION =
        "SELECT cp.id_convoy, cp.status, COUNT(c.id_carriage) as carriage_count, " +
        "COALESCE(string_agg(DISTINCT c.model_type, ','), '') as types " +
        "FROM convoy_pool cp " +
        "LEFT JOIN carriage c ON c.id_convoy = cp.id_convoy " +
        "WHERE cp.id_station = ? " +
        "GROUP BY cp.id_convoy, cp.status";
    /**
     * SQL query to insert a new convoy pool.
     */
    private static final String INSERT =
        "INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?)";

    ConvoyPoolDaoImp (){}

    @Override
    public ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idConvoy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.ConvoyPoolMapper.toDomain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void updateConvoyPool(ConvoyPool convoyPool) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setInt(1, convoyPool.getIdStation());
            ps.setString(2, convoyPool.getConvoyStatus().name());
            ps.setInt(3, convoyPool.getIdConvoy());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ConvoyPool> getAllConvoyPools() throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapper.ConvoyPoolMapper.toDomain(rs));
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION)) {
            ps.setInt(1, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION_AND_STATUS)) {
            ps.setInt(1, idStation);
            ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<domain.ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException {
        List<domain.ConvoyTableDTO> result = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TABLE_DATA_BY_STATION)) {
            ps.setInt(1, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idConvoy = rs.getInt("id_convoy");
                    String status = rs.getString("status");
                    int carriageCount = rs.getInt("carriage_count");
                    String type = rs.getString("types");
                    result.add(new domain.ConvoyTableDTO(idConvoy, type, status, carriageCount));
                }
            }
        }
        return result;
    }

    @Override
    public void insertConvoyPool(domain.ConvoyPool pool) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setInt(1, pool.getIdConvoy());
            ps.setInt(2, pool.getIdStation());
            ps.setString(3, pool.getConvoyStatus().name());
            ps.executeUpdate();
        }
    }

    @Override
    public List<domain.Convoy> findAvailableConvoysForRun(int idLine, String direction, java.time.LocalDate date, String time) {
        List<domain.Convoy> result = new ArrayList<>();
        try {
            // Calcolo la stazione di testa in base a linea/direzione
            List<domain.LineStation> stations = dao.LineStationDao.of().findByLine(idLine);
            if (stations == null || stations.isEmpty()) return result;
            int headStationId = direction.equalsIgnoreCase("Andata") ? stations.get(0).getStationId() : stations.get(stations.size() - 1).getStationId();
            // Query ottimizzata: convogli nella stazione di testa, status DEPOT o WAITING, non assegnati a run sovrapposte, non in manutenzione
            String sql = "SELECT DISTINCT c.id_convoy FROM convoy_pool cp " +
                    "JOIN convoy c ON c.id_convoy = cp.id_convoy " +
                    "WHERE cp.id_station = ? " +
                    "AND cp.status IN ('DEPOT', 'WAITING') " +
                    "AND c.id_convoy NOT IN ( " +
                    "    SELECT r.id_convoy FROM run r " +
                    "    WHERE r.id_line = ? AND r.time_departure <= ?::time AND r.time_arrival >= ?::time AND r.id_convoy = cp.id_convoy " +
                    ") " +
                    "AND c.id_convoy NOT IN ( " +
                    "    SELECT ca.id_convoy FROM carriage ca " +
                    "    JOIN carriage_depot cd ON ca.id_carriage = cd.id_carriage " +
                    "    WHERE cd.status_of_carriage = 'MAINTENANCE' AND cd.time_exited IS NULL " +
                    ")";
            try (Connection conn = dao.PostgresConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, headStationId);
                ps.setInt(2, idLine);
                ps.setString(3, time);
                ps.setString(4, time);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idConvoy = rs.getInt("id_convoy");
                        // Recupera le carriages associate
                        List<domain.Carriage> carriages = dao.CarriageDao.of().selectCarriagesByConvoyId(idConvoy);
                        result.add(mapper.ConvoyMapper.toDomain(rs, carriages));
                    }
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(ConvoyPoolDaoImp.class.getName()).severe("Errore ricerca convogli disponibili: " + e.getMessage());
        }
        return result;
    }
}
