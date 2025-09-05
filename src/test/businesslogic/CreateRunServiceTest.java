package test.businesslogic;

import businessLogic.service.CreateRunService;
import businessLogic.RailSuiteFacade;
import domain.*;
import domain.DTO.StaffDTO;
import mapper.NotificationMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CreateRunServiceTest {
    private int testConvoyId;
    private int testCarriageId;
    private int testStationId;
    private int testLineId;
    private int testStaffId;
    private Connection conn;
    private LocalDate testDate;

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("dao/db.properties")) {
            if (is == null) throw new RuntimeException("db.properties non trovato nel classpath!");
            props.load(is);
        }
        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Cleanup entità residue
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_convoy = ? OR id_line = ? OR id_staff = ? OR id_first_station = ? OR id_last_station = ?;")) {
            ps.setInt(1, 88888);
            ps.setInt(2, 88888);
            ps.setInt(3, 88888);
            ps.setInt(4, 88888);
            ps.setInt(5, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_convoy = ? OR id_station = ?;")) {
            ps.setInt(1, 88888);
            ps.setInt(2, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        // Inserisci depot di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO depot (id_depot) VALUES (?)")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Inserisci dati di test
        testStationId = 88888;
        int testStationId2 = 88889;
        testLineId = 88888;
        testStaffId = 88888;
        testCarriageId = 88888;
        testDate = LocalDate.now().plusDays(1);
        // Inserisci/aggiorna stazione di test 1
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testStationId);
            ps.setString(2, "JUnitTestStation");
            ps.setInt(3, 1);
            ps.setString(4, "JUnit test station");
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate")) throw e;
            // Se già esiste, aggiorna i dati e forza is_head = true
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE station SET location = ?, num_bins = ?, service_description = ?, is_head = ? WHERE id_station = ?;")) {
                ps2.setString(1, "JUnitTestStation");
                ps2.setInt(2, 1);
                ps2.setString(3, "JUnit test station");
                ps2.setBoolean(4, true);
                ps2.setInt(5, testStationId);
                ps2.executeUpdate();
            }
        }
        // Inserisci/aggiorna stazione di test 2
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testStationId2);
            ps.setString(2, "JUnitTestStation2");
            ps.setInt(3, 1);
            ps.setString(4, "JUnit test station 2");
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate")) throw e;
            // Se già esiste, aggiorna i dati e forza is_head = true
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE station SET location = ?, num_bins = ?, service_description = ?, is_head = ? WHERE id_station = ?;")) {
                ps2.setString(1, "JUnitTestStation2");
                ps2.setInt(2, 1);
                ps2.setString(3, "JUnit test station 2");
                ps2.setBoolean(4, true);
                ps2.setInt(5, testStationId2);
                ps2.executeUpdate();
            }
        }
        // Associa entrambe le stazioni alla linea di test con time_to_next_station valorizzato
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO line_station (id_line, id_station, station_order, time_to_next_station) VALUES (?, ?, ?, ?::interval) ON CONFLICT (id_line, id_station) DO UPDATE SET time_to_next_station = EXCLUDED.time_to_next_station, station_order = EXCLUDED.station_order;")) {
            ps.setInt(1, testLineId);
            ps.setInt(2, testStationId);
            ps.setInt(3, 1);
            ps.setString(4, "5 minutes");
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO line_station (id_line, id_station, station_order, time_to_next_station) VALUES (?, ?, ?, ?::interval) ON CONFLICT (id_line, id_station) DO UPDATE SET time_to_next_station = EXCLUDED.time_to_next_station, station_order = EXCLUDED.station_order;")) {
            ps.setInt(1, testLineId);
            ps.setInt(2, testStationId2);
            ps.setInt(3, 2);
            ps.setString(4, "0 minutes"); // ultima stazione, nessun tempo verso la successiva
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO staff (id_staff, name, surname, type_of_staff) VALUES (?, ?, ?, ?);")) {
            ps.setInt(1, testStaffId);
            ps.setString(2, "JUnit");
            ps.setString(3, "Tester");
            ps.setString(4, "OPERATOR");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testCarriageId);
            ps.setString(2, "JUnitTestModel");
            ps.setString(3, "TestType");
            ps.setInt(4, 2025);
            ps.setInt(5, 99);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Crea convoglio di test
        RailSuiteFacade facade = new RailSuiteFacade();
        Carriage testCarriage = Carriage.of(testCarriageId, "JUnitTestModel", "TestType", 2025, 99, null);
        testConvoyId = facade.createConvoy(List.of(testCarriage)).getId();
        // Inserisci staff_pool con shift_start e shift_end come Timestamp
        LocalDate today = LocalDate.now();
        Timestamp shiftStart = Timestamp.valueOf(today.atTime(8, 0));
        Timestamp shiftEnd = Timestamp.valueOf(today.atTime(16, 0));
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO staff_pool (id_staff, id_station, shift_start, shift_end, status) VALUES (?, ?, ?, ?, ?) ON CONFLICT (id_staff) DO UPDATE SET id_station = EXCLUDED.id_station, shift_start = EXCLUDED.shift_start, shift_end = EXCLUDED.shift_end, status = EXCLUDED.status;")) {
            NotificationMapper.setDeleteNotificationParams(ps, testStaffId, testStationId, shiftStart);
            ps.setTimestamp(4, shiftEnd);
            ps.setString(5, "AVAILABLE");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        try {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_convoy = ? OR id_line = ? OR id_staff = ? OR id_first_station = ? OR id_last_station = ?;")) {
                ps.setInt(1, testConvoyId);
                ps.setInt(2, testLineId);
                ps.setInt(3, testStaffId);
                ps.setInt(4, testStationId);
                ps.setInt(5, testStationId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_convoy = ? OR id_station = ?;")) {
                ps.setInt(1, testConvoyId);
                ps.setInt(2, testStationId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage = ?;")) {
                ps.setInt(1, testCarriageId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
                ps.setInt(1, testCarriageId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = ?;")) {
                ps.setInt(1, testConvoyId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
                ps.setInt(1, testStationId);
                ps.executeUpdate();
            }
        } finally {
            // Rimuovi associazioni stazioni-linea di test
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM line_station WHERE id_line = ? AND id_station = ?;")) {
                ps.setInt(1, testLineId);
                ps.setInt(2, 88888);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM line_station WHERE id_line = ? AND id_station = ?;")) {
                ps.setInt(1, testLineId);
                ps.setInt(2, 88889);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id_station = ?;")) {
                ps.setInt(1, 88889);
                ps.executeUpdate();
            } catch (Exception ignored) {}
            conn.close();
        }
    }

    @Test
    void setConvoysPoolAvailable() throws Exception {
        CreateRunService service = new CreateRunService();
        List<Convoy> convoys = new ArrayList<>();
        Convoy convoy = new RailSuiteFacade().selectConvoy(testConvoyId);
        convoys.add(convoy);
        service.setConvoysPoolAvailable(convoys);
        assertEquals(1, service.getConvoysPoolAvailable().size());
        assertEquals(testConvoyId, service.getConvoysPoolAvailable().getFirst().getId());
    }

    @Test
    void setConvoysPoolAvailableFilteredByType() throws Exception {
        CreateRunService service = new CreateRunService();
        List<Convoy> convoys = new ArrayList<>();
        Convoy convoy = new RailSuiteFacade().selectConvoy(testConvoyId);
        convoys.add(convoy);
        service.setConvoysPoolAvailableFilteredByType(convoys);
        assertEquals(1, service.getConvoysPoolAvailableFilteredByType().size());
        assertEquals(testConvoyId, service.getConvoysPoolAvailableFilteredByType().getFirst().getId());
    }

    @Test
    void getConvoysPoolAvailable() {
        CreateRunService service = new CreateRunService();
        assertNotNull(service.getConvoysPoolAvailable());
        assertTrue(service.getConvoysPoolAvailable().isEmpty());
    }

    @Test
    void getConvoysPoolAvailableFilteredByType() {
        CreateRunService service = new CreateRunService();
        assertNotNull(service.getConvoysPoolAvailableFilteredByType());
        assertTrue(service.getConvoysPoolAvailableFilteredByType().isEmpty());
    }

    @Test
    void getAllLines() {
        CreateRunService service = new CreateRunService();
        List<Line> lines = service.getAllLines();
        assertNotNull(lines);
        boolean found = lines.stream().anyMatch(l -> l.getIdLine() == testLineId);
        assertTrue(found, "La linea di test con id " + testLineId + " non è presente tra le linee: " + lines);
    }

    @Test
    void calculateTravelTime() throws Exception {
        CreateRunService service = new CreateRunService();
        List<Line> lines = new RailSuiteFacade().findAllLines();
        Line line = lines.stream().filter(l -> l.getIdLine() == testLineId).findFirst().orElse(null);
        assertNotNull(line, "Linea di test non trovata. Linee disponibili: " + lines);
        Duration duration = service.calculateTravelTime(line);
        assertNotNull(duration);
        assertFalse(duration.isNegative());
    }

    @Test
    void setConvoyPools() {
        CreateRunService service = new CreateRunService();
        service.setConvoyPools(testStationId, "08:00", testDate, testLineId);
        List<Convoy> convoys = service.getConvoysPoolAvailable();
        assertNotNull(convoys);
    }

    @Test
    void getConvoyTypes() throws Exception {
        CreateRunService service = new CreateRunService();
        Convoy convoy = new RailSuiteFacade().selectConvoy(testConvoyId);
        List<Convoy> convoys = List.of(convoy);
        List<String> types = service.getConvoyTypes(convoys);
        assertNotNull(types);
        assertTrue(types.contains("TestType"));
    }

    @Test
    void setAvailableConvoysFilteredByType() throws Exception {
        CreateRunService service = new CreateRunService();
        Convoy convoy = new RailSuiteFacade().selectConvoy(testConvoyId);
        List<Convoy> convoys = List.of(convoy);
        service.setAvailableConvoysFilteredByType("TestType", convoys);
        List<Convoy> filtered = service.getConvoysPoolAvailableFilteredByType();
        assertNotNull(filtered);
        assertTrue(filtered.stream().anyMatch(c -> c.getId() == testConvoyId));
    }

    @Test
    void getStaffPools() {
        CreateRunService service = new CreateRunService();
        List<StaffDTO> staff = service.getStaffPools(testStationId, testDate, "08:00");
        assertNotNull(staff);
    }

    @Test
    void getDateCellFactory() {
        CreateRunService service = new CreateRunService();
        assertNotNull(service.getDateCellFactory());
    }

    @Test
    void getAvailableDepartureTimes() {
        CreateRunService service = new CreateRunService();
        List<String> times = service.getAvailableDepartureTimes(testDate);
        assertNotNull(times);
        assertTrue(times.contains("06:00"));
    }

    @Test
    void getTimeTableForRun() {
        CreateRunService service = new CreateRunService();
        List<TimeTable.StationArrAndDep> table = service.getTimeTableForRun(testLineId, testStationId, "08:00");
        assertNotNull(table);
    }

    @Test
    void setTravelTime() {
        CreateRunService service = new CreateRunService();
        service.setTravelTime(Duration.ofMinutes(10));
        assertEquals(Duration.ofMinutes(10), service.getTravelTime());
    }

    @Test
    void getTravelTime() {
        CreateRunService service = new CreateRunService();
        assertNull(service.getTravelTime());
        service.setTravelTime(Duration.ofMinutes(5));
        assertEquals(Duration.ofMinutes(5), service.getTravelTime());
    }

    @Test
    void waitForTravelTime() throws Exception {
        CreateRunService service = new CreateRunService();
        List<Line> lines = new RailSuiteFacade().findAllLines();
        Line line = lines.stream().filter(l -> l.getIdLine() == testLineId).findFirst().orElse(null);
        assertNotNull(line, "Linea di test non trovata. Linee disponibili: " + lines);
        Duration duration = service.waitForTravelTime(line);
        assertNotNull(duration);
        assertFalse(duration.isNegative());
    }

    @Test
    void createRun() throws Exception {
        CreateRunService service = new CreateRunService();
        RailSuiteFacade facade = new RailSuiteFacade();
        List<Line> lines = facade.findAllLines();
        Line line = lines.stream().filter(l -> l.getIdLine() == testLineId).findFirst().orElse(null);
        assertNotNull(line, "Linea di test non trovata. Linee disponibili: " + lines);
        Convoy convoy = facade.selectConvoy(testConvoyId);
        List<StaffDTO> staffList = facade.findAvailableOperatorsForRun(testStationId, testDate, "08:00");
        assertNotNull(staffList, "La lista degli operatori disponibili è null");
        assertFalse(staffList.isEmpty(), "Nessun operatore disponibile per la run di test");
        StaffDTO staff = staffList.getFirst();
        assertDoesNotThrow(() -> service.createRun(line, testDate, "08:00", convoy, staff));
    }
}