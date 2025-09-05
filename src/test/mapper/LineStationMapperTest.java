package test.mapper;

import mapper.LineStationMapper;
import domain.LineStation;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LineStationMapperTest {
    private Connection conn;
    private final int TEST_LINE_ID_1 = 1001;
    private final int TEST_STATION_ID_1 = 2001;
    private final int TEST_STATION_ID_2 = 2002;

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
        // Inserisco line
        st.executeUpdate("INSERT INTO line (id_line, name) VALUES (" + TEST_LINE_ID_1 + ", 'LineaTest') ON CONFLICT DO NOTHING");
        // Inserisco station
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'LocA', 1, 'DescA', true) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_2 + ", 'LocB', 2, 'DescB', false) ON CONFLICT DO NOTHING");
        // Inserisco line_station
        st.executeUpdate("INSERT INTO line_station (id_line, id_station, station_order, time_to_next_station) VALUES (" + TEST_LINE_ID_1 + ", " + TEST_STATION_ID_1 + ", 1, '01:15:00') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO line_station (id_line, id_station, station_order, time_to_next_station) VALUES (" + TEST_LINE_ID_1 + ", " + TEST_STATION_ID_2 + ", 2, NULL) ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM line_station WHERE id_line=" + TEST_LINE_ID_1);
        st.executeUpdate("DELETE FROM line WHERE id_line=" + TEST_LINE_ID_1);
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void parseDurationFromPgInterval() {
        assertEquals(Duration.ofHours(1).plusMinutes(15), LineStationMapper.parseDurationFromPgInterval("01:15:00"));
        assertEquals(Duration.ofHours(2), LineStationMapper.parseDurationFromPgInterval("02:00:00"));
        assertEquals(Duration.ofMinutes(30), LineStationMapper.parseDurationFromPgInterval("00:30:00"));
        assertNull(LineStationMapper.parseDurationFromPgInterval(null));
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM line_station WHERE id_line=" + TEST_LINE_ID_1 + " AND id_station=" + TEST_STATION_ID_1);
        assertTrue(rs.next());
        LineStation ls = LineStationMapper.toDomain(rs);
        assertEquals(TEST_STATION_ID_1, ls.getStationId());
        assertEquals(1, ls.getOrder());
        assertEquals(Duration.ofHours(1).plusMinutes(15), ls.getTimeToNextStation());
        rs.close();
        st.close();
    }

    @Test
    void toDomainHandlesNulls() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM line_station WHERE id_line=" + TEST_LINE_ID_1 + " AND id_station=" + TEST_STATION_ID_2);
        assertTrue(rs.next());
        LineStation ls = LineStationMapper.toDomain(rs);
        assertEquals(TEST_STATION_ID_2, ls.getStationId());
        assertEquals(2, ls.getOrder());
        assertNull(ls.getTimeToNextStation());
        rs.close();
        st.close();
    }

    @Test
    void setIdLine() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM line_station WHERE id_line=?");
        LineStationMapper.setIdLine(stmt, TEST_LINE_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_LINE_ID_1, rs.getInt("id_line"));
        rs.close();
        stmt.close();
    }

    @Test
    void setFindTimeTableForRunParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM line_station WHERE id_line=? AND id_station=? OR id_line=?");
        LineStationMapper.setFindTimeTableForRunParams(stmt, TEST_LINE_ID_1, TEST_STATION_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_LINE_ID_1, rs.getInt("id_line"));
        assertEquals(TEST_STATION_ID_1, rs.getInt("id_station"));
        rs.close();
        stmt.close();
    }
}