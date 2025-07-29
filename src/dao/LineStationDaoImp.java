package dao;

import domain.LineStation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the LineStationDao interface.
 * Contains SQL queries and logic for accessing line-station relationship data.
 */
class LineStationDaoImp implements LineStationDao {
    LineStationDaoImp(){}

    @Override
    public LineStation findById(int idLine, int idStation) throws SQLException {
        // SQL query to get a specific LineStation relation by idLine and idStation
        String sql = "SELECT * FROM line_station WHERE id_line = ? AND id_station = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLine);
            stmt.setInt(2, idStation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapper.LineStationMapper.toDomain(rs);
            }
            return null;
        }
    }

    @Override
    public List<LineStation> findByLine(int idLine) throws SQLException {
        // SQL query to get all LineStation relations for a line, ordered by station_order
        String sql = "SELECT * FROM line_station WHERE id_line = ? ORDER BY station_order";
        List<LineStation> stations = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLine);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stations.add(mapper.LineStationMapper.toDomain(rs));
            }
        }
        return stations;
    }
}
