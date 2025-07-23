package dao;

import domain.CarriageDepot;
import domain.Depot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mapper.DepotMapper;

class DepotDaoImp implements DepotDao {
    DepotDaoImp() {}

    @Override
    public Depot getDepot(int idDepot) throws SQLException {
        String sql = "SELECT * FROM depot WHERE id_depot = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepot);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Depot depot = DepotMapper.toDomain(rs);
                CarriageDepotDao carriageDepotDao = new CarriageDepotDaoImp();
                List<CarriageDepot> carriages = carriageDepotDao.getCarriagesByDepot(idDepot);
                for (CarriageDepot cd : carriages) {
                    depot.addCarriage(cd);
                }
                return depot;
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
                Depot depot = DepotMapper.toDomain(rs);
                CarriageDepotDao carriageDepotDao = new CarriageDepotDaoImp();
                List<CarriageDepot> carriages = carriageDepotDao.getCarriagesByDepot(depot.getIdDepot());
                for (CarriageDepot cd : carriages) {
                    depot.addCarriage(cd);
                }
                depots.add(depot);
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
