package test.dao;

import dao.RunDao;
import dao.PostgresConnection;
import domain.Run;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RunDaoTest {
    private static Connection conn;
    private RunDao runDao;
    private final List<Integer> testStaffIds = new ArrayList<>();
    private final List<Integer> testConvoyIds = new ArrayList<>();
    private final List<Integer> testLineIds = new ArrayList<>();
    private final List<Integer> testStationIds = new ArrayList<>();
    private final List<Object[]> testRunKeys = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        runDao = RunDao.of();
        testStaffIds.clear();
        testConvoyIds.clear();
        testLineIds.clear();
        testStationIds.clear();
        testRunKeys.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Elimina run
        for (Object[] key : testRunKeys) {
            String sql = "DELETE FROM run WHERE id_staff = ? AND id_convoy = ? AND id_line = ? AND id_first_station = ? AND time_departure = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, (Integer) key[0]);
                ps.setInt(2, (Integer) key[1]);
                ps.setInt(3, (Integer) key[2]);
                ps.setInt(4, (Integer) key[3]);
                ps.setTimestamp(5, (Timestamp) key[4]);
                ps.executeUpdate();
            }
        }
        // Elimina staff
        if (!testStaffIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testStaffIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM staff WHERE id_staff IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testStaffIds.size(); i++) {
                    ps.setInt(i + 1, testStaffIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina convoy
        if (!testConvoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testConvoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testConvoyIds.size(); i++) {
                    ps.setInt(i + 1, testConvoyIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina line
        if (!testLineIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testLineIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM line WHERE id_line IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testLineIds.size(); i++) {
                    ps.setInt(i + 1, testLineIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina station
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
    void testInsertAndSelectRun() throws Exception {
        int staffId = 88888;
        int convoyId = 88888;
        int lineId = 88888;
        int firstStationId = 88888;
        int lastStationId = 88889;
        Timestamp dep = Timestamp.valueOf("2025-09-05 10:00:00");
        Timestamp arr = Timestamp.valueOf("2025-09-05 12:00:00");
        insertStation(firstStationId);
        insertStation(lastStationId);
        insertStaff(staffId, "Test", "Staff");
        insertConvoy(convoyId);
        insertLine(lineId, "LINEA_TEST");
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testLineIds.add(lineId);
        testStationIds.add(firstStationId);
        testStationIds.add(lastStationId);
        boolean created = runDao.createRun(lineId, convoyId, staffId, dep, arr, firstStationId, lastStationId);
        assertTrue(created);
        testRunKeys.add(new Object[]{staffId, convoyId, lineId, firstStationId, dep});
        Run run = runDao.selectRunByLineConvoyAndStaff(lineId, convoyId, dep, staffId, firstStationId);
        assertNotNull(run);
        assertEquals(staffId, run.getIdStaff());
        assertEquals(convoyId, run.getIdConvoy());
        assertEquals(lineId, run.getIdLine());
        assertEquals(firstStationId, run.getIdFirstStation());
        assertEquals(lastStationId, run.getIdLastStation());
        assertEquals(dep, run.getTimeDeparture());
        assertEquals(arr, run.getTimeArrival());
    }

    @Test
    void testDeleteRun() throws Exception {
        int staffId = 88889;
        int convoyId = 88889;
        int lineId = 88889;
        int firstStationId = 88889;
        int lastStationId = 88890;
        Timestamp dep = Timestamp.valueOf("2025-09-05 13:00:00");
        Timestamp arr = Timestamp.valueOf("2025-09-05 15:00:00");
        insertStation(firstStationId);
        insertStation(lastStationId);
        insertStaff(staffId, "Test2", "Staff2");
        insertConvoy(convoyId);
        insertLine(lineId, "LINEA_TEST2");
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testLineIds.add(lineId);
        testStationIds.add(firstStationId);
        testStationIds.add(lastStationId);
        boolean created = runDao.createRun(lineId, convoyId, staffId, dep, arr, firstStationId, lastStationId);
        assertTrue(created);
        testRunKeys.add(new Object[]{staffId, convoyId, lineId, firstStationId, dep});
        boolean deleted = runDao.deleteRun(lineId, convoyId, staffId, dep);
        assertTrue(deleted);
        Run run = runDao.selectRunByLineConvoyAndStaff(lineId, convoyId, dep, staffId, firstStationId);
        assertNull(run);
    }

    @Test
    void testSelectRunsByStaff() throws Exception {
        int staffId = 88891;
        int convoyId = 88891;
        int lineId = 88891;
        int firstStationId = 88891;
        int lastStationId = 88892;
        Timestamp dep = Timestamp.valueOf("2025-09-05 16:00:00");
        Timestamp arr = Timestamp.valueOf("2025-09-05 18:00:00");
        insertStation(firstStationId);
        insertStation(lastStationId);
        insertStaff(staffId, "Test3", "Staff3");
        insertConvoy(convoyId);
        insertLine(lineId, "LINEA_TEST3");
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testLineIds.add(lineId);
        testStationIds.add(firstStationId);
        testStationIds.add(lastStationId);
        boolean created = runDao.createRun(lineId, convoyId, staffId, dep, arr, firstStationId, lastStationId);
        assertTrue(created);
        testRunKeys.add(new Object[]{staffId, convoyId, lineId, firstStationId, dep});
        List<Run> runs = runDao.selectRunsByStaff(staffId);
        assertNotNull(runs);
        assertTrue(runs.stream().anyMatch(r -> r.getIdStaff() == staffId && r.getIdConvoy() == convoyId && r.getIdLine() == lineId));
    }

    @Test
    void testSelectRunsByConvoy() throws Exception {
        int staffId = 88892;
        int convoyId = 88892;
        int lineId = 88892;
        int firstStationId = 88892;
        int lastStationId = 88893;
        Timestamp dep = Timestamp.valueOf("2025-09-05 19:00:00");
        Timestamp arr = Timestamp.valueOf("2025-09-05 21:00:00");
        insertStation(firstStationId);
        insertStation(lastStationId);
        insertStaff(staffId, "Test4", "Staff4");
        insertConvoy(convoyId);
        insertLine(lineId, "LINEA_TEST4");
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testLineIds.add(lineId);
        testStationIds.add(firstStationId);
        testStationIds.add(lastStationId);
        boolean created = runDao.createRun(lineId, convoyId, staffId, dep, arr, firstStationId, lastStationId);
        assertTrue(created);
        testRunKeys.add(new Object[]{staffId, convoyId, lineId, firstStationId, dep});
        List<Run> runs = runDao.selectRunsByConvoy(convoyId);
        assertNotNull(runs);
        assertTrue(runs.stream().anyMatch(r -> r.getIdStaff() == staffId && r.getIdConvoy() == convoyId && r.getIdLine() == lineId));
    }

    @Test
    void testGetAllRuns() throws Exception {
        int staffId = 88893;
        int convoyId = 88893;
        int lineId = 88893;
        int firstStationId = 88893;
        int lastStationId = 88894;
        Timestamp dep = Timestamp.valueOf("2025-09-05 22:00:00");
        Timestamp arr = Timestamp.valueOf("2025-09-05 23:00:00");
        insertStation(firstStationId);
        insertStation(lastStationId);
        insertStaff(staffId, "Test5", "Staff5");
        insertConvoy(convoyId);
        insertLine(lineId, "LINEA_TEST5");
        testStaffIds.add(staffId);
        testConvoyIds.add(convoyId);
        testLineIds.add(lineId);
        testStationIds.add(firstStationId);
        testStationIds.add(lastStationId);
        boolean created = runDao.createRun(lineId, convoyId, staffId, dep, arr, firstStationId, lastStationId);
        assertTrue(created);
        testRunKeys.add(new Object[]{staffId, convoyId, lineId, firstStationId, dep});
        List<Run> runsByStaff = runDao.selectRunsByStaff(staffId);
        List<Run> runsByConvoy = runDao.selectRunsByConvoy(convoyId);
        assertNotNull(runsByStaff);
        assertNotNull(runsByConvoy);
        assertTrue(runsByStaff.stream().anyMatch(r -> r.getIdStaff() == staffId && r.getIdConvoy() == convoyId && r.getIdLine() == lineId));
        assertTrue(runsByConvoy.stream().anyMatch(r -> r.getIdStaff() == staffId && r.getIdConvoy() == convoyId && r.getIdLine() == lineId));
    }

    // --- Helper methods ---
    private void insertStation(int id) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST', 1, 'desc', false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertStaff(int id, String name, String surname) throws SQLException {
        String sql = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, ?, ?, 'addr', 'email@test.com', 'pwd', 'OPERATOR')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, surname);
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
    private void insertLine(int id, String name) throws SQLException {
        String sql = "INSERT INTO line (id_line, name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }
}
