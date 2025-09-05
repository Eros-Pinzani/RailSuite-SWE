package test.dao;

import dao.StaffDao;
import dao.PostgresConnection;
import domain.Staff;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StaffDaoTest {
    private static Connection conn;
    private StaffDao staffDao;
    private final List<Integer> staffIds = new ArrayList<>();
    private final List<Integer> stationIds = new ArrayList<>();
    private final List<Integer> lineIds = new ArrayList<>();
    private final List<Integer> convoyIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        staffDao = StaffDao.of();
        staffIds.clear();
        stationIds.clear();
        lineIds.clear();
        convoyIds.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
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
        // Elimina convoy
        if (!convoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < convoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < convoyIds.size(); i++) {
                    ps.setInt(i + 1, convoyIds.get(i));
                }
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testFindByEmail() throws Exception {
        int id = 99901;
        String email = "testemail1@test.com";
        insertStaff(id, "Mario", "Rossi", email, "OPERATOR");
        staffIds.add(id);
        Staff staff = staffDao.findByEmail(email);
        assertNotNull(staff);
        assertEquals(email, staff.getEmail());
        assertEquals("Mario", staff.getName());
        assertEquals("Rossi", staff.getSurname());
    }

    @Test
    void testFindByType() throws Exception {
        int id1 = 99902;
        int id2 = 99903;
        insertStaff(id1, "Anna", "Bianchi", "anna.bianchi@test.com", "OPERATOR");
        insertStaff(id2, "Luca", "Verdi", "luca.verdi@test.com", "SUPERVISOR");
        staffIds.add(id1);
        staffIds.add(id2);
        List<Staff> operators = staffDao.findByType(Staff.TypeOfStaff.OPERATOR);
        assertNotNull(operators);
        assertTrue(operators.stream().anyMatch(s -> s.getIdStaff() == id1));
        assertFalse(operators.stream().anyMatch(s -> s.getIdStaff() == id2));
        List<Staff> supervisors = staffDao.findByType(Staff.TypeOfStaff.SUPERVISOR);
        assertNotNull(supervisors);
        assertTrue(supervisors.stream().anyMatch(s -> s.getIdStaff() == id2));
        assertFalse(supervisors.stream().anyMatch(s -> s.getIdStaff() == id1));
    }

    @Test
    void testCheckOperatorAvailability() throws Exception {
        int id1 = 99904; // staff che cerca sostituto
        int id2 = 99905; // staff candidato
        int stationId = 99904;
        int lineId = 99904;
        Timestamp depRun1 = Timestamp.valueOf("2025-09-05 10:00:00");
        Timestamp arrRun1 = Timestamp.valueOf("2025-09-05 12:00:00");
        Timestamp depRun2 = Timestamp.valueOf("2025-09-05 08:00:00");
        Timestamp arrRun2 = Timestamp.valueOf("2025-09-05 09:30:00"); // termina almeno 15 min prima della run di id1
        insertStation(stationId);
        stationIds.add(stationId);
        insertLine(lineId);
        lineIds.add(lineId);
        insertConvoy(99904);
        insertConvoy(99905);
        convoyIds.add(99904);
        convoyIds.add(99905);
        insertStaff(id1, "Marco", "Neri", "marco.neri@test.com", "OPERATOR");
        insertStaff(id2, "Luca", "Bianchi", "luca.bianchi@test.com", "OPERATOR");
        staffIds.add(id1);
        staffIds.add(id2);
        insertStaffPool(id1, stationId, Timestamp.valueOf("2025-09-05 08:00:00"), Timestamp.valueOf("2025-09-05 18:00:00"), null, "AVAILABLE");
        insertStaffPool(id2, stationId, Timestamp.valueOf("2025-09-05 08:00:00"), Timestamp.valueOf("2025-09-05 18:00:00"), null, "AVAILABLE");
        // Crea la run di riferimento per id1
        insertRun(id1, 99904, lineId, depRun1, arrRun1, stationId, stationId);
        // Crea una run precedente per id2 che termina nella stessa stazione
        insertRun(id2, 99905, lineId, depRun2, arrRun2, stationId, stationId);
        List<Staff> available = staffDao.checkOperatorAvailability(id1, lineId, depRun1);
        System.out.println("DEBUG - Disponibili per id1: " + available);
        assertNotNull(available);
        assertTrue(available.stream().anyMatch(s -> s.getIdStaff() == id2));
        assertFalse(available.stream().anyMatch(s -> s.getIdStaff() == id1));
    }

    // --- Helper methods ---
    private void insertStaff(int id, String name, String surname, String email, String type) throws SQLException {
        String sql = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, ?, ?, 'indirizzo', ?, 'pwd', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, surname);
            ps.setString(4, email);
            ps.setString(5, type);
            ps.executeUpdate();
        }
    }

    private void insertStaffPool(int idStaff, int idStation, Timestamp shiftStart, Timestamp shiftEnd, Integer idConvoy, String status) throws SQLException {
        String sql = "INSERT INTO staff_pool (id_staff, id_station, shift_start, shift_end, id_convoy, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            ps.setInt(2, idStation);
            ps.setTimestamp(3, shiftStart);
            ps.setTimestamp(4, shiftEnd);
            if (idConvoy == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idConvoy);
            }
            ps.setString(6, status);
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

    private void insertLine(int id) throws SQLException {
        String sql = "INSERT INTO line (id_line, name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, "LINEA_TEST");
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

    private void insertConvoy(int id) throws SQLException {
        String sql = "INSERT INTO convoy (id_convoy) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
