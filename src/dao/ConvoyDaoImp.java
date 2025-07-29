package dao;

import domain.Carriage;
import domain.Convoy;
import java.sql.SQLException;
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
     * SQL query to find convoy id by carriage id.
     */
    private static final String findConvoyIdByCarriageIdQuery =
            "SELECT id_convoy FROM carriage WHERE id_carriage = ?";
    /**
     * SQL query to insert a new convoy and return its id.
     */
    private static final String insertConvoyQuery =
            "INSERT INTO convoy DEFAULT VALUES RETURNING id_convoy";

    @Override
    public Convoy selectConvoy(int id) throws SQLException {
        List<Carriage> carriages = new java.util.ArrayList<>();
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement convoyStmt = conn.prepareStatement(selectConvoyQuery);
            java.sql.PreparedStatement carriageStmt = conn.prepareStatement(selectCarriagesByConvoyIdQuery)
        ) {
            convoyStmt.setInt(1, id);
            try (java.sql.ResultSet convoyRs = convoyStmt.executeQuery()) {
                if (!convoyRs.next()) return null;
                carriageStmt.setInt(1, id);
                try (java.sql.ResultSet rs = carriageStmt.executeQuery()) {
                    while (rs.next()) {
                        carriages.add(mapper.CarriageMapper.toDomain(rs));
                    }
                }
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
                int convoyId = convoyIdsRs.getInt("id_convoy");
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
            updateCarriageStmt.setInt(1, id);
            updateCarriageStmt.executeUpdate();
            deleteConvoyStmt.setInt(1, id);
            int affectedRows = deleteConvoyStmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing convoy with id: " + id, e);
        }
    }

    @Override
    public boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException {
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(updateCarriageConvoyQuery)
        ) {
            pstmt.setInt(1, convoyId);
            pstmt.setInt(2, carriage.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error adding carriage " + carriage.getId() + " to convoy " + convoyId, e);
        }
    }

    @Override
    public boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException {
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(removeCarriageFromConvoyQuery)
        ) {
            pstmt.setInt(1, carriage.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing carriage " + carriage.getId() + " from convoy " + convoyId, e);
        }
    }

    @Override
    public Integer findConvoyIdByCarriageId(int carriageId) throws SQLException {
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(findConvoyIdByCarriageIdQuery)
        ) {
            pstmt.setInt(1, carriageId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_convoy");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding convoyId for carriageId: " + carriageId, e);
        }
        return null;
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
                    generatedId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated convoy id");
                }
            }
            if (carriages != null && !carriages.isEmpty()) {
                try (java.sql.PreparedStatement updateCarriageStmt = conn.prepareStatement(updateCarriageConvoyQuery)) {
                    for (Carriage carriage : carriages) {
                        updateCarriageStmt.setInt(1, generatedId);
                        updateCarriageStmt.setInt(2, carriage.getId());
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
        businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw raw = new businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw();
        raw.convoyId = id;
        raw.lineName = "";
        raw.staffName = "";
        raw.departureStation = "";
        raw.departureTime = "";
        raw.arrivalStation = "";
        raw.arrivalTime = "";
        java.util.List<domain.Carriage> carriages = new java.util.ArrayList<>();
        java.util.Set<Integer> carriageIds = new java.util.HashSet<>();
        java.util.List<businessLogic.service.ConvoyDetailsService.StationRow> stationRows = new java.util.ArrayList<>();
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT ca.id_carriage, ca.model, ca.model_type, ca.year_produced, ca.capacity, " +
                "l.id_line, l.name AS line_name, s.id_station, s.location AS station_name, ls.station_order, " +
                "r.time_departure, r.time_arrival, stf.name AS staff_name, stf.surname AS staff_surname, " +
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
            stmt.setInt(1, id);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int carriageId = rs.getInt("id_carriage");
                    if (carriageId > 0 && !carriageIds.contains(carriageId)) {
                        carriages.add(Carriage.of(
                            carriageId,
                            rs.getString("model"),
                            rs.getString("model_type"),
                            rs.getInt("year_produced"),
                            rs.getInt("capacity"),
                            id
                        ));
                        carriageIds.add(carriageId);
                    }
                    if (raw.lineName.isEmpty() && rs.getString("line_name") != null) {
                        raw.lineName = rs.getString("line_name");
                    }
                    if (raw.staffName.isEmpty() && rs.getString("staff_name") != null) {
                        raw.staffName = rs.getString("staff_name") + " " + rs.getString("staff_surname");
                    }
                    if (raw.departureStation.isEmpty() && rs.getString("departure_station") != null) {
                        raw.departureStation = rs.getString("departure_station");
                    }
                    if (raw.arrivalStation.isEmpty() && rs.getString("arrival_station") != null) {
                        raw.arrivalStation = rs.getString("arrival_station");
                    }
                    if (raw.departureTime.isEmpty() && rs.getString("time_departure") != null) {
                        raw.departureTime = rs.getString("time_departure");
                    }
                    if (raw.arrivalTime.isEmpty() && rs.getString("time_arrival") != null) {
                        raw.arrivalTime = rs.getString("time_arrival");
                    }
                    int stationId = rs.getInt("id_station");
                    String stationName = rs.getString("station_name");
                    if (stationId > 0 && stationName != null && stationRows.stream().noneMatch(s -> s.stationName.equals(stationName))) {
                        stationRows.add(new businessLogic.service.ConvoyDetailsService.StationRow(
                            stationName, "", ""
                        ));
                    }
                }
            }
        }
        raw.carriages = carriages;
        raw.stationRows = stationRows;
        return raw;
    }

    @Override
    public List<ConvoyDao.ConvoyAssignedRow> selectAssignedConvoysRowsByStaff(int staffId) throws SQLException {
        List<ConvoyDao.ConvoyAssignedRow> result = new ArrayList<>();
        String sql = "SELECT r.id_convoy, s1.location AS departure_station, r.time_departure, s2.location AS arrival_station, r.time_arrival " +
                "FROM run r " +
                "JOIN station s1 ON r.id_first_station = s1.id_station " +
                "JOIN station s2 ON r.id_last_station = s2.id_station " +
                "WHERE r.id_staff = ?";
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, staffId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int convoyId = rs.getInt("id_convoy");
                    String departureStation = rs.getString("departure_station");
                    String departureTime = rs.getString("time_departure");
                    String arrivalStation = rs.getString("arrival_station");
                    String arrivalTime = rs.getString("time_arrival");
                    result.add(new ConvoyDao.ConvoyAssignedRow(
                        convoyId,
                        departureStation != null ? departureStation : "",
                        departureTime != null ? departureTime : "",
                        arrivalStation != null ? arrivalStation : "",
                        arrivalTime != null ? arrivalTime : ""
                    ));
                }
            }
        }
        return result;
    }
}
