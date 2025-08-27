package dao;

import domain.TimeTable;
import domain.LineStation;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the LineStationDao interface.
 * Contains SQL queries and logic for accessing line-station relationship data.
 */
class LineStationDaoImp implements LineStationDao {
    LineStationDaoImp() {
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

    // Oggetto interno per contenere i dati di riga
    private static class LineStationRow {
        int idStation;
        String location;
        int stationOrder;
        Duration timeToNext; // null se nella tabella è null

        LineStationRow(int idStation, String location, int stationOrder, Duration timeToNext) {
            this.idStation = idStation;
            this.location = location;
            this.stationOrder = stationOrder;
            this.timeToNext = timeToNext;
        }
    }

    @Override
    public List<TimeTable.StationArrAndDep> findTimeTableForRun(
            int idLine,
            int idStartStation,
            String departureTime) throws SQLException {

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findTimeTableForRunSQL)) {
            stmt.setInt(1, idLine);
            stmt.setInt(2, idStartStation);
            stmt.setInt(3, idLine);
            ResultSet rs = stmt.executeQuery();

            // Leggo tutte le righe in una lista di oggetti
            List<LineStationRow> rows = new ArrayList<>();
            while (rs.next()) {
                int idStation = rs.getInt("id_station");
                String location = rs.getString("location");
                int stationOrder = rs.getInt("station_order");
                String intervalStr = rs.getString("time_to_next_station");
                Duration d = parsePgInterval(intervalStr); // può ritornare null
                rows.add(new LineStationRow(idStation, location, stationOrder, d));
            }

            if (rows.isEmpty()) {
                return Collections.emptyList();
            }

            // Se la prima riga non corrisponde alla stazione di partenza, invertiamo
            boolean forward = rows.getFirst().idStation == idStartStation;
            if (!forward) {
                Collections.reverse(rows);
            }

            // Calcolo arrivi/partenze
            LocalTime depPrev = LocalTime.parse(departureTime);
            List<TimeTable.StationArrAndDep> result = new ArrayList<>();

            for (int i = 0; i < rows.size(); i++) {
                LineStationRow r = rows.get(i);
                String arr, dep;

                if (i == 0) {
                    // stazione di partenza
                    arr = "------";
                    dep = depPrev.toString();
                } else {
                    // per arrivare alla stazione i uso il time_to_next della stazione (i-1)
                    Duration prevSegment = rows.get(i - 1).timeToNext;
                    if (prevSegment == null) {
                        // gestione del NULL: log e assumo Duration.ZERO
                        System.out.println("Warning: time_to_next_station is NULL for station "
                                + rows.get(i - 1).idStation + " — assuming Duration.ZERO");
                        prevSegment = Duration.ZERO;
                    }
                    LocalTime arrCurr = depPrev.plus(prevSegment);
                    arr = arrCurr.toString();

                    if (i < rows.size() - 1) {
                        // fermata di 1 minuto
                        depPrev = arrCurr.plusMinutes(1);
                        dep = depPrev.toString();
                    } else {
                        // ultima stazione: nessuna partenza
                        dep = "------";
                    }
                }

                result.add(new TimeTable.StationArrAndDep(r.idStation, r.location, arr, dep));
            }

            return result;
        }
    }
}
