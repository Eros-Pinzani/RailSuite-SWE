package test.dao;

import dao.ConvoyPoolDao;
import dao.PostgresConnection;
import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyPoolDaoTest {
    private static Connection conn;
    private ConvoyPoolDao convoyPoolDao;
    private final List<Integer> testConvoyIds = new ArrayList<>();
    private final List<Integer> testStationIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        convoyPoolDao = ConvoyPoolDao.of();
        testConvoyIds.clear();
        testStationIds.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulisci ConvoyPool
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_convoy >= 88888 OR id_station >= 88888")) {
            ps.executeUpdate();
        }
        // Pulisci Convoy
        if (!testConvoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testConvoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                for (int i = 0; i < testConvoyIds.size(); i++) {
                    ps2.setInt(i + 1, testConvoyIds.get(i));
                }
                ps2.executeUpdate();
            }
        }
        // Pulisci Station
        if (!testStationIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testStationIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM station WHERE id_station IN (" + inClause + ")";
            try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                for (int i = 0; i < testStationIds.size(); i++) {
                    ps2.setInt(i + 1, testStationIds.get(i));
                }
                ps2.executeUpdate();
            }
        }
    }

    @Test
    void testInsertAndGetConvoyPool() throws Exception {
        int stationId = 88888;
        int convoyId = 88888;
        insertStation(stationId);
        insertConvoy(convoyId);
        testStationIds.add(stationId);
        testConvoyIds.add(convoyId);
        ConvoyPool pool = domain.ConvoyPool.of(convoyId, stationId, ConvoyPool.ConvoyStatus.DEPOT);
        convoyPoolDao.insertConvoyPool(pool);
        ConvoyPool found = convoyPoolDao.getConvoyPoolById(convoyId);
        assertNotNull(found);
        assertEquals(convoyId, found.getIdConvoy());
        assertEquals(stationId, found.getIdStation());
        assertEquals(ConvoyPool.ConvoyStatus.DEPOT, found.getConvoyStatus());
    }

    @Test
    void testGetConvoyTableDataByStation() throws Exception {
        int stationId = 88889;
        int convoyId = 88889;
        insertStation(stationId);
        insertConvoy(convoyId);
        testStationIds.add(stationId);
        testConvoyIds.add(convoyId);
        ConvoyPool pool = domain.ConvoyPool.of(convoyId, stationId, ConvoyPool.ConvoyStatus.DEPOT);
        convoyPoolDao.insertConvoyPool(pool);
        List<ConvoyTableDTO> list = convoyPoolDao.getConvoyTableDataByStation(stationId);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(dto -> dto.getIdConvoy() == convoyId));
    }

    @Test
    void testCheckAndUpdateConvoyStatus() throws Exception {
        int stationId = 88890;
        int convoyId = 88890;
        insertStation(stationId);
        insertConvoy(convoyId);
        testStationIds.add(stationId);
        testConvoyIds.add(convoyId);
        ConvoyPool pool = domain.ConvoyPool.of(convoyId, stationId, ConvoyPool.ConvoyStatus.DEPOT);
        convoyPoolDao.insertConvoyPool(pool);
        boolean updated = convoyPoolDao.checkAndUpdateConvoyStatus(convoyId);
        assertTrue(updated);
    }

    @Test
    void testCheckConvoyAvailability() throws Exception {
        int stationId = 88891;
        int convoyId = 88891;
        insertStation(stationId);
        insertConvoy(convoyId);
        testStationIds.add(stationId);
        testConvoyIds.add(convoyId);
        ConvoyPool pool = domain.ConvoyPool.of(convoyId, stationId, ConvoyPool.ConvoyStatus.DEPOT);
        convoyPoolDao.insertConvoyPool(pool);
        List<ConvoyTableDTO> available = convoyPoolDao.checkConvoyAvailability(stationId);
        assertNotNull(available);
        assertTrue(available.stream().anyMatch(dto -> dto.getIdConvoy() == convoyId));
    }

    // --- Helper methods ---
    private void insertStation(int id) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST', 1, 'desc', false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertConvoy(int id) throws SQLException {
        String sql = "INSERT INTO convoy (id_convoy) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
