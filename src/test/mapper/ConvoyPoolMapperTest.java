package test.mapper;

import mapper.ConvoyPoolMapper;
import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyPoolMapperTest {
    private Connection conn;
    private final int TEST_CONVOY_ID_1 = 9001;
    private final int TEST_CONVOY_ID_2 = 9002;
    private final int TEST_STATION_ID_1 = 9101;
    private final int TEST_STATION_ID_2 = 9102;

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
        // Inserisco convoy
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID_1 + ") ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID_2 + ") ON CONFLICT DO NOTHING");
        // Inserisco station
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'LocA', 1, 'DescA', false) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_2 + ", 'LocB', 2, 'DescB', false) ON CONFLICT DO NOTHING");
        // Inserisco convoy_pool
        st.executeUpdate("INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (" + TEST_CONVOY_ID_1 + ", " + TEST_STATION_ID_1 + ", 'DEPOT') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (" + TEST_CONVOY_ID_2 + ", " + TEST_STATION_ID_2 + ", 'ON_RUN') ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM convoy_pool WHERE id_convoy IN (" + TEST_CONVOY_ID_1 + ", " + TEST_CONVOY_ID_2 + ")");
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy IN (" + TEST_CONVOY_ID_1 + ", " + TEST_CONVOY_ID_2 + ")");
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM convoy_pool WHERE id_convoy=" + TEST_CONVOY_ID_1);
        assertTrue(rs.next());
        ConvoyPool pool = ConvoyPoolMapper.toDomain(rs);
        assertEquals(TEST_CONVOY_ID_1, pool.getIdConvoy());
        assertEquals(TEST_STATION_ID_1, pool.getIdStation());
        assertEquals(ConvoyPool.ConvoyStatus.DEPOT, pool.getConvoyStatus());
        rs.close();
        st.close();
    }

    @Test
    void toConvoyTableDTO() throws SQLException {
        Statement st = conn.createStatement();
        // Simulo una query che restituisce i campi richiesti da ConvoyTableDTO
        String sql = "SELECT " + TEST_CONVOY_ID_1 + " AS id_convoy, 'ModelX' AS model, 'DEPOT' AS status, 2 AS carriage_count, 180 AS sum, 'TypeA,TypeB' AS model_types";
        ResultSet rs = st.executeQuery(sql);
        assertTrue(rs.next());
        ConvoyTableDTO dto = ConvoyPoolMapper.toConvoyTableDTO(rs);
        assertEquals(TEST_CONVOY_ID_1, dto.getIdConvoy());
        assertEquals("ModelX", dto.getModel());
        assertEquals("DEPOT", dto.getStatus());
        assertEquals(2, dto.getCarriageCount());
        assertEquals(180, dto.getCapacity());
        assertEquals("TypeA,TypeB", dto.getModelType());
        rs.close();
        st.close();
    }

    @Test
    void setIdConvoy() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM convoy_pool WHERE id_convoy=?");
        ConvoyPoolMapper.setIdConvoy(stmt, TEST_CONVOY_ID_2);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CONVOY_ID_2, rs.getInt("id_convoy"));
        rs.close();
        stmt.close();
    }

    @Test
    void setInsertConvoyPool() throws SQLException {
        // Elimino eventuale record esistente per evitare errore di chiave duplicata
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM convoy_pool WHERE id_convoy=" + TEST_CONVOY_ID_2);
        st.close();
        ConvoyPool pool = ConvoyPool.of(TEST_CONVOY_ID_2, TEST_STATION_ID_2, ConvoyPool.ConvoyStatus.ON_RUN);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?)");
        ConvoyPoolMapper.setInsertConvoyPool(stmt, pool);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected); // una riga inserita
        stmt.close();
        // pulizia
        Statement st2 = conn.createStatement();
        st2.executeUpdate("DELETE FROM convoy_pool WHERE id_convoy=" + TEST_CONVOY_ID_2 + " AND id_station=" + TEST_STATION_ID_2 + " AND status='ON_RUN'");
        st2.close();
    }

    @Test
    void setIdStation() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM convoy_pool WHERE id_station=?");
        ConvoyPoolMapper.setIdStation(stmt, TEST_STATION_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_STATION_ID_1, rs.getInt("id_station"));
        rs.close();
        stmt.close();
    }

    @Test
    void setCheckConvoyStatus() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM convoy_pool WHERE id_convoy=? OR id_convoy=?");
        ConvoyPoolMapper.setCheckConvoyStatus(stmt, TEST_CONVOY_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CONVOY_ID_1, rs.getInt("id_convoy"));
        rs.close();
        stmt.close();
    }
}