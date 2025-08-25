package dao;

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
    // SQL query to select a run by line and convoy
    private static final String selectRunByLineAndConvoyQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ? AND r.id_line = ?""";
    // SQL query to select a run by line, convoy, and staff
    private static final String selectRunByLineConvoyAndStaffQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ? AND r.id_line = ? AND r.id_staff = ?
            """;
    // SQL query to select a run by staff and convoy
    private static final String selectRunByStaffAndConvoyQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ? AND r.id_staff""";
    // SQL query to select a run by staff and line
    private static final String selectRunByStaffAndLineQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_line = ? AND r.id_staff = ?""";
    // SQL query to delete a run by line and convoy
    private static final String deleteRunQuery = "DELETE FROM run WHERE id_line = ? AND id_convoy = ? AND id_staff = ? AND time_departure = ?";
    // SQL query to insert a new run
    private static final String insertRunQuery =
            "INSERT INTO run (id_line, id_convoy, id_staff, time_departure, time_arrival, id_first_station, id_last_station) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
    // SQL query to update an existing run
    private static final String updateRunQuery =
            "UPDATE run SET id_staff = ?, time_departure = ?, time_arrival = ?, id_first_station = ?, id_last_station = ?" +
                    "WHERE id_line = ? AND id_convoy = ?";
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
    // SQL query to select all runs by line
    private static final String selectRunsByLineQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_line = ?""";
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
    // SQL query to select all runs by first station
    private static final String selectRunsByFirstStationQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_first_station = ?""";
    // SQL query to select all runs by last station
    private static final String selectRunsByLastStationQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_last_station = ?""";
    // SQL query to select all runs by first station and departure time
    private static final String selectRunsByFirstStationAndDepartureQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_first_station = ? AND time_departure = ?\s""";

    private static final String selectRunDTOdetails = """
            SELECT r.id_line, l.name,
                r.id_convoy,
                r.id_staff, s.name, s.surname, s.email,
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
    private static final String selectRunsForConvoyAfterTimeQuery = """
            SELECT r.id_line, r.id_convoy, r.id_staff, r.time_departure
            FROM run r
            WHERE r.id_line = ? AND r.id_convoy = ? AND r.id_staff = ? AND r.time_departure > ?
            """;
    private static final String selectRunsByConvoyAndTimeForTakeFutureRunsQuery = """
            SELECT r.id_line, l.name as line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.time_departure, r.time_arrival,
                   r.id_first_station, fs.location as first_station_name, r.id_last_station, ls.location as last_station_name
            FROM run r
                LEFT JOIN line l ON r.id_line = l.id_line
                LEFT JOIN staff s ON r.id_staff = s.id_staff
                LEFT JOIN station fs ON r.id_first_station = fs.id_station
                LEFT JOIN station ls ON r.id_last_station = ls.id_station
            WHERE r.id_convoy = ? AND r.time_departure > now()
            """;


    private Run resultSetToRun(ResultSet rs) throws SQLException {
        return Run.of(
                rs.getInt("id_line"),
                rs.getString("line_name"),
                rs.getInt("id_convoy"),
                rs.getInt("id_staff"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getInt("id_first_station"),
                rs.getString("first_station_name"),
                rs.getInt("id_last_station"),
                rs.getString("last_station_name"),
                rs.getTimestamp("time_departure"),
                rs.getTimestamp("time_arrival")
        );
    }

    private List<Run> resultSetToRunList(ResultSet rs) throws SQLException {
        List<Run> runs = new ArrayList<>();
        while (rs.next()) {
            runs.add(resultSetToRun(rs));
        }
        if (runs.isEmpty()) {
            return null;
        }
        return runs;
    }

    @Override
    public List<Run> selectRunsByLineAndConvoy(int idLine, int idConvoy) throws SQLException {
        // Executes the query to get a run by line and convoy
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByLineAndConvoyQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
    }

    @Override
    public Run selectRunByLineConvoyAndStaff(int idLine, int idConvoy, int idStaff) throws SQLException {
        // Executes the query to get a run by line, convoy, and staff
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByLineConvoyAndStaffQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToRun(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Run> selectRunsByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException {
        // Executes the query to get a run by staff and convoy
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByStaffAndConvoyQuery)) {
            pstmt.setInt(1, idStaff);
            pstmt.setInt(2, idConvoy);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
    }

    @Override
    public List<Run> selectRunsByStaffAndLine(int idStaff, int idLine) throws SQLException {
        // Executes the query to get a run by staff and line
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByStaffAndLineQuery)) {
            pstmt.setInt(1, idStaff);
            pstmt.setInt(2, idLine);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
    }

    @Override
    public boolean deleteRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        // Executes the query to delete a specific run
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteRunQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            pstmt.setTimestamp(4, timeDeparture);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing run: " + idLine + ", " + idConvoy + ", " + idStaff, e);
        }
    }

    @Override
    public boolean createRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        // Executes the query to insert a new run into the database
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertRunQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            pstmt.setTimestamp(4, timeDeparture);
            pstmt.setTimestamp(5, timeArrival);
            pstmt.setInt(6, idFirstStation);
            pstmt.setInt(7, idLastStation);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating run", e);
        }
        return false;
    }

    @Override
    public boolean updateRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        // Executes the query to update an existing run
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateRunQuery)) {
            pstmt.setInt(1, idStaff);
            pstmt.setTimestamp(2, timeDeparture);
            pstmt.setTimestamp(3, timeArrival);
            pstmt.setInt(4, idFirstStation);
            pstmt.setInt(5, idLastStation);
            pstmt.setInt(6, idLine);
            pstmt.setInt(7, idConvoy);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error updating run: " + idLine + ", " + idConvoy, e);
        }
    }

    private List<Run> getRuns(int id, String query) throws SQLException {
        // Utility method to execute queries that return multiple runs filtered by a parameter
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
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
    public List<Run> selectRunsByLine(int idLine) throws SQLException {
        return getRuns(idLine, selectRunsByLineQuery);
    }

    @Override
    public List<Run> selectRunsByConvoy(int idConvoy) throws SQLException {
        return getRuns(idConvoy, selectRunsByConvoyQuery);
    }

    @Override
    public List<Run> selectRunsByFirstStation(int idFirstStation) throws SQLException {
        return getRuns(idFirstStation, selectRunsByFirstStationQuery);
    }

    @Override
    public List<Run> selectRunsByLastStation(int idLastStation) throws SQLException {
        return getRuns(idLastStation, selectRunsByLastStationQuery);
    }

    @Override
    public List<Run> selectRunsByFirstStationAndDeparture(int idFirstStation, Timestamp timeDeparture) throws SQLException {
        // Executes the query to get all runs that start from a station and have a specific departure time
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsByFirstStationAndDepartureQuery)) {
            pstmt.setInt(1, idFirstStation);
            pstmt.setTimestamp(2, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
        }
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
                    runs.add(resultSetToRun(rs));
                }
                return runs;
            }
        }
    }


    @Override
    public RunDTO selectRunDTODetails(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        // Executes the query to get run details as a RunDTO
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunDTOdetails)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            pstmt.setTimestamp(4, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RunDTO(
                            rs.getInt("id_line"),
                            rs.getString("name"),
                            rs.getInt("id_convoy"),
                            rs.getInt("id_staff"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getTimestamp("time_departure"),
                            rs.getString("location")
                    );
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
            pstmt.setInt(1, idStaff);
            pstmt.setTimestamp(2, timeDeparture);
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
             PreparedStatement pstmt = conn.prepareStatement(selectRunsForConvoyAfterTimeQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            pstmt.setTimestamp(4, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new SQLException("Error on findRuns By convoy after time", e);
        }
    }

    @Override
    public List<Run> selectRunsByConvoyAndTimeForTakeFutureRuns(int idConvoy, Timestamp timeDeparture) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsByConvoyAndTimeForTakeFutureRunsQuery)) {
            pstmt.setInt(1, idConvoy);
            pstmt.setTimestamp(2, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToRunList(rs);
            }
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
    boolean replaceFutureRunsConvoy(int idConvoy, int newIdConvoy, RunDTO run) throws SQLException {

    }

}
