package dao;

import domain.DTO.TimeTableDTO;
import domain.LineStation;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the LineStationDao interface.
 * Contains SQL queries and logic for accessing line-station relationship data.
 */
class LineStationDaoImp implements LineStationDao {
    LineStationDaoImp() {
    }

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

    private static Duration parsePgInterval(String intervalStr) {
        if (intervalStr == null) return null;
        // Gestisce solo formato HH:mm:ss
        String[] parts = intervalStr.split(":");
        int hours = 0, minutes = 0, seconds = 0;
        if (parts.length == 3) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
            seconds = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
        } else if (parts.length == 1) {
            hours = Integer.parseInt(parts[0]);
        }
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * Restituisce la tabella orari per una corsa: ogni stazione con orario di arrivo e partenza.
     *
     * @param idLine id della linea
     * @param idStartStation id della stazione di partenza
     * @param departureTime orario di partenza (formato HH:mm)
     * @return lista di StationArrAndDepDTO
     */
    private static final String findTimeTableForRunSQL = """
            SELECT ls.*, s.location
            FROM line_station ls
            JOIN station s ON ls.id_station = s.id_station
            WHERE ls.id_line = ?
            ORDER BY CASE
                WHEN ? = (SELECT id_station FROM line_station WHERE id_line = ?
                          ORDER BY station_order ASC LIMIT 1) THEN ls.station_order
                          ELSE -ls.station_order
            END""";

    @Override
    public List<TimeTableDTO.StationArrAndDepDTO> findTimeTableForRun(
            int idLine,
            int idStartStation,
            String departureTime) throws SQLException {
        List<TimeTableDTO.StationArrAndDepDTO> result = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findTimeTableForRunSQL)) {
            stmt.setInt(1, idLine);
            stmt.setInt(2, idStartStation);
            stmt.setInt(3, idLine);
            ResultSet rs = stmt.executeQuery();

            LocalTime depPrev = LocalTime.parse(departureTime);
            LocalTime arrCurr;
            boolean isFirst = true;
            while (rs.next()) {
                int idStation = rs.getInt("id_station");
                String stationName = rs.getString("location");
                String intervalStr = rs.getString("time_to_next_station");
                Duration timeToNext = parsePgInterval(intervalStr);

                String arr, dep;
                if (isFirst) {
                    arr = "------";
                    dep = depPrev.toString();
                    isFirst = false;
                } else {
                    arrCurr = depPrev.plus(timeToNext != null ? timeToNext : Duration.ZERO);
                    arr = arrCurr.toString();
                    if (!rs.isLast()) {
                        depPrev = arrCurr.plusMinutes(1);
                        dep = depPrev.toString();
                    } else {
                        dep = "------";
                    }
                }
                result.add(new TimeTableDTO.StationArrAndDepDTO(idStation, stationName, arr, dep));
            }
        }
        return result;
    }
}
