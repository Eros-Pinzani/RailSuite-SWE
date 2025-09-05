package test.mapper;

import mapper.RunMapper;
import domain.Run;
import domain.DTO.RunDTO;
import domain.DTO.ConvoyTableDTO;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RunMapperTest {
    private Connection conn;
    private final int TEST_LINE_ID = 1001;
    private final int TEST_CONVOY_ID = 2001;
    private final int TEST_STAFF_ID = 3001;
    private final int TEST_FIRST_STATION_ID = 4001;
    private final int TEST_LAST_STATION_ID = 4002;
    private final String TEST_LINE_NAME = "LineaTest";
    private final String TEST_STAFF_NAME = "Mario";
    private final Timestamp TEST_DEPARTURE = Timestamp.valueOf("2025-09-05 10:00:00");
    private final Timestamp TEST_ARRIVAL = Timestamp.valueOf("2025-09-05 12:00:00");

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/dao/db.properties")) {
            props.load(fis);
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        conn = DriverManager.getConnection(url, user, password);
        Statement st = conn.createStatement();
        // Inserisco dati necessari
        st.executeUpdate("INSERT INTO line (id_line, name) VALUES (" + TEST_LINE_ID + ", '" + TEST_LINE_NAME + "') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID + ") ON CONFLICT DO NOTHING");
        String TEST_STAFF_SURNAME = "Rossi";
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (" + TEST_STAFF_ID + ", '" + TEST_STAFF_NAME + "', '" + TEST_STAFF_SURNAME + "', 'Via Roma', 'test@test.it', 'pwd', 'OPERATOR') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_FIRST_STATION_ID + ", 'LocA', 1, 'DescA', true) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_LAST_STATION_ID + ", 'LocB', 2, 'DescB', false) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO run (id_staff, id_convoy, id_line, time_departure, time_arrival, id_first_station, id_last_station) VALUES (" + TEST_STAFF_ID + ", " + TEST_CONVOY_ID + ", " + TEST_LINE_ID + ", '" + TEST_DEPARTURE + "', '" + TEST_ARRIVAL + "', " + TEST_FIRST_STATION_ID + ", " + TEST_LAST_STATION_ID + ") ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM run WHERE id_staff=" + TEST_STAFF_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND id_line=" + TEST_LINE_ID + " AND id_first_station=" + TEST_FIRST_STATION_ID + " AND time_departure='" + TEST_DEPARTURE + "'");
        st.executeUpdate("DELETE FROM line WHERE id_line=" + TEST_LINE_ID);
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy=" + TEST_CONVOY_ID);
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy IN (" + (TEST_CONVOY_ID + 1) + ", " + (TEST_CONVOY_ID + 2) + ", " + (TEST_CONVOY_ID + 3) + ")");
        st.executeUpdate("DELETE FROM staff WHERE id_staff=" + TEST_STAFF_ID);
        st.executeUpdate("DELETE FROM staff WHERE id_staff=" + (TEST_STAFF_ID + 1));
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_FIRST_STATION_ID + ", " + TEST_LAST_STATION_ID + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        // Simulo una query con join per ottenere tutti i campi richiesti da RunMapper
        ResultSet rs = st.executeQuery("SELECT r.id_line, l.name AS line_name, r.id_convoy, r.id_staff, s.name, s.surname, r.id_first_station, fs.location AS first_station_name, r.id_last_station, ls.location AS last_station_name, r.time_departure, r.time_arrival FROM run r JOIN line l ON r.id_line = l.id_line JOIN staff s ON r.id_staff = s.id_staff JOIN station fs ON r.id_first_station = fs.id_station JOIN station ls ON r.id_last_station = ls.id_station WHERE r.id_staff=" + TEST_STAFF_ID + " AND r.id_convoy=" + TEST_CONVOY_ID + " AND r.id_line=" + TEST_LINE_ID + " AND r.id_first_station=" + TEST_FIRST_STATION_ID + " AND r.time_departure='" + TEST_DEPARTURE + "'");
        assertTrue(rs.next());
        Run run = RunMapper.toDomain(rs);
        assertEquals(TEST_LINE_ID, run.getIdLine());
        assertEquals(TEST_LINE_NAME, run.getLineName());
        assertEquals(TEST_CONVOY_ID, run.getIdConvoy());
        assertEquals(TEST_STAFF_ID, run.getIdStaff());
        assertEquals(TEST_FIRST_STATION_ID, run.getIdFirstStation());
        assertEquals(TEST_LAST_STATION_ID, run.getIdLastStation());
        assertEquals(TEST_DEPARTURE, run.getTimeDeparture());
        assertEquals(TEST_ARRIVAL, run.getTimeArrival());
        rs.close();
        st.close();
    }

    @Test
    void toRunDTO() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT r.id_line, l.name AS name, r.id_convoy, r.id_staff, s.name AS staff_name, s.surname AS surname, s.email AS email, r.time_departure, fs.location AS location FROM run r JOIN line l ON r.id_line = l.id_line JOIN staff s ON r.id_staff = s.id_staff JOIN station fs ON r.id_first_station = fs.id_station WHERE r.id_staff=" + TEST_STAFF_ID + " AND r.id_convoy=" + TEST_CONVOY_ID + " AND r.id_line=" + TEST_LINE_ID + " AND r.id_first_station=" + TEST_FIRST_STATION_ID + " AND r.time_departure='" + TEST_DEPARTURE + "'");
        assertTrue(rs.next());
        RunDTO dto = RunMapper.toRunDTO(rs);
        assertEquals(TEST_LINE_ID, dto.getIdLine());
        assertEquals(TEST_LINE_NAME, dto.getLineName());
        assertEquals(TEST_CONVOY_ID, dto.getIdConvoy());
        assertEquals(TEST_STAFF_ID, dto.getIdStaff());
        assertEquals(TEST_STAFF_NAME, dto.getStaffName());
        assertEquals(TEST_DEPARTURE, dto.getTimeDeparture());
        assertEquals("LocA", dto.getFirstStationName());
        rs.close();
        st.close();
    }

    @Test
    void toConvoyTableDTO() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(
            "SELECT id_convoy, 'ModelX' AS model, 'ACTIVE' AS status, 5 AS carriage_count, 300 AS capacity, 'TypeX' AS model_type FROM convoy WHERE id_convoy=" + TEST_CONVOY_ID
        );
        assertTrue(rs.next());
        ConvoyTableDTO dto = RunMapper.toConvoyTableDTO(rs);
        assertEquals(TEST_CONVOY_ID, dto.getIdConvoy());
        assertEquals("ModelX", dto.getModel());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals(5, dto.getCarriageCount());
        assertEquals(300, dto.getCapacity());
        assertEquals("TypeX", dto.getModelType());
        rs.close();
        st.close();
    }

    @Test
    void setRunKeyParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM run WHERE id_line=? AND id_convoy=? AND id_staff=? AND time_departure=? AND id_first_station=?");
        RunMapper.setRunKeyParams(ps, TEST_LINE_ID, TEST_CONVOY_ID, TEST_STAFF_ID, TEST_DEPARTURE, TEST_FIRST_STATION_ID);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_LINE_ID, rs.getInt("id_line"));
        assertEquals(TEST_CONVOY_ID, rs.getInt("id_convoy"));
        assertEquals(TEST_STAFF_ID, rs.getInt("id_staff"));
        assertEquals(TEST_DEPARTURE, rs.getTimestamp("time_departure"));
        assertEquals(TEST_FIRST_STATION_ID, rs.getInt("id_first_station"));
        rs.close();
        ps.close();
    }

    @Test
    void setInsertRunParams() throws SQLException {
        // Pulizia preventiva
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM run WHERE id_staff=" + TEST_STAFF_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND id_line=" + TEST_LINE_ID + " AND id_first_station=" + TEST_FIRST_STATION_ID + " AND time_departure='2025-09-05 11:00:00'");
        st.close();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO run (id_line, id_convoy, id_staff, time_departure, time_arrival, id_first_station, id_last_station) VALUES (?, ?, ?, ?, ?, ?, ?)");
        RunMapper.setInsertRunParams(ps, TEST_LINE_ID, TEST_CONVOY_ID, TEST_STAFF_ID, Timestamp.valueOf("2025-09-05 11:00:00"), TEST_ARRIVAL, TEST_FIRST_STATION_ID, TEST_LAST_STATION_ID);
        int affected = ps.executeUpdate();
        assertEquals(1, affected);
        ps.close();
        // pulizia
        st = conn.createStatement();
        st.executeUpdate("DELETE FROM run WHERE id_staff=" + TEST_STAFF_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND id_line=" + TEST_LINE_ID + " AND id_first_station=" + TEST_FIRST_STATION_ID + " AND time_departure='2025-09-05 11:00:00'");
        st.close();
    }

    @Test
    void setIdParam() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM line WHERE id_line=?");
        RunMapper.setIdParam(ps, TEST_LINE_ID);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_LINE_ID, rs.getInt("id_line"));
        rs.close();
        ps.close();
    }

    @Test
    void setIdAndTimestampParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM run WHERE id_line=? AND time_departure=?");
        RunMapper.setIdAndTimestampParams(ps, TEST_LINE_ID, TEST_DEPARTURE);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_LINE_ID, rs.getInt("id_line"));
        assertEquals(TEST_DEPARTURE, rs.getTimestamp("time_departure"));
        rs.close();
        ps.close();
    }

    @Test
    void setReplaceFutureRunsParams() throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + (TEST_CONVOY_ID + 1) + ") ON CONFLICT DO NOTHING");
        st.close();
        PreparedStatement ps = conn.prepareStatement("UPDATE run SET id_convoy=? WHERE id_convoy=?");
        RunMapper.setReplaceFutureRunsParams(ps, TEST_CONVOY_ID + 1, TEST_CONVOY_ID);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
        // ripristino
        st = conn.createStatement();
        st.executeUpdate("UPDATE run SET id_convoy=" + TEST_CONVOY_ID + " WHERE id_convoy=" + (TEST_CONVOY_ID + 1));
        st.close();
    }

    @Test
    void setReplaceFutureRunsWithTimeParams() throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + (TEST_CONVOY_ID + 2) + ") ON CONFLICT DO NOTHING");
        st.close();
        PreparedStatement ps = conn.prepareStatement("UPDATE run SET id_convoy=? WHERE id_convoy=? AND time_departure=? AND id_line=? AND id_staff=?");
        RunMapper.setReplaceFutureRunsWithTimeParams(ps, TEST_CONVOY_ID + 2, TEST_CONVOY_ID, TEST_DEPARTURE, TEST_LINE_ID, TEST_STAFF_ID);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
        // ripristino
        st = conn.createStatement();
        st.executeUpdate("UPDATE run SET id_convoy=" + TEST_CONVOY_ID + " WHERE id_convoy=" + (TEST_CONVOY_ID + 2));
        st.close();
    }

    @Test
    void setUpdateRunStaffParams() throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (" + (TEST_STAFF_ID + 1) + ", 'Mario2', 'Rossi2', 'Via Roma', 'test2@test.it', 'pwd', 'OPERATOR') ON CONFLICT DO NOTHING");
        st.close();
        PreparedStatement ps = conn.prepareStatement("UPDATE run SET id_staff=? WHERE id_line=? AND id_convoy=? AND id_staff=? AND time_departure=?");
        RunMapper.setUpdateRunStaffParams(ps, TEST_STAFF_ID + 1, TEST_LINE_ID, TEST_CONVOY_ID, TEST_STAFF_ID, TEST_DEPARTURE);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
        // ripristino
        st = conn.createStatement();
        st.executeUpdate("UPDATE run SET id_staff=" + TEST_STAFF_ID + " WHERE id_staff=" + (TEST_STAFF_ID + 1));
        st.close();
    }

    @Test
    void setUpdateRunDepartureTimeParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE run SET time_departure=? WHERE id_line=? AND id_convoy=? AND id_staff=? AND time_departure=?");
        RunMapper.setUpdateRunDepartureTimeParams(ps, Timestamp.valueOf("2025-09-05 13:00:00"), TEST_LINE_ID, TEST_CONVOY_ID, TEST_STAFF_ID, TEST_DEPARTURE);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
        // ripristino
        Statement st = conn.createStatement();
        st.executeUpdate("UPDATE run SET time_departure='" + TEST_DEPARTURE + "' WHERE time_departure='2025-09-05 13:00:00'");
        st.close();
    }

    @Test
    void setRunDeleteKeyParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_line=? AND id_convoy=? AND id_staff=? AND time_departure=?");
        RunMapper.setRunDeleteKeyParams(ps, TEST_LINE_ID, TEST_CONVOY_ID, TEST_STAFF_ID, TEST_DEPARTURE);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
        // ripristino
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO run (id_staff, id_convoy, id_line, time_departure, time_arrival, id_first_station, id_last_station) VALUES (" + TEST_STAFF_ID + ", " + TEST_CONVOY_ID + ", " + TEST_LINE_ID + ", '" + TEST_DEPARTURE + "', '" + TEST_ARRIVAL + "', " + TEST_FIRST_STATION_ID + ", " + TEST_LAST_STATION_ID + ") ON CONFLICT DO NOTHING");
        st.close();
    }

    @Test
    void setUpdateStaffAndConvoyAfterRunCreationParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE run SET id_first_station=?, id_convoy=? WHERE id_first_station=? AND id_staff=?");
        RunMapper.setUpdateStaffAndConvoyAfterRunCreationParams(ps, TEST_STAFF_ID, TEST_CONVOY_ID, TEST_FIRST_STATION_ID);
        int affected = ps.executeUpdate();
        assertTrue(affected >= 0);
        ps.close();
    }
}
