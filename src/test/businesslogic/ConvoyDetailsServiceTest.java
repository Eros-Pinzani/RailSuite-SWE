package test.businesslogic;

import businessLogic.service.ConvoyDetailsService;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyDetailsServiceTest {
    private int testConvoyId;
    private int testCarriageId;
    private int testStationId;
    private int testLineId;
    private int testStaffId;
    private Timestamp testDepartureTime;
    private String testArrivalTime;
    private Connection conn;

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
        // Cleanup entità residue in un blocco separato (senza DELETE su station e line)
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
        // Cleanup depot
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        // NON cancellare da station e line
        // Cleanup entità residue anche per id 999999 (usato nel test di fallimento)
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM run WHERE id_convoy = ? OR id_line = ? OR id_staff = ? OR id_first_station = ? OR id_last_station = ?;")) {
            ps.setInt(1, 999999);
            ps.setInt(2, 999999);
            ps.setInt(3, 999999);
            ps.setInt(4, 999999);
            ps.setInt(5, 999999);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_convoy = ? OR id_station = ?;")) {
            ps.setInt(1, 999999);
            ps.setInt(2, 999999);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage = ?;")) {
            ps.setInt(1, 999999);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 999999);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = ?;")) {
            ps.setInt(1, 999999);
            ps.executeUpdate();
        }
        // Cleanup depot anche per 999999
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
            ps.setInt(1, 999999);
            ps.executeUpdate();
        }
        // NON cancellare da station e line
        // Inserisci depot di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO depot (id_depot) VALUES (?)")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Inserisci dati di test (fuori dal blocco try-catch!)
        testStationId = 88888;
        testLineId = 88888;
        testStaffId = 88888;
        testCarriageId = 88888;
        testDepartureTime = new Timestamp(System.currentTimeMillis());
        testArrivalTime = "23:59";
        // Stazione (head)
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testStationId);
            ps.setString(2, "JUnitTestStation");
            ps.setInt(3, 1);
            ps.setString(4, "JUnit test station");
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Linea
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO line (id_line, name) VALUES (?, ?);")) {
            ps.setInt(1, testLineId);
            ps.setString(2, "JUnitTestLine");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Staff
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO staff (id_staff, name, surname, type_of_staff) VALUES (?, ?, ?, ?);")) {
            ps.setInt(1, testStaffId);
            ps.setString(2, "JUnit");
            ps.setString(3, "Tester");
            ps.setString(4, "OPERATOR");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Carrozza
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testCarriageId);
            ps.setString(2, "JUnitTestModel");
            ps.setString(3, "TestType");
            ps.setInt(4, 2025);
            ps.setInt(5, 99);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Convoglio
        RailSuiteFacade facade = new RailSuiteFacade();
        Carriage testCarriage = Carriage.of(testCarriageId, "JUnitTestModel", "TestType", 2025, 99, null);
        testConvoyId = facade.createConvoy(List.of(testCarriage)).getId();
        // Associa il convoglio alla linea tramite la tabella run
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO run (id_convoy, id_line, id_staff, id_first_station, id_last_station, time_departure, time_arrival) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setInt(1, testConvoyId);
            ps.setInt(2, testLineId);
            ps.setInt(3, testStaffId);
            ps.setInt(4, testStationId);
            ps.setInt(5, testStationId); // Per semplicità, stessa stazione
            ps.setTimestamp(6, testDepartureTime);
            ps.setTimestamp(7, testDepartureTime); // Usa lo stesso orario per arrivo, oppure converti testArrivalTime se serve
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        try {
            // Prima cancella da run, convoy_pool, carriage_depot, carriage, convoy
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
            // Cleanup depot
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
                ps.setInt(1, testStationId);
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    void getConvoyDetailsDTO_success() {
        AssignedConvoyInfo info = new AssignedConvoyInfo(
                testConvoyId, testLineId, testStaffId, testStationId, testDepartureTime,
                "JUnitTestStation", "JUnitTestStation", testArrivalTime
        );
        ConvoyDetailsService service = new ConvoyDetailsService();
        ConvoyDetailsService.ConvoyDetailsDTO dto = service.getConvoyDetailsDTO(info);
        assertNotNull(dto);
        assertEquals("JUnitTestLine", dto.lineName);
        assertEquals("JUnit Tester", dto.staffName);
        assertEquals(testStaffId, dto.idStaff);
        assertEquals("JUnitTestStation", dto.departureStation);
        assertEquals("JUnitTestStation", dto.arrivalStation);
        assertNotNull(dto.carriages);
        assertFalse(dto.carriages.isEmpty());
        assertEquals(testCarriageId, dto.carriages.getFirst().getId());
    }

    @Test
    void getConvoyDetailsDTO_nullIfNotFound() {
        AssignedConvoyInfo info = new AssignedConvoyInfo(
                999999, 999999, 999999, 999999, testDepartureTime,
                "FakeStation", "FakeStation", "00:00"
        );
        ConvoyDetailsService service = new ConvoyDetailsService();
        ConvoyDetailsService.ConvoyDetailsDTO dto = service.getConvoyDetailsDTO(info);
        // Non deve essere null, ma tutti i campi devono essere nulli o vuoti
        assertNotNull(dto);
        assertTrue(dto.lineName == null || dto.lineName.isEmpty());
        assertTrue(dto.staffName == null || dto.staffName.isEmpty());
        // Accetta sia 0 che -1 come valore di default per idStaff
        assertEquals(-1, dto.idStaff);
        assertTrue(dto.departureStation == null || dto.departureStation.isEmpty());
        assertTrue(dto.arrivalStation == null || dto.arrivalStation.isEmpty());
        assertTrue(dto.carriages == null || dto.carriages.isEmpty());
    }

    @Test
    void getNotificationService() {
        ConvoyDetailsService service = new ConvoyDetailsService();
        assertNotNull(service.getNotificationService());
    }
}