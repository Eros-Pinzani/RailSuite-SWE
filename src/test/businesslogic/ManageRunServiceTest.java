package test.businesslogic;

import businessLogic.service.ManageRunService;
import domain.*;
import org.junit.jupiter.api.*;
import java.sql.Timestamp;
import java.util.*;
import dao.PostgresConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class ManageRunServiceTest {
    private ManageRunService service;
    private static Connection conn;
    private final List<Integer> testRunIds = new ArrayList<>();
    private final List<Integer> testStaffIds = new ArrayList<>();
    private final List<Integer> testConvoyIds = new ArrayList<>();
    private final List<Integer> testLineIds = new ArrayList<>();
    private final List<Integer> testStationIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setUp() throws Exception {
        service = new ManageRunService();
        // Inserisci dati minimi per un run
        int stationId = 88888;
        int lineId = 88888;
        int staffId = 88888;
        int convoyId = 88888;
        int runId = 88888; // non esiste come PK, ma serve per tracking
        insertStation(stationId);
        insertLine(lineId);
        insertStaff(staffId);
        insertConvoy(convoyId);
        insertRun(staffId, convoyId, lineId, stationId);
        testStationIds.add(stationId);
        testLineIds.add(lineId);
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testRunIds.add(runId);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Elimina run
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_staff = 88888 AND id_convoy = 88888 AND id_line = 88888 AND id_first_station = 88888")) {
            ps.executeUpdate();
        }
        // Elimina staff
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE id_staff = 88888")) {
            ps.executeUpdate();
        }
        // Elimina convoy
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = 88888")) {
            ps.executeUpdate();
        }
        // Elimina line
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM line WHERE id_line = 88888")) {
            ps.executeUpdate();
        }
        // Elimina station
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id_station = 88888")) {
            ps.executeUpdate();
        }
    }

    // --- Helper methods ---
    private void insertStation(int id) throws Exception {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST', 1, 'desc', true) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertLine(int id) throws Exception {
        String sql = "INSERT INTO line (id_line, name) VALUES (?, 'TEST_LINE') ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertStaff(int id) throws Exception {
        String sql = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, 'Test', 'Test', 'Test', 'test@test.com', 'pwd', 'OPERATOR') ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertConvoy(int id) throws Exception {
        String sql = "INSERT INTO convoy (id_convoy) VALUES (?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertRun(int staffId, int convoyId, int lineId, int stationId) throws Exception {
        String sql = "INSERT INTO run (id_staff, id_convoy, id_line, time_departure, time_arrival, id_first_station, id_last_station) VALUES (?, ?, ?, now(), now(), ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, convoyId);
            ps.setInt(3, lineId);
            ps.setInt(4, stationId);
            ps.setInt(5, stationId);
            ps.executeUpdate();
        }
    }

    @Test
    void testGetAllRun() {
        List<Run> runs = service.getAllRun();
        assertNotNull(runs);
    }

    @Test
    void testFilterRunRaws() {
        List<Run> allRuns = service.getAllRun();
        if (!allRuns.isEmpty()) {
            Run sample = allRuns.getFirst();
            List<Run> filtered = service.filterRunRaws(sample.getLineName(), String.valueOf(sample.getIdConvoy()), sample.getStaffNameSurname(), sample.getFirstStationName(), null);
            assertTrue(filtered.stream().anyMatch(r -> runsSuperKeyEquals(r, sample)));
        } else {
            List<Run> filtered = service.filterRunRaws("fake", "fake", "fake", "fake", null);
            assertTrue(filtered.isEmpty());
        }
    }

    /**
     * Confronta due Run usando la superchiave del DB: id_staff, id_convoy, id_line, id_first_station, time_departure
     */
    private boolean runsSuperKeyEquals(Run a, Run b) {
        return Objects.equals(a.getIdStaff(), b.getIdStaff()) &&
               Objects.equals(a.getIdConvoy(), b.getIdConvoy()) &&
               Objects.equals(a.getIdLine(), b.getIdLine()) &&
               Objects.equals(a.getIdFirstStation(), b.getIdFirstStation()) &&
               Objects.equals(a.getTimeDeparture(), b.getTimeDeparture());
    }

    @Test
    void testSearchRunsByDay() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp future = new Timestamp(System.currentTimeMillis() + 100000);
        List<Run> runs = service.searchRunsByDay(null, null, null, null, now, future);
        assertNotNull(runs);
    }

    @Test
    void testUpdateCarriageDepotStatuses() {
        // Il metodo non lancia eccezioni e aggiorna lo stato delle carrozze in cleaning/maintenance
        assertDoesNotThrow(() -> service.updateCarriageDepotStatuses());
    }

    @Test
    void testCompleteRun() {
        // Questo test richiede un Run valido con notifiche approvate associate
        List<Run> runs = service.getAllRun();
        if (!runs.isEmpty()) {
            Run run = runs.getFirst();
            assertDoesNotThrow(() -> service.completeRun(run));
        }
    }
}
