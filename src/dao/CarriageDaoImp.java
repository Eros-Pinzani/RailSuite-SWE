package dao;

import domain.Carriage;
import mapper.CarriageMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class CarriageDaoImp implements CarriageDao {
    private static final String selectCarriageQuery =
            "SELECT id_carriage, model, model_type, year_produced, capacity FROM carriage WHERE id_carriage = ?";
    private static final String selectAllCarriageQuery =
            "SELECT id_carriage, model, model_type, year_produced, capacity FROM carriage";
    private static final String selectCarriagesByConvoyIdQuery =
            "SELECT id_carriage, model, model_type, year_produced, capacity, id_convoy FROM carriage WHERE id_convoy = ?";
    private static final String updateCarriageConvoyQuery =
            "UPDATE carriage SET id_convoy = ? WHERE id_carriage = ?";

    public CarriageDaoImp() {
    }

    @Override
    public Carriage selectCarriage(int id) throws SQLException {
        try (
                Connection conn = PostgresConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectCarriageQuery)
        ) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return CarriageMapper.toDomain(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding carriage by id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Carriage> selectAllCarriages() throws SQLException {
        List<Carriage> results = new ArrayList<>();
        try (
                Connection conn = PostgresConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectAllCarriageQuery);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                results.add(CarriageMapper.toDomain(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding all carriages:", e);
        }
        return results;
    }

    @Override
    public List<Carriage> selectCarriagesByConvoyId(int convoyId) throws SQLException {
        List<Carriage> results = new ArrayList<>();
        try (
                Connection conn = PostgresConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectCarriagesByConvoyIdQuery)
        ) {
            pstmt.setInt(1, convoyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(CarriageMapper.toDomain(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding carriages by convoy id: " + convoyId, e);
        }
        return results;
    }

    @Override
    public boolean updateCarriageConvoy(int carriageId, Integer idConvoy) throws SQLException {
        try (
                Connection conn = PostgresConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updateCarriageConvoyQuery)
        ) {
            if (idConvoy == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, idConvoy);
            }
            pstmt.setInt(2, carriageId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error updating convoy for carriage: " + carriageId, e);
        }
    }
}
