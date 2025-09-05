package test.dao;

import dao.CarriageDao;
import dao.PostgresConnection;
import domain.Carriage;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarriageDaoTest {
    private static Connection conn;
    private CarriageDao carriageDao;

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        carriageDao = CarriageDao.of();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Cancella tutte le carrozze e convogli di test con id >= 88888
        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage >= 88888");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy >= 88888")) {
            ps1.executeUpdate();
            ps2.executeUpdate();
        }
    }

    @Test
    void testSelectCarriage_found() throws Exception {
        int id = 88888;
        insertCarriage(id, "ICR", "PASSEGGERI", 2020, 100, null);
        Carriage c = carriageDao.selectCarriage(id);
        assertNotNull(c);
        assertEquals("ICR", c.getModel());
        assertEquals(2020, c.getYearProduced());
    }

    @Test
    void testSelectCarriage_notFound() throws Exception {
        Carriage c = carriageDao.selectCarriage(99999);
        assertNull(c);
    }

    @Test
    void testSelectCarriagesByConvoyId() throws Exception {
        int convoyId = 88889;
        insertConvoy(convoyId);
        int id1 = 88890;
        int id2 = 88891;
        insertCarriage(id1, "A", "PASSEGGERI", 2010, 80, convoyId);
        insertCarriage(id2, "B", "PASSEGGERI", 2011, 90, convoyId);
        insertCarriage(88892, "C", "MERCI", 2012, 50, null);
        List<Carriage> list = carriageDao.selectCarriagesByConvoyId(convoyId);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(c -> c.getId() == id1));
        assertTrue(list.stream().anyMatch(c -> c.getId() == id2));
    }

    @Test
    void testUpdateCarriageConvoy_assignAndUnassign() throws Exception {
        int convoyId = 88893;
        insertConvoy(convoyId);
        int id = 88894;
        insertCarriage(id, "X", "MERCI", 2015, 60, null);
        boolean assigned = carriageDao.updateCarriageConvoy(id, convoyId);
        assertTrue(assigned);
        Carriage c = carriageDao.selectCarriage(id);
        assertEquals(convoyId, c.getIdConvoy());
        boolean unassigned = carriageDao.updateCarriageConvoy(id, null);
        assertTrue(unassigned);
        c = carriageDao.selectCarriage(id);
        assertNull(c.getIdConvoy());
    }

    // --- Helper methods ---
    private void insertConvoy(int idConvoy) throws SQLException {
        String sql = "INSERT INTO convoy (id_convoy) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConvoy);
            ps.executeUpdate();
        }
    }

    private void insertCarriage(int id, String model, String modelType, int year, int capacity, Integer idConvoy) throws SQLException {
        String sql = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, model);
            ps.setString(3, modelType);
            ps.setInt(4, year);
            ps.setInt(5, capacity);
            if (idConvoy == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, idConvoy);
            }
            ps.executeUpdate();
        }
    }
}
