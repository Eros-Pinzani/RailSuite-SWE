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
}
