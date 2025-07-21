package dao;

import domain.Line;
import domain.LineStation;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class LineDaoImp implements LineDao {
    @Override
    public Line findById(int idLine) throws SQLException {
        String sqlLine = "SELECT * FROM line WHERE id_line = ?";
        String sqlStations = "SELECT * FROM line_station WHERE id_line = ? ORDER BY station_order";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmtLine = conn.prepareStatement(sqlLine);
             PreparedStatement stmtStations = conn.prepareStatement(sqlStations)) {
            stmtLine.setInt(1, idLine);
            ResultSet rsLine = stmtLine.executeQuery();
            if (!rsLine.next()) return null;
            String name = rsLine.getString("name");
            stmtStations.setInt(1, idLine);
            ResultSet rsStations = stmtStations.executeQuery();
            List<LineStation> stations = new ArrayList<>();
            while (rsStations.next()) {
                int stationId = rsStations.getInt("id_station");
                int order = rsStations.getInt("station_order");
                Duration duration = null;
                Object intervalObj = rsStations.getObject("time_to_next_station");
                if (intervalObj instanceof Duration) {
                    duration = (Duration) intervalObj;
                } else if (intervalObj != null) {
                    String intervalStr = rsStations.getString("time_to_next_station");
                    if (intervalStr != null) duration = Duration.parse(intervalStr);
                }
                stations.add(LineStation.of(stationId, order, duration));
            }
            stations.sort(Comparator.comparingInt(LineStation::getOrder));
            return Line.of(idLine, name, stations);
        }
    }

    @Override
    public List<Line> findAll() throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT id_line FROM line";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lines.add(findById(rs.getInt("id_line")));
            }
        }
        return lines;
    }

    @Override
    public List<Line> findByStation(int idStation) throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT DISTINCT id_line FROM line_station WHERE id_station = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStation);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lines.add(findById(rs.getInt("id_line")));
            }
        }
        return lines;
    }
}
