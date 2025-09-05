package test.dao;

import dao.DepotDao;
import dao.PostgresConnection;
import domain.Depot;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepotDaoTest {
    private static Connection conn;
    private DepotDao depotDao;
    private final List<Integer> testDepotIds = new ArrayList<>();
    private final List<Integer> testStationIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        depotDao = DepotDao.of();
        testDepotIds.clear();
        testStationIds.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulisci Depot
        if (!testDepotIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testDepotIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM depot WHERE id_depot IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testDepotIds.size(); i++) {
                    ps.setInt(i + 1, testDepotIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Pulisci Station
        if (!testStationIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testStationIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM station WHERE id_station IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testStationIds.size(); i++) {
                    ps.setInt(i + 1, testStationIds.get(i));
                }
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testInsertAndGetDepot() throws Exception {
        int depotId = 88888;
        insertStation(depotId);
        depotDao.insertDepot(depotId);
        testDepotIds.add(depotId);
        testStationIds.add(depotId);
        Depot depot = depotDao.getDepot(depotId);
        assertNotNull(depot);
        assertEquals(depotId, depot.getIdDepot());
    }

    @Test
    void testGetAllDepots() throws Exception {
        int depotId = 88889;
        insertStation(depotId);
        depotDao.insertDepot(depotId);
        testDepotIds.add(depotId);
        testStationIds.add(depotId);
        List<Depot> depots = depotDao.getAllDepots();
        assertNotNull(depots);
        assertTrue(depots.stream().anyMatch(d -> d.getIdDepot() == depotId));
    }

    @Test
    void testGetDepotByStationId() throws Exception {
        int depotId = 88890;
        insertStation(depotId);
        depotDao.insertDepot(depotId);
        testDepotIds.add(depotId);
        testStationIds.add(depotId);
        Depot depot = depotDao.getDepotByStationId(depotId);
        assertNotNull(depot);
        assertEquals(depotId, depot.getIdDepot());
    }

    @Test
    void testDeleteDepot() throws Exception {
        int depotId = 88891;
        insertStation(depotId);
        depotDao.insertDepot(depotId);
        testDepotIds.add(depotId);
        testStationIds.add(depotId);
        depotDao.deleteDepot(depotId);
        Depot depot = depotDao.getDepot(depotId);
        assertNull(depot);
    }

    // --- Helper methods ---
    private void insertStation(int id) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST_DEPOT', 1, 'desc', false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

