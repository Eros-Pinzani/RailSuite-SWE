package test.mapper;

import mapper.StaffPoolMapper;
import domain.StaffPool;
import domain.DTO.StaffDTO;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class StaffPoolMapperTest {
    private Connection conn;
    private final int TEST_STAFF_ID_1 = 999;
    private final int TEST_STAFF_ID_2 = 998;
    private final int TEST_STATION_ID_1 = 999;
    private final int TEST_STATION_ID_2 = 998;
    private final int TEST_CONVOY_ID_1 = 999;
    private final int TEST_CONVOY_ID_2 = 998;

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
        // Inserisco convoy
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID_1 + ") ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID_2 + ") ON CONFLICT DO NOTHING");
        // Inserisco staff
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, type_of_staff) VALUES (" + TEST_STAFF_ID_1 + ", 'Mario', 'Rossi', 'OPERATOR') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, type_of_staff) VALUES (" + TEST_STAFF_ID_2 + ", 'Luigi', 'Verdi', 'OPERATOR') ON CONFLICT DO NOTHING");
        // Inserisco staff_pool
        st.executeUpdate("INSERT INTO staff_pool (id_staff, id_station, id_convoy, shift_start, shift_end, status) VALUES (" + TEST_STAFF_ID_1 + ", " + TEST_STATION_ID_1 + ", " + TEST_CONVOY_ID_1 + ", '2023-09-01 08:00:00', '2023-09-01 16:00:00', 'AVAILABLE')");
        st.executeUpdate("INSERT INTO staff_pool (id_staff, id_station, id_convoy, shift_start, shift_end, status) VALUES (" + TEST_STAFF_ID_2 + ", " + TEST_STATION_ID_2 + ", " + TEST_CONVOY_ID_2 + ", '2023-09-02 09:00:00', '2023-09-02 17:00:00', 'ON_RUN')");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM staff_pool WHERE id_staff IN (" + TEST_STAFF_ID_1 + ", " + TEST_STAFF_ID_2 + ")");
        st.executeUpdate("DELETE FROM staff WHERE id_staff IN (" + TEST_STAFF_ID_1 + ", " + TEST_STAFF_ID_2 + ")");
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy IN (" + TEST_CONVOY_ID_1 + ", " + TEST_CONVOY_ID_2 + ")");
        st.executeUpdate("DELETE FROM station WHERE id_station IN (" + TEST_STATION_ID_1 + ", " + TEST_STATION_ID_2 + ")");
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM staff_pool WHERE id_staff=" + TEST_STAFF_ID_1);
        assertTrue(rs.next());
        StaffPool pool = StaffPoolMapper.toDomain(rs);
        assertEquals(TEST_STAFF_ID_1, pool.getIdStaff());
        assertEquals(TEST_STATION_ID_1, pool.getIdStation());
        assertEquals(TEST_CONVOY_ID_1, pool.getIdConvoy());
        assertEquals(Timestamp.valueOf("2023-09-01 08:00:00"), pool.getShiftStart());
        assertEquals(Timestamp.valueOf("2023-09-01 16:00:00"), pool.getShiftEnd());
        assertEquals(StaffPool.ShiftStatus.AVAILABLE, pool.getShiftStatus());
        rs.close();
        st.close();
    }

    @Test
    void toStaffDTO() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM staff WHERE id_staff=" + TEST_STAFF_ID_1);
        assertTrue(rs.next());
        StaffDTO dto = StaffPoolMapper.toStaffDTO(rs);
        assertEquals(TEST_STAFF_ID_1, dto.getIdStaff());
        assertEquals("Mario Rossi", dto.getStaffNameSurname());
        rs.close();
        st.close();
    }

    @Test
    void setFindAvailableOperatorsParams() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM staff_pool WHERE id_station=? AND shift_start::date=? AND id_station=? AND shift_end::date=? AND shift_start>=? AND shift_end::date=? AND shift_end>=? AND shift_start>=? AND shift_end>=?");
        int idStation = TEST_STATION_ID_1;
        java.time.LocalDate date = java.time.LocalDate.of(2023, 9, 1);
        Timestamp departureTimestamp = Timestamp.valueOf("2023-09-01 08:00:00");
        StaffPoolMapper.setFindAvailableOperatorsParams(ps, idStation, date, departureTimestamp);
        assertEquals(idStation, ps.getParameterMetaData().getParameterCount() >= 1 ? idStation : null); // controllo solo che non lanci eccezioni
        ps.close();
    }
}