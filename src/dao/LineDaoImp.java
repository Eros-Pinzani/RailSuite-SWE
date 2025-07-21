package dao;

import domain.Line;
import domain.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.postgresql.util.PGInterval;

class LineDaoImp implements LineDao {
    private final StationDao stationDao = new StationDaoImp();

    private Line mapResultSetToLine(ResultSet rs) throws SQLException {
        // Conversione da SQL Interval a java.time.Duration
        Object intervalObj = rs.getObject("time_to_next_station");
        java.time.Duration duration = null;
        if (intervalObj instanceof java.time.Duration) {
            duration = (java.time.Duration) intervalObj;
        } else if (intervalObj instanceof PGInterval) {
            PGInterval pgInterval = (PGInterval) intervalObj;
            duration = java.time.Duration.ofSeconds((long) pgInterval.getSeconds() + pgInterval.getMinutes() * 60 + pgInterval.getHours() * 3600 + pgInterval.getDays() * 86400);
        } else {
            // fallback: tentativo di conversione da stringa ISO-8601
            String intervalStr = rs.getString("time_to_next_station");
            duration = java.time.Duration.parse(intervalStr);
        }
        Station station = stationDao.findById(rs.getInt("id_station"));
        Station nextStation = stationDao.findById(rs.getInt("id_next_station"));
        return Line.of(
            rs.getInt("id_line"),
            station,
            nextStation,
            duration
        );
    }

    @Override
    public Line findById(int idLine) throws SQLException {
        String sql = "SELECT * FROM line WHERE id_line = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLine);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLine(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding line by id: " + idLine, e);
        }
        return null;
    }

    @Override
    public List<Line> findAll() throws SQLException {
        String sql = "SELECT * FROM line";
        return getLines(sql);
    }

    @Override
    public List<Line> findByStation(int idStation) throws SQLException {
        String sql = "SELECT * FROM line WHERE id_station = ?";
        return getLinesWithParam(sql, idStation);
    }

    @Override
    public List<Line> findByNextStation(int idNextStation) throws SQLException {
        String sql = "SELECT * FROM line WHERE id_next_station = ?";
        return getLinesWithParam(sql, idNextStation);
    }

    private List<Line> getLines(String sql) throws SQLException {
        List<Line> lines = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lines.add(mapResultSetToLine(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving lines", e);
        }
        return lines;
    }

    private List<Line> getLinesWithParam(String sql, int param) throws SQLException {
        List<Line> lines = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lines.add(mapResultSetToLine(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving lines with param", e);
        }
        return lines;
    }
}
