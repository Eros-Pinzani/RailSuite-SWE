package test.dao;

import dao.ConvoyDao;
import dao.PostgresConnection;
import domain.Carriage;
import domain.Convoy;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyDaoTest {
    private static Connection conn;
    private ConvoyDao convoyDao;
    private final List<Integer> convoyIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        convoyDao = ConvoyDao.of();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage >= 88888")) {
            ps1.executeUpdate();
        }
        if (!convoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < convoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                for (int i = 0; i < convoyIds.size(); i++) {
                    ps2.setInt(i + 1, convoyIds.get(i));
                }
                ps2.executeUpdate();
            }
        }
    }

    @Test
    void testCreateAndSelectConvoy() throws Exception {
        int carriageId = 88888;
        insertCarriage(carriageId, "TEST_MODEL", "PASSEGGERI", 2020, 100);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(selectCarriage(carriageId));
        Convoy convoy = convoyDao.createConvoy(carriages);
        convoyIds.add(convoy.getId());
        assertNotNull(convoy);
        Convoy found = convoyDao.selectConvoy(convoy.getId());
        assertNotNull(found);
        assertEquals(convoy.getId(), found.getId());
    }

    @Test
    void testSelectAllConvoys() throws Exception {
        int carriageId = 88889;
        insertCarriage(carriageId, "TEST_MODEL2", "MERCI", 2021, 80);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(selectCarriage(carriageId));
        Convoy convoy = convoyDao.createConvoy(carriages);
        convoyIds.add(convoy.getId());
        List<Convoy> all = convoyDao.selectAllConvoys();
        assertTrue(all.stream().anyMatch(c -> c.getId() == convoy.getId()));
    }

    @Test
    void testRemoveConvoy() throws Exception {
        int carriageId = 88890;
        insertCarriage(carriageId, "TEST_MODEL3", "MERCI", 2022, 70);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(selectCarriage(carriageId));
        Convoy convoy = convoyDao.createConvoy(carriages);
        convoyIds.add(convoy.getId());
        boolean removed = convoyDao.removeConvoy(convoy.getId());
        assertTrue(removed);
        Convoy found = convoyDao.selectConvoy(convoy.getId());
        assertNull(found);
    }

    @Test
    void testRemoveCarriageFromConvoy() throws Exception {
        int carriageId = 88891;
        insertCarriage(carriageId, "TEST_MODEL4", "MERCI", 2023, 60);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(selectCarriage(carriageId));
        Convoy convoy = convoyDao.createConvoy(carriages);
        convoyIds.add(convoy.getId());
        Carriage carriage = selectCarriage(carriageId);
        boolean removed = convoyDao.removeCarriageFromConvoy(convoy.getId(), carriage);
        assertTrue(removed);
        // Dopo la rimozione, la carrozza non deve più essere assegnata al convoglio
        Carriage updated = selectCarriage(carriageId);
        assertNotNull(updated);
        assertNull(updated.getIdConvoy());
    }

    // --- Helper methods ---
    private void insertCarriage(int id, String model, String modelType, int year, int capacity) throws SQLException {
        String sql = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, model);
            ps.setString(3, modelType);
            ps.setInt(4, year);
            ps.setInt(5, capacity);
            ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
        }
    }
    private Carriage selectCarriage(int id) throws SQLException {
        String sql = "SELECT * FROM carriage WHERE id_carriage = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Usa il costruttore/factory della tua implementazione Carriage
                return domain.Carriage.of(
                    rs.getInt("id_carriage"),
                    rs.getString("model"),
                    rs.getString("model_type"),
                    rs.getInt("year_produced"),
                    rs.getInt("capacity"),
                    rs.getObject("id_convoy") == null ? null : rs.getInt("id_convoy")
                );
            }
            return null;
        }
    }
}
