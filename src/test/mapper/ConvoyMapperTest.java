package test.mapper;

import mapper.ConvoyMapper;
import domain.Convoy;
import domain.Carriage;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyMapperTest {
    private Connection conn;
    private final int TEST_CONVOY_ID_1 = 1001;
    private final int TEST_CONVOY_ID_2 = 1002;
    private final int TEST_CARRIAGE_ID_1 = 2001;
    private final int TEST_CARRIAGE_ID_2 = 2002;

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
        // Inserisco carriage e li collego al convoy
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (" + TEST_CARRIAGE_ID_1 + ", 'ModelA', 'TypeA', 2010, 80, " + TEST_CONVOY_ID_1 + ") ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (" + TEST_CARRIAGE_ID_2 + ", 'ModelB', 'TypeB', 2015, 100, " + TEST_CONVOY_ID_1 + ") ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM carriage WHERE id_carriage IN (" + TEST_CARRIAGE_ID_1 + ", " + TEST_CARRIAGE_ID_2 + ")");
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy IN (" + TEST_CONVOY_ID_1 + ", " + TEST_CONVOY_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM convoy WHERE id_convoy=" + TEST_CONVOY_ID_1);
        assertTrue(rs.next());
        // Recupero carriages associati
        Statement st2 = conn.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM carriage WHERE id_convoy=" + TEST_CONVOY_ID_1);
        List<Carriage> carriages = new ArrayList<>();
        while (rs2.next()) {
            carriages.add(Carriage.of(
                rs2.getInt("id_carriage"),
                rs2.getString("model"),
                rs2.getString("model_type"),
                rs2.getInt("year_produced"),
                rs2.getInt("capacity"),
                rs2.getInt("id_convoy")
            ));
        }
        rs2.close();
        st2.close();
        Convoy convoy = ConvoyMapper.toDomain(rs, carriages);
        assertEquals(TEST_CONVOY_ID_1, convoy.getId());
        assertEquals(2, convoy.getCarriages().size());
        assertTrue(convoy.getCarriages().stream().anyMatch(c -> c.getId() == TEST_CARRIAGE_ID_1));
        assertTrue(convoy.getCarriages().stream().anyMatch(c -> c.getId() == TEST_CARRIAGE_ID_2));
        rs.close();
        st.close();
    }

    @Test
    void toPreparedStatementForSelectConvoy() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM convoy WHERE id_convoy=?");
        ConvoyMapper.toPreparedStatementForSelectConvoy(stmt, TEST_CONVOY_ID_2);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CONVOY_ID_2, rs.getInt("id_convoy"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForConvoyId() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM convoy WHERE id_convoy=?");
        ConvoyMapper.toPreparedStatementForConvoyId(stmt, TEST_CONVOY_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CONVOY_ID_1, rs.getInt("id_convoy"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForRemoveCarriageFromConvoy() throws SQLException {
        Carriage carriage = Carriage.of(TEST_CARRIAGE_ID_1, "ModelA", "TypeA", 2010, 80, TEST_CONVOY_ID_1);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM carriage WHERE id_carriage=?");
        ConvoyMapper.toPreparedStatementForRemoveCarriageFromConvoy(stmt, carriage);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CARRIAGE_ID_1, rs.getInt("id_carriage"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForUpdateCarriageConvoy() throws SQLException {
        Carriage carriage = Carriage.of(TEST_CARRIAGE_ID_2, "ModelB", "TypeB", 2015, 100, TEST_CONVOY_ID_1);
        PreparedStatement stmt = conn.prepareStatement("UPDATE carriage SET id_convoy=? WHERE id_carriage=?");
        ConvoyMapper.toPreparedStatementForUpdateCarriageConvoy(stmt, TEST_CONVOY_ID_2, carriage);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected); // una riga aggiornata
        stmt.close();
        // ripristino
        PreparedStatement stmt2 = conn.prepareStatement("UPDATE carriage SET id_convoy=? WHERE id_carriage=?");
        ConvoyMapper.toPreparedStatementForUpdateCarriageConvoy(stmt2, TEST_CONVOY_ID_1, carriage);
        stmt2.executeUpdate();
        stmt2.close();
    }

    @Test
    void toDomainWithNoCarriages() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM convoy WHERE id_convoy=" + TEST_CONVOY_ID_2);
        assertTrue(rs.next());
        List<Carriage> carriages = new ArrayList<>();
        Convoy convoy = ConvoyMapper.toDomain(rs, carriages);
        assertEquals(TEST_CONVOY_ID_2, convoy.getId());
        assertEquals(0, convoy.getCarriages().size());
        rs.close();
        st.close();
    }
}
