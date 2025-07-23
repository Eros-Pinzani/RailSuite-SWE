package dao;

import domain.Run;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunDaoImp implements RunDao {
    private static final String selectRunQuery =
            "SELECT * FROM run WHERE id_line = ? AND id_convoy = ?";
    private static final String selectRunByLineConvoyStaffQuery =
            "SELECT * FROM run WHERE id_line = ? AND id_convoy = ? AND id_staff = ?";
    private static final String selectRunByStaffAndConvoyQuery =
            "SELECT * FROM run WHERE id_staff = ? AND id_convoy = ?";
    private static final String selectRunByStaffAndLineQuery = "SELECT * FROM run WHERE id_staff = ? AND id_line = ?";
    private static final String selectAllRunsQuery = "SELECT * FROM run";
    private static final String deleteRunQuery = "DELETE FROM run WHERE id_line = ? AND id_convoy = ?";
    private static final String insertRunQuery =
            "INSERT INTO run (id_line, id_convoy, id_staff, time_departure, time_arrival, id_first_station, id_last_station) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String updateRunQuery =
            "UPDATE run SET id_staff = ?, time_departure = ?, time_arrival = ?, id_first_station = ?, id_last_station = ?" +
            "WHERE id_line = ? AND id_convoy = ?";
    private static final String selectRunsByStaffQuery = "SELECT * FROM run WHERE id_staff = ?";
    private static final String selectRunsByLineQuery = "SELECT * FROM run WHERE id_line = ?";
    private static final String selectRunsByConvoyQuery = "SELECT * FROM run WHERE id_convoy = ?";
    private static final String selectRunsByFirstStationQuery = "SELECT * FROM run WHERE id_first_station = ?";
    private static final String selectRunsByLastStationQuery = "SELECT * FROM run WHERE id_last_station = ?";
    private static final String selectRunsByFirstStationAndDepartureQuery =
            "SELECT * FROM run WHERE id_first_station = ? AND time_departure = ?";



    @Override
    public List<Run> selectAllRuns() throws SQLException {
        List<Run> runs = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectAllRunsQuery);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                runs.add(Run.of(
                    rs.getInt("id_line"),
                    rs.getInt("id_convoy"),
                    rs.getInt("id_staff"),
                    rs.getTime("time_departure"),
                    rs.getTime("time_arrival"),
                    rs.getInt("id_first_station"),
                    rs.getInt("id_last_station")
                ));
            }
        } catch (SQLException e) {
            throw new SQLException("Error selecting all runs", e);
        }
        return runs;
    }

    @Override
    public boolean removeRun(int idLine, int idConvoy) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteRunQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error removing run: " + idLine + ", " + idConvoy, e);
        }
    }

    @Override
    public Run createRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertRunQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            pstmt.setTime(4, timeDeparture);
            pstmt.setTime(5, timeArrival);
            pstmt.setInt(6, idFirstStation);
            pstmt.setInt(7, idLastStation);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return Run.of(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating run", e);
        }
        return null;
    }

    @Override
    public boolean updateRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateRunQuery)) {
            pstmt.setInt(1, idStaff);
            pstmt.setTime(2, timeDeparture);
            pstmt.setTime(3, timeArrival);
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

    @Override
    public Run selectRunByLineAndConvoy(int idLine, int idConvoy) throws SQLException {
        return getRun(idLine, idConvoy, selectRunQuery);
    }

    @Override
    public Run selectRun(int idLine, int idConvoy, int idStaff) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunByLineConvoyStaffQuery)) {
            pstmt.setInt(1, idLine);
            pstmt.setInt(2, idConvoy);
            pstmt.setInt(3, idStaff);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Run.of(
                        rs.getInt("id_line"),
                        rs.getInt("id_convoy"),
                        rs.getInt("id_staff"),
                        rs.getTime("time_departure"),
                        rs.getTime("time_arrival"),
                        rs.getInt("id_first_station"),
                        rs.getInt("id_last_station")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Run selectRunByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException {
        return getRun(idStaff, idConvoy, selectRunByStaffAndConvoyQuery);
    }

    @Override
    public Run selectRunByStaffAndLine(int idStaff, int idLine) throws SQLException {
        return getRun(idStaff, idLine, selectRunByStaffAndLineQuery);
    }

    private Run getRun(int idStaff, int idLine, String query) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStaff);
            pstmt.setInt(2, idLine);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Run.of(
                        rs.getInt("id_line"),
                        rs.getInt("id_convoy"),
                        rs.getInt("id_staff"),
                        rs.getTime("time_departure"),
                        rs.getTime("time_arrival"),
                        rs.getInt("id_first_station"),
                        rs.getInt("id_last_station")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Run> selectRunsByStaff(int idStaff) throws SQLException {
        return getRuns(idStaff, selectRunsByStaffQuery);
    }

    private List<Run> getRuns(int idStaff, String query) throws SQLException {
        List<Run> runs = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStaff);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    runs.add(Run.of(
                        rs.getInt("id_line"),
                        rs.getInt("id_convoy"),
                        rs.getInt("id_staff"),
                        rs.getTime("time_departure"),
                        rs.getTime("time_arrival"),
                        rs.getInt("id_first_station"),
                        rs.getInt("id_last_station")
                    ));
                }
            }
        }
        return runs;
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
    public List<Run> selectRunsByFirstStationAndDeparture(int idFirstStation, Time timeDeparture) throws SQLException {
        List<Run> runs = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectRunsByFirstStationAndDepartureQuery)) {
            pstmt.setInt(1, idFirstStation);
            pstmt.setTime(2, timeDeparture);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    runs.add(Run.of(
                        rs.getInt("id_line"),
                        rs.getInt("id_convoy"),
                        rs.getInt("id_staff"),
                        rs.getTime("time_departure"),
                        rs.getTime("time_arrival"),
                        rs.getInt("id_first_station"),
                        rs.getInt("id_last_station")
                    ));
                }
            }
        }
        return runs;
    }
}
