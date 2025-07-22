package dao;

import domain.CarriageDepot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class CarriageDepotDaoImp implements CarriageDepotDao {
    private static CarriageDepot mapResultSetToCarriageDepot(ResultSet rs) throws SQLException {
        return CarriageDepot.of(
            rs.getInt("id_depot"),
            rs.getInt("id_carriage"),
            rs.getTimestamp("time_entered"),
            rs.getTimestamp("time_exited"),
            CarriageDepot.StatusOfCarriage.valueOf(rs.getString("status_of_carriage"))
        );
    }

    @Override
    public CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException {
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
        String sql = "SELECT * FROM carriage_depot WHERE id_depot = ?";
        try {
            return getCarriageDepots(sql, idDepot);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving carriages for depot: " + idDepot, e);
        }
    }

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
}
