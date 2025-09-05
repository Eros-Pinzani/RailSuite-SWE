package test.mapper;

import mapper.DepotMapper;
import domain.Depot;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DepotMapperTest {
    private Connection conn;
    private final int TEST_STATION_ID_1 = 888;
    private final int TEST_DEPOT_ID_1 = 888;

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
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'DepotLoc', 1, 'DepotDesc', true) ON CONFLICT DO NOTHING");
        // Inserisco depot
        st.executeUpdate("INSERT INTO depot (id_depot) VALUES (" + TEST_DEPOT_ID_1 + ") ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM depot WHERE id_depot=" + TEST_DEPOT_ID_1);
        st.executeUpdate("DELETE FROM station WHERE id_station=" + TEST_STATION_ID_1);
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM depot WHERE id_depot=" + TEST_DEPOT_ID_1);
        assertTrue(rs.next());
        Depot depot = DepotMapper.toDomain(rs);
        assertEquals(TEST_DEPOT_ID_1, depot.getIdDepot());
        rs.close();
        st.close();
    }

    @Test
    void setIdDepot() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM depot WHERE id_depot=?");
        DepotMapper.setIdDepot(stmt, TEST_DEPOT_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_DEPOT_ID_1, rs.getInt("id_depot"));
        rs.close();
        stmt.close();
    }
}