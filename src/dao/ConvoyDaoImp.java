package dao;

import domain.Carriage;
import domain.Convoy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ConvoyDao interface.
 * Contains SQL queries and logic for accessing convoy data.
 */
class ConvoyDaoImp implements ConvoyDao {
    /**
     * SQL query to select a convoy by id.
     */
    private static final String selectConvoyQuery =
            "SELECT id_convoy FROM convoy WHERE id_convoy = ?";
    /**
     * SQL query to select carriages by convoy id.
     */
    private static final String selectCarriagesByConvoyIdQuery =
            "SELECT id_carriage, model, model_type, year_produced, capacity, id_convoy FROM carriage WHERE id_convoy = ?";
    /**
     * SQL query to select all convoy ids.
     */
    private static final String selectAllConvoyIdsQuery =
            "SELECT id_convoy FROM convoy";
    /**
     * SQL query to delete a convoy by id.
     */
    private static final String deleteConvoyQuery =
            "DELETE FROM convoy WHERE id_convoy = ?";
    /**
     * SQL query to update the convoy assignment for a carriage.
     */
    private static final String updateCarriageConvoyQuery =
            "UPDATE carriage SET id_convoy = ? WHERE id_carriage = ?";
    /**
     * SQL query to remove a carriage from a convoy.
     */
    private static final String removeCarriageFromConvoyQuery =
            "UPDATE carriage SET id_convoy = NULL WHERE id_carriage = ?";
    /**
     * SQL query to insert a new convoy and return its id.
     */
    private static final String insertConvoyQuery =
            "INSERT INTO convoy DEFAULT VALUES RETURNING id_convoy";

    private static final String convoyForNewRunQuery = """
            SELECT
              c.id_convoy,
              cp.id_station,
              cp.status,
              ca.id_carriage,
              ca.model,
              ca.model_type,
              ca.year_produced,
              ca.capacity
            FROM convoy_pool cp
            JOIN convoy c ON c.id_convoy = cp.id_convoy
            LEFT JOIN carriage ca ON ca.id_convoy = c.id_convoy
            WHERE cp.id_station = ?
              AND cp.status IN ('DEPOT', 'WAITING')
              AND c.id_convoy NOT IN (
                  SELECT r.id_convoy
                  FROM run r
                  WHERE r.time_departure <= ?::timestamp
                    AND r.time_arrival >= ?::timestamp
                    AND r.id_line = ?
              )
              AND c.id_convoy NOT IN (
                  SELECT ca2.id_convoy
                  FROM carriage ca2
                  JOIN carriage_depot cd ON ca2.id_carriage = cd.id_carriage
                  WHERE cd.status_of_carriage = 'MAINTENANCE'
                    AND cd.time_exited IS NULL
              )
            ORDER BY c.id_convoy, ca.id_carriage;""";

    @Override
    public Convoy selectConvoy(int id) throws SQLException {
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement convoyStmt = conn.prepareStatement(selectConvoyQuery);
                java.sql.PreparedStatement carriageStmt = conn.prepareStatement(selectCarriagesByConvoyIdQuery)
        ) {
            mapper.ConvoyMapper.setConvoyId(convoyStmt, id);
            try (java.sql.ResultSet convoyRs = convoyStmt.executeQuery()) {
                if (!convoyRs.next()) return null;
                mapper.ConvoyMapper.setConvoyId(carriageStmt, id);
                List<Carriage> carriages = mapper.ConvoyMapper.toCarriageList(carriageStmt.executeQuery());
                return mapper.ConvoyMapper.toDomain(convoyRs, carriages);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding convoy by id: " + id, e);
        }
    }

    @Override
    public List<Convoy> selectAllConvoys() throws SQLException {
        List<Convoy> convoys = new java.util.ArrayList<>();
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement convoyIdsStmt = conn.prepareStatement(selectAllConvoyIdsQuery);
                java.sql.ResultSet convoyIdsRs = convoyIdsStmt.executeQuery()
        ) {
            while (convoyIdsRs.next()) {
                int convoyId = mapper.ConvoyMapper.getConvoyId(convoyIdsRs);
                convoys.add(selectConvoy(convoyId));
            }
        } catch (SQLException e) {
            throw new SQLException("Error selecting all convoys", e);
        }
        return convoys;
    }

    @Override
    public boolean removeConvoy(int id) throws SQLException {
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement updateCarriageStmt = conn.prepareStatement("UPDATE carriage SET id_convoy = NULL WHERE id_convoy = ?");
                java.sql.PreparedStatement deleteConvoyStmt = conn.prepareStatement(deleteConvoyQuery)
        ) {
            mapper.ConvoyMapper.setConvoyId(updateCarriageStmt, id);
            updateCarriageStmt.executeUpdate();
            mapper.ConvoyMapper.setConvoyId(deleteConvoyStmt, id);
            int affectedRows = deleteConvoyStmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing convoy with id: " + id, e);
        }
    }

    @Override
    public boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException {
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(removeCarriageFromConvoyQuery)
        ) {
            mapper.ConvoyMapper.setCarriageId(pstmt, carriage);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing carriage " + carriage.getId() + " from convoy " + convoyId, e);
        }
    }

    @Override
    public Convoy createConvoy(List<Carriage> carriages) throws SQLException {
        int generatedId;
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement insertConvoyStmt = conn.prepareStatement(insertConvoyQuery)
        ) {
            try (java.sql.ResultSet rs = insertConvoyStmt.executeQuery()) {
                if (rs.next()) {
                    generatedId = mapper.ConvoyMapper.getConvoyId(rs);
                } else {
                    throw new SQLException("Failed to retrieve generated convoy id");
                }
            }
            if (carriages != null && !carriages.isEmpty()) {
                try (java.sql.PreparedStatement updateCarriageStmt = conn.prepareStatement(updateCarriageConvoyQuery)) {
                    for (Carriage carriage : carriages) {
                        mapper.ConvoyMapper.setConvoyAndCarriageId(updateCarriageStmt, generatedId, carriage);
                        updateCarriageStmt.addBatch();
                    }
                    int[] results = updateCarriageStmt.executeBatch();
                    for (int res : results) {
                        if (res == 0) {
                            throw new SQLException("Failed to update carriage with id_convoy: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating new convoy and updating carriages", e);
        }
        return domain.Convoy.of(generatedId, carriages);
    }

    @Override
    public businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw selectConvoyDetailsById(int id) throws SQLException {
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "SELECT ca.id_carriage, ca.model, ca.model_type, ca.year_produced, ca.capacity, " +
                        "l.id_line, l.name AS line_name, s.id_station, s.location AS station_name, ls.station_order, " +
                        "r.time_departure, r.time_arrival, stf.name AS staff_name, stf.surname AS staff_surname, stf.id_staff, " +
                        "r.id_first_station, r.id_last_station, s2.location AS departure_station, s3.location AS arrival_station " +
                        "FROM convoy c " +
                        "LEFT JOIN carriage ca ON ca.id_convoy = c.id_convoy " +
                        "LEFT JOIN run r ON r.id_convoy = c.id_convoy " +
                        "LEFT JOIN staff stf ON stf.id_staff = r.id_staff " +
                        "LEFT JOIN line l ON l.id_line = r.id_line " +
                        "LEFT JOIN line_station ls ON ls.id_line = l.id_line " +
                        "LEFT JOIN station s ON s.id_station = ls.id_station " +
                        "LEFT JOIN station s2 ON s2.id_station = r.id_first_station " +
                        "LEFT JOIN station s3 ON s3.id_station = r.id_last_station " +
                        "WHERE c.id_convoy = ? " +
                        "ORDER BY ls.station_order"
                )
        ) {
            mapper.ConvoyMapper.setConvoyId(stmt, id);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                return mapper.ConvoyMapper.toConvoyDetailsRaw(rs, id);
            }
        }
    }

    @Override
    public List<ConvoyDao.ConvoyAssignedRow> selectAssignedConvoysRowsByStaff(int staffId) throws SQLException {
        List<ConvoyDao.ConvoyAssignedRow> result = new ArrayList<>();
        String sql = "SELECT r.id_convoy, r.id_line, r.id_staff, r.id_first_station, s1.location AS departure_station, r.time_departure, s2.location AS arrival_station, r.time_arrival " +
                "FROM run r " +
                "JOIN station s1 ON r.id_first_station = s1.id_station " +
                "JOIN station s2 ON r.id_last_station = s2.id_station " +
                "WHERE r.id_staff = ?";
        try (
                java.sql.Connection conn = PostgresConnection.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            mapper.ConvoyMapper.setStaffId(stmt, staffId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.ConvoyMapper.toConvoyAssignedRow(rs));
                }
            }
        }
        return result;
    }

    @Override
    public List<Convoy> getConvoysForNewRun(int idStation, String timeDeparture, LocalDate dateDeparture, int idLine) throws SQLException {
        List<Convoy> convoys = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(convoyForNewRunQuery)) {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(dateDeparture, java.time.LocalTime.parse(timeDeparture));
            java.sql.Timestamp departureTimestamp = java.sql.Timestamp.valueOf(dateTime);
            mapper.ConvoyMapper.setConvoysForNewRunParams(stmt, idStation, departureTimestamp, idLine);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                convoys = mapper.ConvoyMapper.toConvoyListForNewRun(rs);
            }
        }
        return convoys;
    }

    public void addCarriagesToConvoy(int id, List<Carriage> carriages) throws SQLException {
        if (carriages == null || carriages.isEmpty()) {
            throw new IllegalArgumentException("Carriages list must not be null or empty.");
        }
        StringBuilder sql = new StringBuilder("UPDATE carriage SET id_convoy = ? WHERE id_carriage IN (");
        for (int i = 0; i < carriages.size(); i++) {
            sql.append("?");
            if (i < carriages.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        try (Connection conn = PostgresConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            mapper.ConvoyMapper.setAddCarriagesToConvoyParams(pstmt, id, carriages);
            pstmt.executeUpdate();
        }
    }
}
