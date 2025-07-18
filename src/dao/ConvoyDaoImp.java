package dao;

import domain.Carriage;
import domain.Convoy;

import java.sql.SQLException;
import java.util.List;

class ConvoyDaoImp implements ConvoyDao {
    private static final String selectConvoyQuery =
            "SELECT id_convoy, id_carriage FROM convoy WHERE id_convoy = ?";
    private static final String selectConvoySubQuery=
            "SELECT id_carriage, model, model_type, year_produced, capacity FROM carriage WHERE id_carriage IN " +
                    "(SELECT id_carriage FROM convoy WHERE id_convoy = ?)";
    private static final String selectAllConvoyIdsQuery =
            "SELECT DISTINCT id_convoy FROM convoy";
    private static final String deleteConvoyQuery =
            "DELETE FROM convoy WHERE id_convoy = ?";
    private static final String addCarriageToConvoyQuery =
            "INSERT INTO convoy (id_convoy, id_carriage) VALUES (?, ?)";
    private static final String removeCarriageFromConvoyQuery =
            "DELETE FROM convoy WHERE id_convoy = ? AND id_carriage = ?";
    private static final String findConvoyIdByCarriageIdQuery =
            "SELECT id_convoy FROM convoy WHERE id_carriage = ?";
    private static final String insertConvoyQuery =
            "INSERT INTO convoy DEFAULT VALUES RETURNING id_convoy";
    private static final String insertConvoyCarriageQuery =
            "INSERT INTO convoy (id_convoy, id_carriage) VALUES (?, ?)";

    @Override
    public Convoy selectConvoy(int id) throws SQLException {
        List<Carriage> carriages = new java.util.ArrayList<>();
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement convoyStmt = conn.prepareStatement(selectConvoyQuery);
            java.sql.PreparedStatement carriageStmt = conn.prepareStatement(selectConvoySubQuery)
        ) {
            convoyStmt.setInt(1, id);
            try (java.sql.ResultSet convoyRs = convoyStmt.executeQuery()) {
                if (!convoyRs.next()) return null;
            }
            carriageStmt.setInt(1, id);
            try (java.sql.ResultSet rs = carriageStmt.executeQuery()) {
                while (rs.next()) {
                    carriages.add(domain.Carriage.of(
                        rs.getInt("id_carriage"),
                        rs.getString("model"),
                        rs.getString("model_type"),
                        rs.getInt("year_produced"),
                        rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding convoy by id: " + id, e);
        }
        return domain.Convoy.of(id, carriages);
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
            java.sql.PreparedStatement pstmt = conn.prepareStatement(deleteConvoyQuery)
        ) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing convoy with id: " + id, e);
        }
    }

    @Override
    public boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException {
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(addCarriageToConvoyQuery)
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
            pstmt.setInt(1, convoyId);
            pstmt.setInt(2, carriage.getId());
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
        int generatedId = -1;
        try (
            java.sql.Connection conn = PostgresConnection.getConnection();
            java.sql.PreparedStatement insertConvoyStmt = conn.prepareStatement(insertConvoyQuery);
        ) {
            try (java.sql.ResultSet rs = insertConvoyStmt.executeQuery()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated convoy id");
                }
            }
            if (carriages != null && !carriages.isEmpty()) {
                try (java.sql.PreparedStatement insertCarriageStmt = conn.prepareStatement(insertConvoyCarriageQuery)) {
                    for (Carriage carriage : carriages) {
                        insertCarriageStmt.setInt(1, generatedId);
                        insertCarriageStmt.setInt(2, carriage.getId());
                        insertCarriageStmt.addBatch();
                    }
                    insertCarriageStmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating new convoy", e);
        }
        return domain.Convoy.of(generatedId, carriages);
    }
}
