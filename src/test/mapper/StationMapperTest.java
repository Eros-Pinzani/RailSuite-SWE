package test.mapper;

import mapper.StationMapper;
import domain.Station;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class StationMapperTest {
    private Connection conn;
    private final int TEST_STATION_ID_1 = 999;
    private final int TEST_STATION_ID_2 = 998;

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
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'TestLoc1', 1, 'TestDesc1', false) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_2 + ", 'TestLoc2', 2, 'TestDesc2', true) ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM station WHERE id_station=" + TEST_STATION_ID_1);
        assertTrue(rs.next());
        Station station = StationMapper.toDomain(rs);
        assertEquals(TEST_STATION_ID_1, station.getIdStation());
        assertEquals("TestLoc1", station.getLocation());
        assertEquals(1, station.getNumBins());
        assertEquals("TestDesc1", station.getServiceDescription());
        assertFalse(station.isHead());
        rs.close();
        st.close();
    }

    @Test
    void toDomainHeadStation() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM station WHERE id_station=" + TEST_STATION_ID_2);
        assertTrue(rs.next());
        Station station = StationMapper.toDomain(rs);
        assertEquals(TEST_STATION_ID_2, station.getIdStation());
        assertEquals("TestLoc2", station.getLocation());
        assertEquals(2, station.getNumBins());
        assertEquals("TestDesc2", station.getServiceDescription());
        assertTrue(station.isHead());
        rs.close();
        st.close();
    }
}