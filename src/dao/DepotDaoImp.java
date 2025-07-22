package dao;

import domain.Depot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DepotDaoImp implements DepotDao {
    DepotDaoImp() {}

    private static Depot mapResultSetToDepot(ResultSet rs) throws SQLException {
        return Depot.of(rs.getInt("id_depot"));
    }

    @Override
    public Depot getDepot(int idDepot) throws SQLException {
        String sql = "SELECT * FROM depot WHERE id_depot = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepot(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding depot by ID: " + idDepot, e);
        }
        return null;
    }

    @Override
    public List<Depot> getAllDepots() throws SQLException {
        String sql = "SELECT * FROM depot";
        try {
            return getDepots(sql);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all depots", e);
        }
    }

    private List<Depot> getDepots(String sql) throws SQLException {
        List<Depot> depots = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                depots.add(mapResultSetToDepot(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving depots", e);
        }
        return depots;
    }

    @Override
    public void insertDepot(int idDepot) throws SQLException {
        String sql = "INSERT INTO depot (id_depot) VALUES (?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error inserting depot: " + idDepot, e);
        }
    }

    @Override
    public void deleteDepot(int idDepot) throws SQLException {
        String sql = "DELETE FROM depot WHERE id_depot = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting depot: " + idDepot, e);
        }
    }
}
