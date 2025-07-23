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
}
