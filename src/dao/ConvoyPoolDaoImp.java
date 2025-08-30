package dao;

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
     * SQL query per controllare lo stato del convoglio (manutenzione o non assegnato a corse).
     */
    private static final String checkConvoyStatus = """
            SELECT
                EXISTS (
                    SELECT 1
                    FROM carriage c
                    JOIN carriage_depot cd ON c.id_carriage = cd.id_carriage
                    WHERE c.id_convoy = ?
                      AND cd.status_of_carriage IN ('MAINTENANCE', 'CLEANING')
                      AND cd.time_exited IS NULL
                ) AS in_maintenance,
                NOT EXISTS (
                    SELECT 1
                    FROM run
                    WHERE id_convoy = ?
                      AND now() BETWEEN time_departure AND time_arrival
                ) AS not_on_run
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
            mapper.ConvoyPoolMapper.setIdConvoy(ps, idConvoy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.ConvoyPoolMapper.toDomain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException {
        List<ConvoyTableDTO> result = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TABLE_DATA_BY_STATION)) {
            mapper.ConvoyPoolMapper.setIdStation(ps, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.ConvoyPoolMapper.toConvoyTableDTO(rs));
                }
            }
        }
        return result;
    }

    @Override
    public void insertConvoyPool(domain.ConvoyPool pool) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            mapper.ConvoyPoolMapper.setInsertConvoyPool(ps, pool);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean checkAndUpdateConvoyStatus(int idConvoy) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkConvoyStatus)) {
            mapper.ConvoyPoolMapper.setCheckConvoyStatus(ps, idConvoy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean inMaintenance = rs.getBoolean("in_maintenance");
                    boolean notOnRun = rs.getBoolean("not_on_run");
                    return inMaintenance || notOnRun;
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("pluto");
            throw new SQLException("Error checking convoy status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ConvoyTableDTO> checkConvoyAvailability(int idStation) throws SQLException {
        List<ConvoyTableDTO> availableConvoys = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection()) {
            try (PreparedStatement psUpdate = conn.prepareStatement(updateConvoyStatus)) {
                mapper.ConvoyPoolMapper.setIdStation(psUpdate, idStation);
                psUpdate.executeUpdate();
            }
            try {
                availableConvoys = getConvoyTableDataByStation(idStation);
            } catch (SQLException e) {
                throw new SQLException("Error retrieving convoy table data", e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error checking convoy availability", e);
        }
        return availableConvoys;
    }

}
