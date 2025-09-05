package test.businesslogic;

import businessLogic.service.CarriageSelectionPopupService;
import domain.Carriage;
import domain.Convoy;
import dao.ConvoyDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarriageSelectionPopupServiceTest {
    private int testConvoyId;
    private int testCarriageId;
    private int emptyConvoyId;
    private int testStationId;
    private Connection conn;

    @BeforeEach
    public void setUp() throws Exception {
        // Carica le proprietà dal file db.properties dal classpath
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("dao/db.properties")) {
            if (is == null) throw new RuntimeException("db.properties non trovato nel classpath!");
            props.load(is);
        }
        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Cleanup entità di test residue per evitare errori di chiave duplicata
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage_depot WHERE id_carriage IN (?, ?);")) {
            ps.setInt(1, 99999);
            ps.setInt(2, 99998);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_station = ?;")) {
            ps.setInt(1, 99999);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage IN (?, ?);")) {
            ps.setInt(1, 99999);
            ps.setInt(2, 99998);
            ps.executeUpdate();
        }
        // Cleanup notifiche residue che referenziano la carriage di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ?;")) {
            ps.setInt(1, 99999);
            ps.executeUpdate();
        }
        // Inserisci una stazione di test con id fisso per evitare problemi di autoincrement
        testStationId = 99999;
        String insertStation = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertStation)) {
            ps.setInt(1, testStationId);
            String TEST_LOCATION = "JUnitTestStation";
            ps.setString(2, TEST_LOCATION);
            int TEST_NUM_BINS = 1;
            ps.setInt(3, TEST_NUM_BINS);
            String TEST_SERVICE_DESCRIPTION = "JUnit test station";
            ps.setString(4, TEST_SERVICE_DESCRIPTION);
            boolean TEST_IS_HEAD = false;
            ps.setBoolean(5, TEST_IS_HEAD);
            ps.executeUpdate();
        } catch (Exception e) {
            // Ignora errore di chiave duplicata (stazione già presente)
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Inserisci un deposito di test con id fisso per rispettare il vincolo di chiave esterna
        String insertDepot = "INSERT INTO depot (id_depot) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertDepot)) {
            ps.setInt(1, testStationId);
            ps.executeUpdate();
        } catch (Exception e) {
            // Ignora errore di chiave duplicata (depot già presente)
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Inserisci una carrozza di test con id fisso
        testCarriageId = 99999;
        String insertCarriage = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?)";
        String TEST_MODEL = "JUnitTestModel";
        String TEST_MODEL_TYPE = "TestType";
        int TEST_YEAR = 2025;
        int TEST_CAPACITY = 99;
        try (PreparedStatement ps = conn.prepareStatement(insertCarriage)) {
            ps.setInt(1, testCarriageId);
            ps.setString(2, TEST_MODEL);
            ps.setString(3, TEST_MODEL_TYPE);
            ps.setInt(4, TEST_YEAR);
            ps.setInt(5, TEST_CAPACITY);
            ps.executeUpdate();
        }
        // Crea un convoglio con la carrozza assegnata
        ConvoyDao convoyDao = ConvoyDao.of();
        Carriage testCarriage = Carriage.of(testCarriageId, TEST_MODEL, TEST_MODEL_TYPE, TEST_YEAR, TEST_CAPACITY, null);
        Convoy convoy = convoyDao.createConvoy(List.of(testCarriage));
        testConvoyId = convoy.getId();
        // Collega il convoglio alla stazione tramite convoy_pool
        String insertConvoyPool = "INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?) RETURNING id_convoy";
        try (PreparedStatement ps = conn.prepareStatement(insertConvoyPool)) {
            ps.setInt(1, testConvoyId);
            ps.setInt(2, testStationId);
            ps.setString(3, "WAITING"); // Usa WAITING come status
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Errore nell'inserimento del convoy_pool di test.");
            }
        }
        // Inserisci la carrozza nella stazione tramite carriage_depot
        String insertCarriageDepot = "INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, status_of_carriage) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertCarriageDepot)) {
            ps.setInt(1, testStationId);
            ps.setInt(2, testCarriageId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "AVAILABLE");
            ps.executeUpdate();
        }
        // Crea un convoglio vuoto per il test fallimento
        Convoy emptyConvoy = convoyDao.createConvoy(List.of());
        emptyConvoyId = emptyConvoy.getId();
        // Collega il convoglio vuoto alla stazione
        try (PreparedStatement ps = conn.prepareStatement(insertConvoyPool)) {
            ps.setInt(1, emptyConvoyId);
            ps.setInt(2, testStationId);
            ps.setString(3, "WAITING"); // Usa WAITING come status
            ps.executeQuery();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        // Rimuovi la carrozza dalla carriage_depot
        String deleteCarriageDepot = "DELETE FROM carriage_depot WHERE id_carriage = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCarriageDepot)) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        // Rimuovi le notifiche di test prima di eliminare la carriage
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ?;")) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        // Rimuovi la carrozza di test
        String deleteCarriage = "DELETE FROM carriage WHERE id_carriage = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCarriage)) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        // Rimuovi i convogli di test
        ConvoyDao convoyDao = ConvoyDao.of();
        convoyDao.removeConvoy(testConvoyId);
        convoyDao.removeConvoy(emptyConvoyId);
        // Rimuovi le righe da convoy_pool
        String deleteConvoyPool = "DELETE FROM convoy_pool WHERE id_convoy = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteConvoyPool)) {
            ps.setInt(1, testConvoyId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(deleteConvoyPool)) {
            ps.setInt(1, emptyConvoyId);
            ps.executeUpdate();
        }
        // Rimuovi il deposito di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM depot WHERE id_depot = ?;")) {
            ps.setInt(1, testStationId);
            ps.executeUpdate();
        }
        // Rimuovi la stazione di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM station WHERE id_station = ?;")) {
            ps.setInt(1, testStationId);
            ps.executeUpdate();
        }
        conn.close();
    }

    @Test
    public void getCarriagesFromStation_success() {
        ConvoyDao convoyDao = ConvoyDao.of();
        Convoy convoy = null;
        try {
            convoy = convoyDao.selectConvoy(testConvoyId);
        } catch (SQLException e) {
            fail("Errore nel recupero del convoglio di test: " + e.getMessage());
        }
        CarriageSelectionPopupService service = new CarriageSelectionPopupService(convoy);
        List<Carriage> carriages = service.getCarriagesFromStation();
        assertNotNull(carriages);
        assertFalse(carriages.isEmpty());
        assertEquals(testCarriageId, carriages.getFirst().getId());
    }

    @Test
    public void getCarriagesFromStation_throwsException_whenNoCarriagesAtStation() {
        ConvoyDao convoyDao = ConvoyDao.of();
        Convoy convoy = null;
        try {
            convoy = convoyDao.selectConvoy(testConvoyId);
            // Rimuovi tutte le carrozze dalla stazione
            String deleteCarriageDepot = "DELETE FROM carriage_depot WHERE id_depot = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteCarriageDepot)) {
                ps.setInt(1, testStationId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            fail("Errore nella preparazione del test: " + e.getMessage());
        }
        CarriageSelectionPopupService service = new CarriageSelectionPopupService(convoy);
        assertThrows(RuntimeException.class, service::getCarriagesFromStation);
    }
}