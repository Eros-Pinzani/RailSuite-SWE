package test.mapper;

import mapper.CarriageDepotMapper;
import domain.CarriageDepot;
import domain.DTO.CarriageDepotDTO;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CarriageDepotMapperTest {
    private Connection conn;
    private final int TEST_STATION_ID_1 = 999;
    private final int TEST_STATION_ID_2 = 998;
    private final int TEST_DEPOT_ID_1 = 999;
    private final int TEST_DEPOT_ID_2 = 998;
    private final int TEST_CARRIAGE_ID_1 = 999;
    private final int TEST_CARRIAGE_ID_2 = 998;
    private final int TEST_CARRIAGE_ID_3 = 997;

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/dao/db.properties")) {
            props.load(fis);
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        conn = DriverManager.getConnection(url, user, password);
        Statement st = conn.createStatement();
        // Inserisco station
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_1 + ", 'TestLoc1', 1, 'TestDesc1', false) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (" + TEST_STATION_ID_2 + ", 'TestLoc2', 2, 'TestDesc2', false) ON CONFLICT DO NOTHING");
        // Inserisco depot
        st.executeUpdate("INSERT INTO depot (id_depot) VALUES (" + TEST_DEPOT_ID_1 + ") ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO depot (id_depot) VALUES (" + TEST_DEPOT_ID_2 + ") ON CONFLICT DO NOTHING");
        // Inserisco carriage
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (" + TEST_CARRIAGE_ID_1 + ", 'ModelA', 'TypeA', 2010, 80) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (" + TEST_CARRIAGE_ID_2 + ", 'ModelB', 'TypeB', 2015, 100) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (" + TEST_CARRIAGE_ID_3 + ", 'ModelZ', 'TypeZ', 2020, 100) ON CONFLICT DO NOTHING");
        // Inserisco carriage_depot
        st.executeUpdate("INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, time_exited, status_of_carriage) VALUES (" + TEST_DEPOT_ID_1 + ", " + TEST_CARRIAGE_ID_1 + ", '2023-09-01 10:00:00', '2023-09-02 12:00:00', 'AVAILABLE')");
        st.executeUpdate("INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, time_exited, status_of_carriage) VALUES (" + TEST_DEPOT_ID_2 + ", " + TEST_CARRIAGE_ID_2 + ", '2023-09-03 08:00:00', NULL, 'MAINTENANCE')");
        st.executeUpdate("INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, time_exited, status_of_carriage) VALUES (" + TEST_DEPOT_ID_1 + ", " + TEST_CARRIAGE_ID_3 + ", '2023-09-04 09:00:00', '2023-09-05 10:00:00', 'AVAILABLE')");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM carriage_depot WHERE id_depot IN (" + TEST_DEPOT_ID_1 + ", " + TEST_DEPOT_ID_2 + ")");
        st.executeUpdate("DELETE FROM carriage WHERE id_carriage IN (" + TEST_CARRIAGE_ID_1 + ", " + TEST_CARRIAGE_ID_2 + ", " + TEST_CARRIAGE_ID_3 + ")");
        st.executeUpdate("DELETE FROM depot WHERE id_depot IN (" + TEST_DEPOT_ID_1 + ", " + TEST_DEPOT_ID_2 + ")");
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM carriage_depot WHERE id_depot=" + TEST_DEPOT_ID_1 + " AND id_carriage=" + TEST_CARRIAGE_ID_1);
        assertTrue(rs.next());
        CarriageDepot depot = CarriageDepotMapper.toDomain(rs);
        assertEquals(TEST_DEPOT_ID_1, depot.getIdDepot());
        assertEquals(TEST_CARRIAGE_ID_1, depot.getIdCarriage());
        assertEquals(Timestamp.valueOf("2023-09-01 10:00:00"), depot.getTimeEntered());
        assertEquals(Timestamp.valueOf("2023-09-02 12:00:00"), depot.getTimeExited());
        assertEquals(CarriageDepot.StatusOfCarriage.AVAILABLE, depot.getStatusOfCarriage());
        rs.close();
        st.close();
    }

    @Test
    void toDomainHandlesNulls() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM carriage_depot WHERE id_depot=" + TEST_DEPOT_ID_2 + " AND id_carriage=" + TEST_CARRIAGE_ID_2);
        assertTrue(rs.next());
        CarriageDepot depot = CarriageDepotMapper.toDomain(rs);
        assertEquals(TEST_DEPOT_ID_2, depot.getIdDepot());
        assertEquals(TEST_CARRIAGE_ID_2, depot.getIdCarriage());
        assertEquals(Timestamp.valueOf("2023-09-03 08:00:00"), depot.getTimeEntered());
        assertNull(depot.getTimeExited()); // solo time_exited può essere null
        assertEquals(CarriageDepot.StatusOfCarriage.MAINTENANCE, depot.getStatusOfCarriage());
        rs.close();
        st.close();
    }

    @Test
    void toPreparedStatement() throws SQLException {
        CarriageDepot depot = CarriageDepot.of(TEST_DEPOT_ID_1, TEST_CARRIAGE_ID_1, Timestamp.valueOf("2023-09-01 10:00:00"), Timestamp.valueOf("2023-09-02 12:00:00"), CarriageDepot.StatusOfCarriage.AVAILABLE);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO carriage_depot (id_depot, id_carriage, time_entered, time_exited, status_of_carriage) VALUES (?, ?, ?, ?, ?)");
        CarriageDepotMapper.toPreparedStatement(stmt, depot);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected); // una riga inserita
        stmt.close();
        // pulizia
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM carriage_depot WHERE id_depot=" + TEST_DEPOT_ID_1 + " AND id_carriage=" + TEST_CARRIAGE_ID_1 + " AND time_entered='2023-09-01 10:00:00'");
        st.close();
    }

    @Test
    void toPreparedStatementForUpdateStatusAndExitTime() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE carriage_depot SET status_of_carriage=?, time_exited=? WHERE id_depot=? AND id_carriage=?");
        CarriageDepotMapper.toPreparedStatementForUpdateStatusAndExitTime(stmt, "CLEANING", Timestamp.valueOf("2023-09-06 11:00:00"), TEST_DEPOT_ID_1, TEST_CARRIAGE_ID_1);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected); // una riga aggiornata
        stmt.close();
        // ripristino
        Statement st = conn.createStatement();
        st.executeUpdate("UPDATE carriage_depot SET status_of_carriage='AVAILABLE', time_exited='2023-09-02 12:00:00' WHERE id_depot=" + TEST_DEPOT_ID_1 + " AND id_carriage=" + TEST_CARRIAGE_ID_1);
        st.close();
    }

    @Test
    void toPreparedStatementForFindAvailableCarriagesForConvoy() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM carriage_depot WHERE id_depot=? OR id_depot=? AND status_of_carriage=?");
        CarriageDepotMapper.toPreparedStatementForFindAvailableCarriagesForConvoy(stmt, TEST_DEPOT_ID_1, "AVAILABLE");
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_DEPOT_ID_1, rs.getInt("id_depot"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForFindAvailableCarriageTypesForConvoy() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM carriage_depot WHERE id_depot=? OR id_depot=?");
        CarriageDepotMapper.toPreparedStatementForFindAvailableCarriageTypesForConvoy(stmt, TEST_DEPOT_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_DEPOT_ID_1, rs.getInt("id_depot"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForFindAvailableCarriageModelsForConvoy() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM carriage_depot WHERE id_depot=? AND status_of_carriage=?");
        CarriageDepotMapper.toPreparedStatementForFindAvailableCarriageModelsForConvoy(stmt, TEST_DEPOT_ID_1, "AVAILABLE");
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_DEPOT_ID_1, rs.getInt("id_depot"));
        rs.close();
        stmt.close();
    }

    @Test
    void toPreparedStatementForGetCarriagesByConvoyPosition() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM carriage_depot WHERE id_depot=? OR id_depot=?");
        CarriageDepotMapper.toPreparedStatementForGetCarriagesByConvoyPosition(stmt, TEST_DEPOT_ID_1);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_DEPOT_ID_1, rs.getInt("id_depot"));
        rs.close();
        stmt.close();
    }

    @Test
    void toCarriageDepotDTO() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT c.id_carriage, c.model, c.year_produced, c.capacity, cd.status_of_carriage, cd.time_exited FROM carriage_depot cd JOIN carriage c ON cd.id_carriage = c.id_carriage WHERE cd.id_depot=" + TEST_DEPOT_ID_1 + " AND cd.id_carriage=" + TEST_CARRIAGE_ID_3);
        assertTrue(rs.next());
        CarriageDepotDTO dto = CarriageDepotMapper.toCarriageDepotDTO(rs);
        assertEquals(TEST_CARRIAGE_ID_3, dto.getIdCarriage());
        assertEquals("ModelZ", dto.getModel());
        assertEquals(2020, dto.getYearProduced());
        assertEquals(100, dto.getCapacity());
        assertEquals("AVAILABLE", dto.getDepotStatus());
        assertEquals(Timestamp.valueOf("2023-09-05 10:00:00"), dto.getTimeExited());
        rs.close();
        st.close();
    }
}
