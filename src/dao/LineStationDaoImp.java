package dao;

import domain.LineStation;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class LineStationDaoImp implements LineStationDao {
    @Override
    public LineStation findById(int idLine, int idStation) throws SQLException {
        String sql = "SELECT * FROM line_station WHERE id_line = ? AND id_station = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLine);
            stmt.setInt(2, idStation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int order = rs.getInt("station_order");
                Duration duration = null;
                String intervalStr = rs.getString("time_to_next_station");
                if (intervalStr != null) duration = Duration.parse(intervalStr);
                return LineStation.of(idStation, order, duration);
            }
            return null;
        }
    }

    @Override
    public List<LineStation> findByLine(int idLine) throws SQLException {
        String sql = "SELECT * FROM line_station WHERE id_line = ? ORDER BY station_order";
        List<LineStation> stations = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLine);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idStation = rs.getInt("id_station");
                int order = rs.getInt("station_order");
                Duration duration = null;
                String intervalStr = rs.getString("time_to_next_station");
                if (intervalStr != null) duration = Duration.parse(intervalStr);
                stations.add(LineStation.of(idStation, order, duration));
            }
        }
        return stations;
    }
}
