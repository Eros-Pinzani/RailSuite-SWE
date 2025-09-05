package test.mapper;

import mapper.LineMapper;
import domain.Line;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LineMapperTest {
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
        // Inserisco station
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'LocA', 1, 'DescA', true) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_2 + ", 'LocB', 2, 'DescB', false) ON CONFLICT DO NOTHING");
        // Inserisco line
        st.executeUpdate("INSERT INTO line (id_line, name) VALUES (" + TEST_LINE_ID_1 + ", 'LineaTest') ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM line WHERE id_line=" + TEST_LINE_ID_1);
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        // Simulo una query che restituisce tutti i campi richiesti da LineMapper
        String sql = "SELECT l.id_line, l.name, " + TEST_STATION_ID_1 + " AS id_first_station, 'LocA' AS first_station_location, " + TEST_STATION_ID_2 + " AS id_last_station, 'LocB' AS last_station_location FROM line l WHERE l.id_line=" + TEST_LINE_ID_1;
        ResultSet rs = st.executeQuery(sql);
        assertTrue(rs.next());
        Line line = LineMapper.toDomain(rs);
        assertEquals(TEST_LINE_ID_1, line.getIdLine());
        assertEquals("LineaTest", line.getLineName());
        assertEquals(TEST_STATION_ID_1, line.getIdFirstStation());
        assertEquals("LocA", line.getFirstStationLocation());
        assertEquals(TEST_STATION_ID_2, line.getIdLastStation());
        assertEquals("LocB", line.getLastStationLocation());
        rs.close();
        st.close();
    }

    @Test
    void toDomainHandlesNulls() throws SQLException {
        Statement st = conn.createStatement();
        // Simulo una query con location delle stazioni nulle
        String sql = "SELECT l.id_line, l.name, " + TEST_STATION_ID_1 + " AS id_first_station, NULL AS first_station_location, " + TEST_STATION_ID_2 + " AS id_last_station, NULL AS last_station_location FROM line l WHERE l.id_line=" + TEST_LINE_ID_1;
        ResultSet rs = st.executeQuery(sql);
        assertTrue(rs.next());
        Line line = LineMapper.toDomain(rs);
        assertEquals(TEST_LINE_ID_1, line.getIdLine());
        assertEquals("LineaTest", line.getLineName());
        assertEquals(TEST_STATION_ID_1, line.getIdFirstStation());
        assertNull(line.getFirstStationLocation());
        assertEquals(TEST_STATION_ID_2, line.getIdLastStation());
        assertNull(line.getLastStationLocation());
        rs.close();
        st.close();
    }
}