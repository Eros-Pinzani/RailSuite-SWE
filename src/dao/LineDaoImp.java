package dao;

import domain.Line;
import domain.LineStation;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of the LineDao interface.
 * Contains SQL queries and logic for accessing line data.
 */
class LineDaoImp implements LineDao {
    LineDaoImp(){}

    @Override
    public Line findById(int idLine) throws SQLException {
        // SQL query to select a line by id.
        String sqlLine = "SELECT * FROM line WHERE id_line = ?";
        // SQL query to select stations for a line.
        String sqlStations = "SELECT * FROM line_station WHERE id_line = ? ORDER BY station_order";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmtLine = conn.prepareStatement(sqlLine);
             PreparedStatement stmtStations = conn.prepareStatement(sqlStations)) {
            stmtLine.setInt(1, idLine);
            ResultSet rsLine = stmtLine.executeQuery();
            if (!rsLine.next()) return null;
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
                    if (intervalStr != null) {
                        if (intervalStr.startsWith("P")) {
                            duration = Duration.parse(intervalStr);
                        } else {
                            // Gestione formato hh:mm:ss
                            String[] parts = intervalStr.split(":");
                            if (parts.length == 3) {
                                int hours = Integer.parseInt(parts[0]);
                                int minutes = Integer.parseInt(parts[1]);
                                int seconds = (int) Double.parseDouble(parts[2]);
                                duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
                            }
                        }
                    }
                }
                stations.add(LineStation.of(stationId, order, duration));
            }
            stations.sort(Comparator.comparingInt(LineStation::getOrder));
            return mapper.LineMapper.toDomain(rsLine, stations);
        }
    }

    @Override
    public List<Line> findAll() throws SQLException {
        List<Line> lines = new ArrayList<>();
        // SQL query to select all lines.
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
}
