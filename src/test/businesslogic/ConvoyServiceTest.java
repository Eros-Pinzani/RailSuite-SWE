package test.businesslogic;

import businessLogic.service.ConvoyService;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.Convoy;
import domain.DTO.ConvoyTableDTO;
import domain.DTO.CarriageDepotDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyServiceTest {
    private int testConvoyId;
    private int testCarriageId;
    private int testStationId;
    private int testLineId;
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
        // Cleanup entità residue SOLO su tabelle sicure (NO run, NO tabelle che attivano trigger)
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
        // Inserisci depot di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO depot (id_depot) VALUES (?)")) {
            ps.setInt(1, 88888);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Inserisci dati di test
        testStationId = 88888;
        testLineId = 88888;
        int testStaffId = 88888;
        testCarriageId = 88888;
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
        // (NON inserire in run per evitare trigger)
        // Inserisci relazione in line_station
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO line_station (id_line, id_station, station_order) VALUES (?, ?, ?);")) {
            ps.setInt(1, testLineId);
            ps.setInt(2, testStationId);
            ps.setInt(3, 1);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
        // Inserisci relazione in convoy_pool
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO convoy_pool (id_convoy, id_station, status) VALUES (?, ?, ?);")) {
            ps.setInt(1, testConvoyId);
            ps.setInt(2, testStationId);
            ps.setString(3, "WAITING");
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw e; }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        try {
            // Cleanup SOLO su tabelle sicure (NO run, NO tabelle che attivano trigger)
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
            // Cleanup line_station
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM line_station WHERE id_line = ? AND id_station = ?;")) {
                ps.setInt(1, testLineId);
                ps.setInt(2, testStationId);
                ps.executeUpdate();
            }
            // Cleanup convoy_pool
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy_pool WHERE id_convoy = ? AND id_station = ?;")) {
                ps.setInt(1, testConvoyId);
                ps.setInt(2, testStationId);
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    void getAllConvoys() {
        ConvoyService service = new ConvoyService();
        List<Convoy> convoys = service.getAllConvoys();
        assertNotNull(convoys);
        assertTrue(convoys.stream().anyMatch(c -> c.getId() == testConvoyId));
    }

    @Test
    void createConvoy() {
        ConvoyService service = new ConvoyService();
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
        service.createConvoy(List.of(newCarriage));
        List<Convoy> convoys = service.getAllConvoys();
        assertTrue(convoys.stream().anyMatch(c -> c.getCarriages().stream().anyMatch(car -> car.getId() == 99999)));
        // Cleanup
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 99999);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Test
    void getConvoyTableByStation() {
        ConvoyService service = new ConvoyService();
        List<ConvoyTableDTO> table = service.getConvoyTableByStation(testStationId);
        assertNotNull(table);
        assertTrue(table.stream().anyMatch(dto -> dto.getIdConvoy() == testConvoyId));
    }

    @Test
    void getAvailableDepotCarriages() {
        ConvoyService service = new ConvoyService();
        List<Carriage> carriages = service.getAvailableDepotCarriages(testStationId, null);
        assertNotNull(carriages);
        // Potrebbero essere vuote se tutte assegnate, ma la chiamata non deve fallire
    }

    @Test
    void getAvailableDepotCarriageTypes() {
        ConvoyService service = new ConvoyService();
        List<String> types = service.getAvailableDepotCarriageTypes(testStationId);
        assertNotNull(types);
    }

    @Test
    void deleteConvoy() throws Exception {
        ConvoyService service = new ConvoyService();
        // Crea un nuovo convoglio da eliminare
        Carriage newCarriage = Carriage.of(99998, "JUnitTestModel3", "TestType3", 2027, 101, null);
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, 99998);
            ps.setString(2, "JUnitTestModel3");
            ps.setString(3, "TestType3");
            ps.setInt(4, 2027);
            ps.setInt(5, 101);
            ps.executeUpdate();
        } catch (Exception e) { if (!e.getMessage().contains("duplicate")) throw new RuntimeException(e); }
        RailSuiteFacade facade = new RailSuiteFacade();
        int convoyId = facade.createConvoy(List.of(newCarriage)).getId();
        service.deleteConvoy(convoyId, testStationId);
        List<Convoy> convoys = service.getAllConvoys();
        assertTrue(convoys.stream().noneMatch(c -> c.getId() == convoyId));
        // Cleanup
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?;")) {
            ps.setInt(1, 99998);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Test
    void updateDepotCarriageAvailability() {
        ConvoyService service = new ConvoyService();
        // Non lancia eccezioni
        assertDoesNotThrow(() -> service.updateDepotCarriageAvailability(testStationId));
    }

    @Test
    void getCarriagesWithDepotStatusByConvoy() {
        ConvoyService service = new ConvoyService();
        List<CarriageDepotDTO> dtos = service.getCarriagesWithDepotStatusByConvoy(testConvoyId);
        assertNotNull(dtos);
        assertTrue(dtos.stream().anyMatch(dto -> dto.getIdCarriage() == testCarriageId));
    }

    @Test
    void getAllHeadStations() {
        ConvoyService service = new ConvoyService();
        var stations = service.getAllHeadStations();
        assertNotNull(stations);
        assertTrue(stations.stream().anyMatch(s -> s.getIdStation() == testStationId));
    }
}