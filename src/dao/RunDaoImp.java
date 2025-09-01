package dao;

import domain.DTO.ConvoyTableDTO;
import domain.DTO.RunDTO;
import domain.Run;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the RunDao interface.
 * Contains SQL queries and logic for accessing run data.
 */
public class RunDaoImp implements RunDao {
    // SQL query to select a run by line, convoy, and staff
    private static final String selectRunByLineConvoyAndStaffQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ? AND r.id_line = ? AND r.id_staff = ? AND r.time_departure = ? AND r.id_first_station = ?
            """;
    // SQL query to delete a run by line and convoy
    private static final String deleteRunQuery = "DELETE FROM run WHERE id_line = ? AND id_convoy = ? AND id_staff = ? AND time_departure = ?";
    // SQL query to insert a new run
    private static final String insertRunQuery =
            "INSERT INTO run (id_line, id_convoy, id_staff, time_departure, time_arrival, id_first_station, id_last_station) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
    // SQL query to select all runs by staff
    private static final String selectRunsByStaffQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_staff = ?""";
    // SQL query to select all runs by convoy
    private static final String selectRunsByConvoyQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ?""";
    private static final String selectRunDTOdetails = """
            SELECT r.id_line, l.name,
                r.id_convoy,
                r.id_staff, s.name as staff_name, s.surname, s.email,
                r.time_departure,
                st.location
            FROM run r
            INNER JOIN line l ON l.id_line = r.id_line
            INNER JOIN staff s ON s.id_staff = r.id_staff
            INNER JOIN station st ON st.id_station = r.id_first_station
            WHERE r.id_line = ? AND r.id_convoy = ? AND r.id_staff = ? AND time_departure = ?
            """;
    private static final String selectRunsForOperatorAfterTimeQuery = """
            SELECT r.id_staff, r.time_departure
            FROM run r
            WHERE r.id_staff = ? AND r.time_departure > ?
            """;
    // Query per tutte le corse future di un convoglio dopo un certo orario
    private static final String selectRunsForConvoyAfterTimeSimpleQuery = """
        SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
               r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
        FROM run r
            LEFT JOIN line l ON r.id_line = l.id_line
            LEFT JOIN staff s ON r.id_staff = s.id_staff
            LEFT JOIN station fs ON r.id_first_station = fs.id_station
            LEFT JOIN station ls ON r.id_last_station = ls.id_station
        WHERE r.id_convoy = ? AND r.time_departure > ?""";
    private static final String selectConvoyTableDTOQuery = """
        SELECT c.id_convoy,
               MIN(car.model) AS model,
               cp.status,
               COUNT(car.id_carriage) AS carriage_count,
               SUM(car.capacity) AS capacity,
               MIN(car.model_type) AS model_type
        FROM convoy c
        LEFT JOIN carriage car ON c.id_convoy = car.id_convoy
        LEFT JOIN convoy_pool cp ON c.id_convoy = cp.id_convoy
        WHERE c.id_convoy = ?
          AND EXISTS (
              SELECT 1 FROM run r
              WHERE r.id_convoy = c.id_convoy
                AND r.time_departure > now()
          )
        GROUP BY c.id_convoy, cp.status
    """;


    // --- MAPPING IN RUNMAPPER ---
    private List<Run> resultSetToRunList(ResultSet rs) throws SQLException {
        List<Run> runs = new ArrayList<>();
        while (rs.next()) {
            runs.add(mapper.RunMapper.toDomain(rs));
        }
        if (runs.isEmpty()) {
            return null;
        }
        return runs;
    }

    private Run resultSetToRun(ResultSet rs) throws SQLException {
        return mapper.RunMapper.toDomain(rs);
    }

    @Override
    public  Run selectRunByLineConvoyAndStaff(int idLine, int idConvoy, Timestamp timeDeparture, int idStaff, int idFirstStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByLineConvoyAndStaffQuery)) {
            //
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            if(timeDeparture != null) pstmt.setTimestamp(4, timeDeparture);
            if(idFirstStation != 0) pstmt.setInt(5, idFirstStation);
            //mapper.RunMapper.setRunKeyParams(pstmt, idLine, idConvoy, idStaff, timeDeparture, idFirstStation);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.RunMapper.toDomain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteRunQuery)) {
            mapper.RunMapper.setRunDeleteKeyParams(pstmt, idLine, idConvoy, idStaff, timeDeparture);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing run: " + idLine + ", " + idConvoy + ", " + idStaff, e);
        }
    }

    @Override
    public boolean createRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertRunQuery)) {
            mapper.RunMapper.setInsertRunParams(pstmt, idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error creating run", e);
        }
    }

    private List<Run> getRuns(int id, String query) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            mapper.RunMapper.setIdParam(pstmt, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
    }

    @Override
    public List<Run> selectRunsByStaff(int idStaff) throws SQLException {
        return getRuns(idStaff, selectRunsByStaffQuery);
    }

    @Override
    public List<Run> selectRunsByConvoy(int idConvoy) throws SQLException {
        return getRuns(idConvoy, selectRunsByConvoyQuery);
    }

    @Override
    public List<Run> searchRunsByDay(String lineName, String convoyId, String staffNameSurname, String firstStationName, java.sql.Timestamp dayStart, java.sql.Timestamp dayEnd) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                    SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                           r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
                    FROM run r
                        LEFT JOIN line l ON r.id_line = l.id_line
                        LEFT JOIN staff s ON r.id_staff = s.id_staff
                        LEFT JOIN station fs ON r.id_first_station = fs.id_station
                        LEFT JOIN station ls ON r.id_last_station = ls.id_station
                    WHERE r.time_departure >= ? AND r.time_departure <= ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(dayStart);
        params.add(dayEnd);
        if (lineName != null && !lineName.isBlank()) {
            sql.append(" AND l.name = ?");
            params.add(lineName);
        }
        if (convoyId != null && !convoyId.isBlank()) {
            sql.append(" AND CAST(r.id_convoy AS TEXT) = ?");
            params.add(convoyId);
        }
        if (staffNameSurname != null && !staffNameSurname.isBlank()) {
            sql.append(" AND (s.name || ' ' || s.surname) = ?");
            params.add(staffNameSurname);
        }
        if (firstStationName != null && !firstStationName.isBlank()) {
            sql.append(" AND fs.location = ?");
            params.add(firstStationName);
        }
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Run> runs = new ArrayList<>();
                while (rs.next()) {
                    runs.add(mapper.RunMapper.toDomain(rs));
                }
                return runs;
            }
        }
    }


    @Override
    public RunDTO selectRunDTODetails(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunDTOdetails)) {
            mapper.RunMapper.setRunKeyParams(pstmt, idLine, idConvoy, idStaff, timeDeparture, 0);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.RunMapper.toRunDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error selecting run details", e);
        }
        return null;
    }

    @Override
    public boolean findRunsByStaffAfterTime(int idStaff, Timestamp timeDeparture) throws SQLException {
        // Executes the query to check if there are runs for a staff member after a specific time
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsForOperatorAfterTimeQuery)) {
            mapper.RunMapper.setIdAndTimestampParams(pstmt, idStaff, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new SQLException("Error on findRuns By staff after time", e);
        }
    }

    @Override
    public boolean findRunsByConvoyAfterTime(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsForConvoyAfterTimeSimpleQuery)) {
            mapper.RunMapper.setIdAndTimestampParams(pstmt, idConvoy, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new SQLException("Error on findRuns By convoy after time", e);
        }
    }

    @Override
    public List<ConvoyTableDTO> selectRunsByConvoyAndTimeForTakeFutureRuns(int idConvoy, Timestamp timeDeparture) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectConvoyTableDTOQuery)) {
            mapper.RunMapper.setIdParam(pstmt, idConvoy);
            List<ConvoyTableDTO> convoyTableDTOList = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    convoyTableDTOList.add(mapper.RunMapper.toConvoyTableDTO(rs));
                }
            }
            return convoyTableDTOList;
        }
    }

    private static final String replaceFutureRunsConvoyQuery = """
            UPDATE run
            SET id_convoy = ?
            WHERE id_convoy = ? AND time_departure > NOW()
            """;
    private static final String selectConvoyByIdQuery = """
            UPDATE run
            SET id_convoy = ?
            WHERE id_convoy = ? AND time_departure = ? AND id_line = ? AND id_staff = ?
            """;

    @Override
    public boolean replaceFutureRunsConvoy(int idConvoy, int newIdConvoy, RunDTO run) throws SQLException {
        int affectedRows = 0;
        try (Connection conn = PostgresConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(replaceFutureRunsConvoyQuery)) {
                mapper.RunMapper.setReplaceFutureRunsParams(pstmt, newIdConvoy, idConvoy);
                affectedRows += pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(selectConvoyByIdQuery)) {
                mapper.RunMapper.setReplaceFutureRunsWithTimeParams(pstmt, newIdConvoy, idConvoy, run.getTimeDeparture(), run.getIdLine(), run.getIdStaff());
                affectedRows += pstmt.executeUpdate();
            }
        }
        return affectedRows > 0;
    }

    @Override
    public void updateRunStaff(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, int idStaff1) throws SQLException {
        String sql = "UPDATE run SET id_staff = ? WHERE id_line = ? AND id_convoy = ? AND id_staff = ? AND time_departure = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            mapper.RunMapper.setUpdateRunStaffParams(pstmt, idStaff1, idLine, idConvoy, idStaff, timeDeparture);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating run staff", e);
        }
    }

    @Override
    public boolean updateRunDepartureTime(int idLine, int idConvoy, int idStaff, Timestamp oldDeparture, Timestamp newDeparture) throws SQLException {
        String sql = "UPDATE run SET time_departure = ? WHERE id_line = ? AND id_convoy = ? AND id_staff = ? AND time_departure = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            mapper.RunMapper.setUpdateRunDepartureTimeParams(ps, newDeparture, idLine, idConvoy, idStaff, oldDeparture);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private static final String selectAllRunQuery = """
        SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
               r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
        FROM run r
            LEFT JOIN line l ON r.id_line = l.id_line
            LEFT JOIN staff s ON r.id_staff = s.id_staff
            LEFT JOIN station fs ON r.id_first_station = fs.id_station
            LEFT JOIN station ls ON r.id_last_station = ls.id_station
    """;

    @Override
    public List<Run> selectAllRun() throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectAllRunQuery)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
    }

    @Override
    public List<Run> selectRunsForConvoyAfterTime(int idConvoy, java.sql.Timestamp afterTime) throws SQLException {
        List<Run> runs;
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsForConvoyAfterTimeSimpleQuery)) {
            pstmt.setInt(1, idConvoy);
            pstmt.setTimestamp(2, afterTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                runs = resultSetToRunList(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero delle corse future del convoglio", e);
        }
        if (runs == null) return new ArrayList<>();
        return runs;
    }
}
