package test.businesslogic;

import businessLogic.service.CreateConvoyService;
import domain.Carriage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CreateConvoyServiceTest {
    private int testCarriageId;
    private int testStationId;
    private Integer testConvoyId; // id del convoy creato nel test
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
        // Cleanup entità residue
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ? OR id_convoy = ? OR id_staff = ?;")) {
            ps.setInt(1, 88888);
            ps.setInt(2, 88888);
            ps.setInt(3, 88888);
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
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id_station = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        } catch (Exception ignored) {}
        // Inserisci stazione di test
        testStationId = 88888;
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testStationId);
            ps.setString(2, "JUnitTestStation");
            ps.setInt(3, 1);
            ps.setString(4, "JUnit test station");
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Ora inserisci depot di test (dopo station)
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO depot (id_depot) VALUES (?)")) {
            ps.setInt(1, testStationId);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Inserisci carrozza di test
        testCarriageId = 88888;
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, testCarriageId);
            ps.setString(2, "JUnitTestModel");
            ps.setString(3, "TestType");
            ps.setInt(4, 2025);
            ps.setInt(5, 99);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Associa la carrozza al depot (carriage_depot) con status AVAILABLE
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage_depot (id_carriage, id_depot, time_entered, status_of_carriage) VALUES (?, ?, ?, ?);")) {
            ps.setInt(1, testCarriageId);
            ps.setInt(2, testStationId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "AVAILABLE");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        try {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage = ?;")) {
                ps.setInt(1, testCarriageId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
                ps.setInt(1, testCarriageId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
                ps.setInt(1, testStationId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ? OR id_convoy = ? OR id_staff = ?;")) {
                ps.setInt(1, testCarriageId);
                ps.setInt(2, testCarriageId);
                ps.setInt(3, testCarriageId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id_station = ?;")) {
                ps.setInt(1, testStationId);
                ps.executeUpdate();
            }
            // Elimina il convoglio creato dal test se presente
            if (testConvoyId != null && testConvoyId != 0) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = ?")) {
                    ps.setInt(1, testConvoyId);
                    ps.executeUpdate();
                } catch (Exception ignored) {}
            }

        } catch (Exception ignored) {}
        finally {
            conn.close();
        }
    }

    @Test
    void getAvailableDepotCarriages() {
        CreateConvoyService service = new CreateConvoyService();
        List<Carriage> carriages = service.getAvailableDepotCarriages(testStationId, "TestType");
        assertNotNull(carriages);
        assertTrue(carriages.stream().anyMatch(c -> c.getId() == testCarriageId));
    }

    @Test
    void createConvoy() {
        CreateConvoyService service = new CreateConvoyService();
        Carriage newCarriage = Carriage.of(99999, "JUnitTestModel2", "TestType2", 2026, 100, null);
        // Inserisci la carrozza nel DB
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, 99999);
            ps.setString(2, "JUnitTestModel2");
            ps.setString(3, "TestType2");
            ps.setInt(4, 2026);
            ps.setInt(5, 100);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw new RuntimeException(e); }
        assertDoesNotThrow(() -> service.createConvoy(List.of(newCarriage)));
        // Recupera l'id_convoy associato alla carrozza 99999
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_convoy FROM carriage WHERE id_carriage = ?")) {
            ps.setInt(1, 99999);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    testConvoyId = rs.getInt("id_convoy");
                }
            }
        } catch (Exception ignored) {}
        // Cleanup della carrozza (il convoglio sarà eliminato in tearDown)
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 99999);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Test
    void getAvailableDepotCarriageTypes() {
        CreateConvoyService service = new CreateConvoyService();
        List<String> types = service.getAvailableDepotCarriageTypes(testStationId);
        assertNotNull(types);
        assertTrue(types.contains("TestType"));
    }

    @Test
    void getAvailableDepotCarriageModels() {
        CreateConvoyService service = new CreateConvoyService();
        List<String> models = service.getAvailableDepotCarriageModels(testStationId, "TestType");
        assertNotNull(models);
        assertTrue(models.contains("JUnitTestModel"));
    }
}
