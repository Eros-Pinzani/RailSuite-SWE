package dao;

import domain.Convoy;
import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ConvoyPoolDao interface.
 * Contains SQL queries and logic for accessing convoy pool data.
 */
class ConvoyPoolDaoImp implements ConvoyPoolDao {
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
    private static final String SELECT_TABLE_DATA_BY_STATION = """
            SELECT cp.id_convoy, cp.status, COUNT(c.id_carriage) as carriage_count, SUM(c.capacity),\s
                    COALESCE(string_agg(DISTINCT c.model_type, ','), '') as model_types,\s
                    c.model
                    FROM convoy_pool cp\s
                    LEFT JOIN carriage c ON c.id_convoy = cp.id_convoy\s
                    WHERE cp.id_station = ?\s
                    GROUP BY cp.id_convoy, cp.status, c.model
                   \s""";
    /**
     * SQL query to insert a new convoy pool.
     */
    private static final String INSERT =
            "INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?)";
    /**
     * SQL query to check the status of a convoy.
     */
    private static final String checkConvoyStatus = """
                SELECT EXISTS (
                SELECT 1
                FROM carriage c
                JOIN carriage_depot cd ON c.id_carriage = cd.id_carriage
                WHERE c.id_convoy = ?
                  AND cd.status_of_carriage IN ('MAINTENANCE', 'CLEANING')
                  AND cd.time_exited IS NULL
            ) AS in_maintenance
            UNION
            SELECT NOT EXISTS (
                SELECT 1
                FROM run
                WHERE id_convoy = ?
                  AND now() BETWEEN time_departure AND time_arrival
            ) AS not_on_run
            LIMIT 1
            """;
    private static final String updateConvoyStatus = """
            UPDATE convoy_pool cp
            SET status = CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM carriage c
                    JOIN carriage_depot cd ON c.id_carriage = cd.id_carriage
                    WHERE c.id_convoy = cp.id_convoy
                      AND cd.status_of_carriage IN ('MAINTENANCE', 'CLEANING')
                      AND cd.time_exited IS NULL
                ) THEN 'DEPOT'
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM run r
                    WHERE r.id_convoy = cp.id_convoy
                      AND now() BETWEEN r.time_departure AND r.time_arrival
                ) THEN 'WAITING'
                ELSE 'ON_RUN'
            END
            WHERE cp.id_station = ?
            """;
    ConvoyPoolDaoImp() {
    }

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
    public List<ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException {
        List<ConvoyTableDTO> result = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TABLE_DATA_BY_STATION)) {
            ps.setInt(1, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idConvoy = rs.getInt("id_convoy");
                    String status = rs.getString("status");
                    int carriageCount = rs.getInt("carriage_count");
                    int capacity = rs.getInt("sum");
                    String modelType = rs.getString("model_types");
                    String model = rs.getString("model");
                    result.add(new ConvoyTableDTO(idConvoy, model, status, carriageCount, capacity, modelType));
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
    public boolean checkAndUpdateConvoyStatus(int idConvoy) throws SQLException {
        try {
            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkConvoyStatus)) {
                ps.setInt(1, idConvoy);
                ps.setInt(2, idConvoy);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean inMaintenance = rs.getBoolean(1);
                        boolean notOnRun = rs.getBoolean(2);
                        return inMaintenance || notOnRun;
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error checking convoy status", e);
        }
    }

    @Override
    public List<ConvoyTableDTO> checkConvoyAvailability(int idStation) throws SQLException {
        List<ConvoyTableDTO> availableConvoys = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection()) {
            try (PreparedStatement psUpdate = conn.prepareStatement(updateConvoyStatus)) {
                psUpdate.setInt(1, idStation);
                psUpdate.executeUpdate();
            }
            try {
                getConvoyTableDataByStation(idStation);
            } catch (SQLException e){
                throw new SQLException("Error retrieving convoy table data", e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error checking convoy availability", e);
        }
        return availableConvoys;
    }

}
