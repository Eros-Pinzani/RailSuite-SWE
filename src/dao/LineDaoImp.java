package dao;

import domain.Line;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class LineDaoImp implements LineDao {

    private final static String getLineRawQuery = """
            SELECT
                l.id_line, l.name,
                s1.id_station AS id_first_station, s1.location AS first_station_location,
                s2.id_station AS id_last_station, s2.location AS last_station_location
            FROM line l
                JOIN line_station ls1 ON l.id_line = ls1.id_line
                JOIN station s1 ON ls1.id_station = s1.id_station AND s1.is_head = true
                JOIN line_station ls2 ON l.id_line = ls2.id_line
                JOIN station s2 ON ls2.id_station = s2.id_station AND s2.is_head = true AND s1.id_station <> s2.id_station""";

    LineDaoImp() {}

    @Override
    public List<Line> getAllLines() throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getLineRawQuery);
             ResultSet rs = stmt.executeQuery()) {
            List<Line> lines = new ArrayList<>();
            while (rs.next()) {
                lines.add(mapper.LineMapper.toDomain(rs));
            }
            return lines;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all lines", e);
        }
    }
}
