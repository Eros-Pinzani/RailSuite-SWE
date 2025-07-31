package dao;

import domain.RunRaw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the RunRawDao interface.
 * Provides methods to retrieve RunRaw records from the database.
 */
public class RunRawDaoImp implements RunRawDao {
    /**
     * SQL query to select all RunRaw records with related information.
     */
    private static final String selectRunRawQuery = """
            SELECT l.id_line, l.name AS line_name,
                c.id_convoy,
                s.id_staff, s.name AS staff_name, s.surname AS staff_surname,
                st.id_station AS id_first_station, st.location AS first_station_location,
                r.time_departure
            FROM run r
            FULL OUTER JOIN line l ON r.id_line = l.id_line
            FULL OUTER JOIN (Select id_staff, name, surname, type_of_staff FROM staff WHERE type_of_staff = 'OPERATOR') s ON r.id_staff = s.id_staff
            FULL OUTER JOIN convoy c ON r.id_convoy = c.id_convoy
            FULL OUTER JOIN (SELECT id_station, location FROM station WHERE is_head = true) st ON r.id_first_station = st.id_station""";

    /**
     * Retrieves all RunRaw records from the database.
     * @return a list of RunRaw objects
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<RunRaw> selectAllRunRaws() throws SQLException {
        List<RunRaw> runRaws = new ArrayList<>();
        try (
                Connection conn = PostgresConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectRunRawQuery);
                ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    RunRaw runRaw = RunRaw.of(
                            rs.getInt("id_line"),
                            rs.getString("line_name"),
                            rs.getInt("id_convoy"),
                            rs.getInt("id_staff"),
                            rs.getString("staff_name"),
                            rs.getString("staff_surname"),
                            rs.getInt("id_first_station"),
                            rs.getString("first_station_location"),
                            rs.getTimestamp("time_departure")
                    );
                    runRaws.add(runRaw);
                }
            } catch (SQLException e) {
                throw new SQLException("Error selecting all RunRaw info", e);
        }
        return runRaws;
    }
}
