package dao;

import domain.CarriageDepot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the CarriageDepotDao interface.
 * Contains SQL queries and logic for accessing carriage depot data.
 */
class CarriageDepotDaoImp implements CarriageDepotDao {
    /**
     * Maps a ResultSet row to a CarriageDepot domain object using the mapper.
     */
    private static CarriageDepot mapResultSetToCarriageDepot(ResultSet rs) throws SQLException {
        return mapper.CarriageDepotMapper.toDomain(rs);
    }

    @Override
    public CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException {
        // SQL query to select a carriage_depot by depot and carriage id.
        String sql = "SELECT * FROM carriage_depot WHERE id_depot = ? AND id_carriage = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            stmt.setInt(2, idCarriage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCarriageDepot(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding carriage_depot by depot and carriage: " + idDepot + ", " + idCarriage, e);
        }
        return null;
    }

    @Override
    public List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException {
        // SQL query to select all carriage_depot by depot id.
        String sql = "SELECT * FROM carriage_depot WHERE id_depot = ?";
        try {
            return getCarriageDepots(sql, idDepot);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving carriages for depot: " + idDepot, e);
        }
    }

    /**
     * Helper method to execute a query and return a list of CarriageDepot objects.
     */
    private List<CarriageDepot> getCarriageDepots(String sql, int idDepot) throws SQLException {
        List<CarriageDepot> carriages = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                carriages.add(mapResultSetToCarriageDepot(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving carriage_depot list for depot: " + idDepot, e);
        }
        return carriages;
    }

    @Override
    public void insertCarriageDepot(CarriageDepot carriageDepot) throws SQLException {
        // SQL query to insert a new carriage_depot record.
        String sql = "INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, time_exited, status_of_carriage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carriageDepot.getIdDepot());
            stmt.setInt(2, carriageDepot.getIdCarriage());
            stmt.setTimestamp(3, carriageDepot.getTimeEntered());
            stmt.setTimestamp(4, carriageDepot.getTimeExited());
            stmt.setString(5, carriageDepot.getStatusOfCarriage().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error inserting carriage_depot: " + carriageDepot.getIdDepot() + ", " + carriageDepot.getIdCarriage(), e);
        }
    }

    @Override
    public void updateCarriageDepot(CarriageDepot carriageDepot) throws SQLException {
        // SQL query to update an existing carriage_depot record.
        String sql = "UPDATE carriage_depot SET time_entered = ?, time_exited = ?, status_of_carriage = ? WHERE id_depot = ? AND id_carriage = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, carriageDepot.getTimeEntered());
            stmt.setTimestamp(2, carriageDepot.getTimeExited());
            stmt.setString(3, carriageDepot.getStatusOfCarriage().name());
            stmt.setInt(4, carriageDepot.getIdDepot());
            stmt.setInt(5, carriageDepot.getIdCarriage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating carriage_depot: " + carriageDepot.getIdDepot() + ", " + carriageDepot.getIdCarriage(), e);
        }
    }

    @Override
    public void deleteCarriageDepot(CarriageDepot carriageDepot) throws SQLException {
        // SQL query to delete a carriage_depot record.
        String sql = "DELETE FROM carriage_depot WHERE id_depot = ? AND id_carriage = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carriageDepot.getIdDepot());
            stmt.setInt(2, carriageDepot.getIdCarriage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting carriage_depot: " + carriageDepot.getIdDepot() + ", " + carriageDepot.getIdCarriage(), e);
        }
    }

    @Override
    public void deleteCarriageDepotByCarriage(int idCarriage) throws SQLException {
        // SQL query to delete carriage_depot records by carriage id.
        String sql = "DELETE FROM carriage_depot WHERE id_carriage = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCarriage);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteCarriageDepotByCarriageIfAvailable(int idCarriage) throws SQLException {
        // SQL query to delete available carriage_depot records by carriage id.
        String sql = "DELETE FROM carriage_depot WHERE id_carriage = ? AND status_of_carriage = 'AVAILABLE'";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCarriage);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<domain.CarriageDepotDTO> findCarriagesWithDepotStatusByConvoy(int idConvoy) throws SQLException {
        List<domain.CarriageDepotDTO> result = new ArrayList<>();
        // SQL query to find carriages with their depot status by convoy id.
        String sql = "SELECT c.id_carriage, c.model, c.year_produced, c.capacity, cd.status_of_carriage, cd.time_exited " +
                "FROM carriage c " +
                "LEFT JOIN carriage_depot cd ON c.id_carriage = cd.id_carriage AND cd.time_exited IS NULL " +
                "WHERE c.id_convoy = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConvoy);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new domain.CarriageDepotDTO(
                    rs.getInt("id_carriage"),
                    rs.getString("model"),
                    rs.getInt("year_produced"),
                    rs.getInt("capacity"),
                    rs.getString("status_of_carriage"),
                    rs.getTimestamp("time_exited")
                ));
            }
        }
        return result;
    }

    @Override
    public List<domain.Carriage> findAvailableCarriagesForConvoyAdd(int idStation, String modelType) throws SQLException {
        List<domain.Carriage> result = new ArrayList<>();
        // SQL query to find available carriages for adding to a convoy.
        String sql = "SELECT c.* FROM carriage_depot cd " +
                "JOIN depot d ON cd.id_depot = d.id_depot " +
                "JOIN carriage c ON cd.id_carriage = c.id_carriage " +
                "WHERE d.id_depot = ? AND cd.status_of_carriage = 'AVAILABLE' AND c.model_type = ? AND c.id_convoy IS NULL AND cd.time_exited IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStation);
            stmt.setString(2, modelType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapper.CarriageMapper.toDomain(rs));
            }
        }
        return result;
    }

    @Override
    public List<domain.Carriage> findAvailableCarriagesForConvoy(int idStation, String modelType) throws SQLException {
        List<domain.Carriage> result = new ArrayList<>();
        // SQL query to find available carriages for a convoy, updating their status if needed.
        String sql =
                // Aggiorna lo stato delle vetture in CLEANING e MAINTENANCE se scaduto il tempo
                "WITH updated AS (\n" +
                "    UPDATE carriage_depot cd\n" +
                "    SET status_of_carriage = 'AVAILABLE'\n" +
                "    FROM depot d\n" +
                "    WHERE cd.id_depot = d.id_depot\n" +
                "      AND d.id_depot = ?\n" +
                "      AND ((cd.status_of_carriage = 'CLEANING' AND cd.time_entered <= NOW() - INTERVAL '3 hours')\n" +
                "           OR (cd.status_of_carriage = 'MAINTENANCE' AND cd.time_entered <= NOW() - INTERVAL '1 day'))\n" +
                "      AND cd.time_exited IS NULL\n" +
                "    RETURNING cd.id_carriage\n" +
                ")\n" +
                // Seleziona tutte le vetture disponibili
                "SELECT c.* FROM carriage_depot cd\n" +
                "JOIN depot d ON cd.id_depot = d.id_depot\n" +
                "JOIN carriage c ON cd.id_carriage = c.id_carriage\n" +
                "WHERE d.id_depot = ?\n" +
                "  AND cd.status_of_carriage = 'AVAILABLE'\n" +
                "  AND c.model_type = ?\n" +
                "  AND c.id_convoy IS NULL\n" +
                "  AND cd.time_exited IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStation); // per l'UPDATE
            stmt.setInt(2, idStation); // per la SELECT
            stmt.setString(3, modelType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapper.CarriageMapper.toDomain(rs));
            }
        }
        return result;
    }

    @Override
    public List<String> findAvailableCarriageTypesForConvoy(int idStation) throws SQLException {
        List<String> result = new ArrayList<>();
        // SQL query to find available carriage types for a convoy, updating their status if needed.
        String sql =
                // Aggiorna lo stato delle vetture in CLEANING e MAINTENANCE se scaduto il tempo
                "WITH updated AS (\n" +
                "    UPDATE carriage_depot cd\n" +
                "    SET status_of_carriage = 'AVAILABLE'\n" +
                "    FROM depot d\n" +
                "    WHERE cd.id_depot = d.id_depot\n" +
                "      AND d.id_depot = ?\n" +
                "      AND ((cd.status_of_carriage = 'CLEANING' AND cd.time_entered <= NOW() - INTERVAL '3 hours')\n" +
                "           OR (cd.status_of_carriage = 'MAINTENANCE' AND cd.time_entered <= NOW() - INTERVAL '1 day'))\n" +
                "      AND cd.time_exited IS NULL\n" +
                "    RETURNING cd.id_carriage\n" +
                ")\n" +
                // Seleziona tutti i model_type disponibili
                "SELECT DISTINCT c.model_type FROM carriage_depot cd\n" +
                "JOIN depot d ON cd.id_depot = d.id_depot\n" +
                "JOIN carriage c ON cd.id_carriage = c.id_carriage\n" +
                "WHERE d.id_depot = ?\n" +
                "  AND cd.status_of_carriage = 'AVAILABLE'\n" +
                "  AND c.id_convoy IS NULL\n" +
                "  AND cd.time_exited IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStation); // per l'UPDATE
            stmt.setInt(2, idStation); // per la SELECT
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("model_type"));
            }
        }
        return result;
    }

    @Override
    public CarriageDepot findActiveDepotByCarriage(int idCarriage) throws SQLException {
        // SQL query to find the active depot for a carriage.
        String sql = "SELECT * FROM carriage_depot WHERE id_carriage = ? AND time_exited IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCarriage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapper.CarriageDepotMapper.toDomain(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding active depot for carriage: " + idCarriage, e);
        }
        return null;
    }

    @Override
    public List<String> findAvailableCarriageModelsForConvoy(int idStation, String modelType) throws SQLException {
        List<String> models = new ArrayList<>();
        // SQL query to find available carriage models for a convoy.
        String sql = "SELECT DISTINCT c.model FROM carriage_depot cd " +
                "JOIN carriage c ON cd.id_carriage = c.id_carriage " +
                "JOIN depot d ON cd.id_depot = d.id_depot " +
                "WHERE d.id_depot = ? AND c.model_type = ? AND cd.status_of_carriage = 'AVAILABLE' AND c.id_convoy IS NULL AND cd.time_exited IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStation);
            stmt.setString(2, modelType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                models.add(rs.getString("model"));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving available carriage models for station: " + idStation + ", type: " + modelType, e);
        }
        return models;
    }
}
