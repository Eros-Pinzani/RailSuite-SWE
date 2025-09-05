package test.dao;

import dao.StaffPoolDao;
import dao.PostgresConnection;
import domain.DTO.StaffDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StaffPoolDaoTest {
    private static Connection conn;
    private StaffPoolDao staffPoolDao;
    private final List<Integer> staffIds = new ArrayList<>();
    private final List<Integer> stationIds = new ArrayList<>();
    private final List<Integer> runConvoyIds = new ArrayList<>();
    private final List<Integer> lineIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        staffPoolDao = StaffPoolDao.of();
        staffIds.clear();
        stationIds.clear();
        runConvoyIds.clear();
        lineIds.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Elimina run
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_convoy >= 99000")) {
            ps.executeUpdate();
        }
        // Elimina staff_pool
        if (!staffIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < staffIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM staff_pool WHERE id_staff IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < staffIds.size(); i++) {
                    ps.setInt(i + 1, staffIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina staff
        if (!staffIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < staffIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM staff WHERE id_staff IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < staffIds.size(); i++) {
                    ps.setInt(i + 1, staffIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina station
        if (!stationIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < stationIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM station WHERE id_station IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < stationIds.size(); i++) {
                    ps.setInt(i + 1, stationIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina convoy
        if (!runConvoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < runConvoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < runConvoyIds.size(); i++) {
                    ps.setInt(i + 1, runConvoyIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Elimina line
        if (!lineIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < lineIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM line WHERE id_line IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < lineIds.size(); i++) {
                    ps.setInt(i + 1, lineIds.get(i));
                }
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testFindAvailableOperatorsNoRuns() throws Exception {
        int stationId = 99001;
        int staff1 = 99001, staff2 = 99002;
        insertStation(stationId);
        stationIds.add(stationId);
        insertStaff(staff1, "Mario", "Rossi");
        insertStaff(staff2, "Luca", "Bianchi");
        staffIds.add(staff1);
        staffIds.add(staff2);
        insertStaffPool(staff1, stationId);
        insertStaffPool(staff2, stationId);
        List<StaffDTO> available = staffPoolDao.findAvailableOperatorsForRun(stationId, LocalDate.of(2025, 9, 5), "10:00:00");
        assertNotNull(available);
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == staff1));
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == staff2));
    }

    @Test
    void testFindAvailableOperatorsWithRun() throws Exception {
        int stationId = 99002;
        int staff1 = 99003, staff2 = 99004;
        int convoyId = 99002;
        int lineId = 99002;
        insertStation(stationId);
        stationIds.add(stationId);
        insertStaff(staff1, "Mario", "Rossi");
        insertStaff(staff2, "Luca", "Bianchi");
        staffIds.add(staff1);
        staffIds.add(staff2);
        insertStaffPool(staff1, stationId);
        insertStaffPool(staff2, stationId);
        insertConvoy(convoyId);
        runConvoyIds.add(convoyId);
        insertLine(lineId);
        lineIds.add(lineId);
        // staff1 ha già una run in quella data
        insertRun(staff1, convoyId, lineId, Timestamp.valueOf("2025-09-05 08:00:00"), Timestamp.valueOf("2025-09-05 09:00:00"), stationId, stationId);
        List<StaffDTO> available = staffPoolDao.findAvailableOperatorsForRun(stationId, LocalDate.of(2025, 9, 5), "10:00:00");
        assertNotNull(available);
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == staff1));
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == staff2));
    }

    @Test
    void testFindAvailableOperatorsWithRunTooClose() throws Exception {
        int stationId = 99003;
        int staff1 = 99005, staff2 = 99006;
        int convoyId = 99003;
        int lineId = 99003;
        insertStation(stationId);
        stationIds.add(stationId);
        insertStaff(staff1, "Mario", "Rossi");
        insertStaff(staff2, "Luca", "Bianchi");
        staffIds.add(staff1);
        staffIds.add(staff2);
        insertStaffPool(staff1, stationId);
        insertStaffPool(staff2, stationId);
        insertConvoy(convoyId);
        runConvoyIds.add(convoyId);
        insertLine(lineId);
        lineIds.add(lineId);
        // staff1 ha una run che termina meno di 15 minuti prima
        insertRun(staff1, convoyId, lineId, Timestamp.valueOf("2025-09-05 09:50:00"), Timestamp.valueOf("2025-09-05 09:59:00"), stationId, stationId);
        List<StaffDTO> available = staffPoolDao.findAvailableOperatorsForRun(stationId, LocalDate.of(2025, 9, 5), "10:00:00");
        assertNotNull(available);
        assertFalse(available.stream().anyMatch(s -> s.getIdStaff() == staff1));
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == staff2));
    }

    // --- Helper methods ---
    private void insertStaff(int id, String name, String surname) throws SQLException {
        String sql = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, ?, ?, 'indirizzo', ?, 'pwd', 'OPERATOR')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, surname);
            ps.setString(4, name.toLowerCase() + "@test.com");
            ps.executeUpdate();
        }
    }
    private void insertStaffPool(int idStaff, int idStation) throws SQLException {
        String sql = "INSERT INTO staff_pool (id_staff, id_station, shift_start, shift_end, id_convoy, status) VALUES (?, ?, '2025-09-05 07:00:00', '2025-09-05 20:00:00', NULL, 'AVAILABLE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            ps.setInt(2, idStation);
            ps.executeUpdate();
        }
    }
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
    private void insertLine(int id) throws SQLException {
        String sql = "INSERT INTO line (id_line, name) VALUES (?, 'LINEA_TEST')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private void insertRun(int idStaff, int idConvoy, int idLine, Timestamp dep, Timestamp arr, int idFirstStation, int idLastStation) throws SQLException {
        String sql = "INSERT INTO run (id_staff, id_convoy, id_line, time_departure, time_arrival, id_first_station, id_last_station) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            ps.setInt(2, idConvoy);
            ps.setInt(3, idLine);
            ps.setTimestamp(4, dep);
            ps.setTimestamp(5, arr);
            ps.setInt(6, idFirstStation);
            ps.setInt(7, idLastStation);
            ps.executeUpdate();
        }
    }
}
