package test.dao;

import dao.CarriageDepotDao;
import dao.PostgresConnection;
import domain.CarriageDepot;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarriageDepotDaoTest {
    private static Connection conn;
    private CarriageDepotDao carriageDepotDao;

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        carriageDepotDao = CarriageDepotDao.of();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulisci solo i dati di test (id >= 88888)
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM carriage_depot WHERE id_depot >= 88888 OR id_carriage >= 88888");
            st.executeUpdate("DELETE FROM depot WHERE id_depot >= 88888");
            st.executeUpdate("DELETE FROM carriage WHERE id_carriage >= 88888");
            st.executeUpdate("DELETE FROM convoy WHERE id_convoy >= 88888");
            st.executeUpdate("DELETE FROM station WHERE id_station >= 88888");
        }
    }

    @Test
    void testInsertAndGetCarriagesByDepot() throws Exception {
        int stationId = 88888, depotId = 88888, carriageId = 88888;
        insertStation(stationId);
        insertDepot(depotId);
        insertCarriage(carriageId, "TEST_MODEL", "PASSEGGERI", 2020, 100);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CarriageDepot cd = domain.CarriageDepot.of(depotId, carriageId, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
        carriageDepotDao.insertCarriageDepot(cd);
        List<CarriageDepot> list = carriageDepotDao.getCarriagesByDepot(depotId);
        assertEquals(1, list.size());
        assertEquals(carriageId, list.getFirst().getIdCarriage());
    }

    @Test
    void testUpdateCarriageDepotStatusAndExitTime() throws Exception {
        int stationId = 88889, depotId = 88889, carriageId = 88889;
        insertStation(stationId);
        insertDepot(depotId);
        insertCarriage(carriageId, "TEST_MODEL2", "MERCI", 2021, 80);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CarriageDepot cd = domain.CarriageDepot.of(depotId, carriageId, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
        carriageDepotDao.insertCarriageDepot(cd);
        Timestamp exit = new Timestamp(System.currentTimeMillis() + 1000);
        carriageDepotDao.updateCarriageDepotStatusAndExitTime(depotId, carriageId, "MAINTENANCE", exit);
        List<CarriageDepot> list = carriageDepotDao.getCarriagesByDepot(depotId);
        assertEquals(domain.CarriageDepot.StatusOfCarriage.MAINTENANCE, list.getFirst().getStatusOfCarriage());
        assertEquals(exit, list.getFirst().getTimeExited());
    }

    @Test
    void testFindActiveDepotByCarriage() throws Exception {
        int stationId = 88890, depotId = 88890, carriageId = 88890;
        insertStation(stationId);
        insertDepot(depotId);
        insertCarriage(carriageId, "TEST_MODEL3", "MERCI", 2022, 70);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CarriageDepot cd = domain.CarriageDepot.of(depotId, carriageId, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
        carriageDepotDao.insertCarriageDepot(cd);
        CarriageDepot found = carriageDepotDao.findActiveDepotByCarriage(carriageId);
        assertNotNull(found);
        assertEquals(depotId, found.getIdDepot());
        assertNull(found.getTimeExited());
    }

    @Test
    void testDeleteCarriageDepotByCarriageIfAvailable() throws Exception {
        int stationId = 88891, depotId = 88891, carriageId = 88891;
        insertStation(stationId);
        insertDepot(depotId);
        insertCarriage(carriageId, "TEST_MODEL4", "MERCI", 2023, 60);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CarriageDepot cd = domain.CarriageDepot.of(depotId, carriageId, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
        carriageDepotDao.insertCarriageDepot(cd);
        carriageDepotDao.deleteCarriageDepotByCarriageIfAvailable(carriageId);
        CarriageDepot found = carriageDepotDao.findActiveDepotByCarriage(carriageId);
        assertNull(found);
    }

    // --- Helper methods ---
    private void insertStation(int id) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST', 1, 'TEST', false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertDepot(int idDepot) throws SQLException {
        String sql = "INSERT INTO depot (id_depot) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDepot);
            ps.executeUpdate();
        }
    }
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
}
