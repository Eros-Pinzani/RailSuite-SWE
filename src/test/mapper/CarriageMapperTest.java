package test.mapper;

import mapper.CarriageMapper;
import domain.Carriage;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CarriageMapperTest {
    private Connection conn;
    private final int TEST_CARRIAGE_ID_1 = 1001;
    private final int TEST_CARRIAGE_ID_2 = 1002;
    private final int TEST_CARRIAGE_ID_3 = 1003;

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
        // Inserisco convoy necessario per la FK
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (2001) ON CONFLICT DO NOTHING");
        // Inserisco carriage con id_convoy valorizzato
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (" + TEST_CARRIAGE_ID_1 + ", 'ModelX', 'TypeX', 2011, 90, 2001) ON CONFLICT DO NOTHING");
        // Inserisco carriage con id_convoy nullo
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (" + TEST_CARRIAGE_ID_2 + ", 'ModelY', 'TypeY', 2012, 100, NULL) ON CONFLICT DO NOTHING");
        // Inserisco carriage senza colonna id_convoy (simulazione edge case)
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (" + TEST_CARRIAGE_ID_3 + ", 'ModelZ', 'TypeZ', 2013, 110) ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM carriage WHERE id_carriage IN (" + TEST_CARRIAGE_ID_1 + ", " + TEST_CARRIAGE_ID_2 + ", " + TEST_CARRIAGE_ID_3 + ")");
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy=2001");
        st.close();
        conn.close();
    }

    @Test
    void toDomainWithIdConvoy() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM carriage WHERE id_carriage=" + TEST_CARRIAGE_ID_1);
        assertTrue(rs.next());
        Carriage carriage = CarriageMapper.toDomain(rs);
        assertEquals(TEST_CARRIAGE_ID_1, carriage.getId());
        assertEquals("ModelX", carriage.getModel());
        assertEquals("TypeX", carriage.getModelType());
        assertEquals(2011, carriage.getYearProduced());
        assertEquals(90, carriage.getCapacity());
        assertEquals(2001, carriage.getIdConvoy());
        rs.close();
        st.close();
    }

    @Test
    void toDomainWithNullIdConvoy() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM carriage WHERE id_carriage=" + TEST_CARRIAGE_ID_2);
        assertTrue(rs.next());
        Carriage carriage = CarriageMapper.toDomain(rs);
        assertEquals(TEST_CARRIAGE_ID_2, carriage.getId());
        assertEquals("ModelY", carriage.getModel());
        assertEquals("TypeY", carriage.getModelType());
        assertEquals(2012, carriage.getYearProduced());
        assertEquals(100, carriage.getCapacity());
        assertNull(carriage.getIdConvoy());
        rs.close();
        st.close();
    }

    @Test
    void toDomainWithoutIdConvoyColumn() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT id_carriage, model, model_type, year_produced, capacity FROM carriage WHERE id_carriage=" + TEST_CARRIAGE_ID_3);
        assertTrue(rs.next());
        Carriage carriage = CarriageMapper.toDomain(rs);
        assertEquals(TEST_CARRIAGE_ID_3, carriage.getId());
        assertEquals("ModelZ", carriage.getModel());
        assertEquals("TypeZ", carriage.getModelType());
        assertEquals(2013, carriage.getYearProduced());
        assertEquals(110, carriage.getCapacity());
        assertNull(carriage.getIdConvoy());
        rs.close();
        st.close();
    }
}
