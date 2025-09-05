package test;

import businessLogic.service.ManageCarriagesService;
import domain.Carriage;
import domain.DTO.CarriageDepotDTO;
import domain.Convoy;
import dao.ConvoyDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManageCarriagesServiceTest {
    private int testConvoyId;
    private int testCarriageId;
    private int testStationId;
    private Connection conn;
    private ManageCarriagesService service;

    @BeforeEach
    public void setUp() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("dao/db.properties")) {
            if (is == null) throw new RuntimeException("db.properties non trovato nel classpath!");
            props.load(is);
        }
        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Cleanup entità di test residue
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_station = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        }
        // Inserisci una stazione di test
        testStationId = 88888;
        String insertDepot = "INSERT INTO depot (id_depot) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertDepot)) {
            ps.setInt(1, testStationId);
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        String insertStation = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertStation)) {
            ps.setInt(1, testStationId);
            ps.setString(2, "JUnitTestStation");
            ps.setInt(3, 1);
            ps.setString(4, "JUnit test station");
            ps.setBoolean(5, false);
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Inserisci una carrozza di test
        testCarriageId = 88888;
        String insertCarriage = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertCarriage)) {
            ps.setInt(1, testCarriageId);
            ps.setString(2, "JUnitTestModel");
            ps.setString(3, "TestType");
            ps.setInt(4, 2025);
            ps.setInt(5, 77);
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Inserisci la carrozza nel deposito
        String insertCarriageDepot = "INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, status_of_carriage) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertCarriageDepot)) {
            ps.setInt(1, testStationId);
            ps.setInt(2, testCarriageId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "AVAILABLE");
            ps.executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Crea un convoglio con la carrozza assegnata
        ConvoyDao convoyDao = ConvoyDao.of();
        Carriage testCarriage = Carriage.of(testCarriageId, "JUnitTestModel", "TestType", 2025, 77, null);
        Convoy convoy = convoyDao.createConvoy(List.of(testCarriage));
        testConvoyId = convoy.getId();
        // Collega il convoglio alla stazione tramite convoy_pool
        String insertConvoyPool = "INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?) RETURNING id_convoy";
        try (PreparedStatement ps = conn.prepareStatement(insertConvoyPool)) {
            ps.setInt(1, testConvoyId);
            ps.setInt(2, testStationId);
            ps.setString(3, "WAITING");
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Errore nell'inserimento del convoy_pool di test.");
            }
        }
        service = new ManageCarriagesService();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        String deleteCarriageDepot = "DELETE FROM carriage_depot WHERE id_carriage = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCarriageDepot)) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        String deleteCarriage = "DELETE FROM carriage WHERE id_carriage = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCarriage)) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        ConvoyDao convoyDao = ConvoyDao.of();
        convoyDao.removeConvoy(testConvoyId);
        String deleteConvoyPool = "DELETE FROM convoy_pool WHERE id_convoy = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteConvoyPool)) {
            ps.setInt(1, testConvoyId);
            ps.executeUpdate();
        }
        conn.close();
    }

    @Test
    public void testGetCarriagesWithDepotStatusByConvoy() {
        List<CarriageDepotDTO> carriages = service.getCarriagesWithDepotStatusByConvoy(testConvoyId);
        assertNotNull(carriages);
        assertFalse(carriages.isEmpty());
        assertEquals(testCarriageId, carriages.getFirst().getIdCarriage());
    }

    @Test
    public void testGetAvailableDepotCarriages() {
        List<Carriage> carriages = service.getAvailableDepotCarriages(testStationId, "TestType");
        assertNotNull(carriages);
        boolean found = carriages.stream().anyMatch(c -> c.getId() == testCarriageId);
        assertFalse(found); // La carrozza NON deve essere disponibile in deposito dopo il setup
    }

    @Test
    public void testAddCarriageToConvoy() {
        // Prima rimuovi la carrozza dal convoglio e rimettila in deposito
        service.removeCarriageFromConvoy(testCarriageId, testConvoyId);
        // Ora aggiungi la carrozza al convoglio
        service.addCarriageToConvoy(testCarriageId, testConvoyId);
        // Verifica che la carrozza non sia più in deposito
        List<Carriage> carriages = service.getAvailableDepotCarriages(testStationId, "TestType");
        boolean found = carriages.stream().anyMatch(c -> c.getId() == testCarriageId);
        assertFalse(found);
    }

    @Test
    public void testRemoveCarriageFromConvoy() {
        // Rimuovi la carrozza dal convoglio
        service.removeCarriageFromConvoy(testCarriageId, testConvoyId);
        // Ora la carrozza deve essere disponibile in deposito
        List<Carriage> carriages = service.getAvailableDepotCarriages(testStationId, "TestType");
        boolean found = carriages.stream().anyMatch(c -> c.getId() == testCarriageId);
        assertTrue(found);
    }

    @Test
    public void testGetAvailableDepotCarriageTypes() {
        // Assicurati che la carrozza sia in deposito
        service.removeCarriageFromConvoy(testCarriageId, testConvoyId);
        List<String> types = service.getAvailableDepotCarriageTypes(testStationId);
        assertNotNull(types);
        assertTrue(types.contains("TestType"));
    }

    @Test
    public void testGetAvailableDepotCarriageModels() {
        // Assicurati che la carrozza sia in deposito
        service.removeCarriageFromConvoy(testCarriageId, testConvoyId);
        List<String> models = service.getAvailableDepotCarriageModels(testStationId, "TestType");
        assertNotNull(models);
        assertTrue(models.contains("JUnitTestModel"));
    }

    @Test
    public void testGetCarriageById() {
        Carriage carriage = service.getCarriageById(testCarriageId);
        assertNotNull(carriage);
        assertEquals(testCarriageId, carriage.getId());
    }
}
