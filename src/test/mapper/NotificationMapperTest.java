package test.mapper;

import mapper.NotificationMapper;
import domain.Notification;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {
    private Connection conn;
    private final int TEST_CARRIAGE_ID = 1001;
    private final int TEST_CONVOY_ID = 2001;
    private final int TEST_STAFF_ID = 3001;
    private final String TEST_MODEL = "ModelTest";
    private final String TEST_WORK_TYPE = "MAINTENANCE";
    private final String TEST_NAME = "Mario";
    private final String TEST_SURNAME = "Rossi";
    private final String TEST_STATUS = "INVIATA";
    private final Timestamp TEST_NOTIFY_TIME = Timestamp.valueOf("2025-09-05 10:00:00");

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
        // Inserisco dati necessari
        st.executeUpdate("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (" + TEST_CARRIAGE_ID + ", '" + TEST_MODEL + "', 'TypeA', 2020, 100) ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO convoy (id_convoy) VALUES (" + TEST_CONVOY_ID + ") ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (" + TEST_STAFF_ID + ", '" + TEST_NAME + "', '" + TEST_SURNAME + "', 'Via Roma', 'test@test.it', 'pwd', 'OPERATOR') ON CONFLICT DO NOTHING");
        st.executeUpdate("INSERT INTO notification (id_carriage, id_convoy, notify_time, work_type, id_staff, status) VALUES (" + TEST_CARRIAGE_ID + ", " + TEST_CONVOY_ID + ", '" + TEST_NOTIFY_TIME + "', '" + TEST_WORK_TYPE + "', " + TEST_STAFF_ID + ", '" + TEST_STATUS + "') ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM notification WHERE id_carriage=" + TEST_CARRIAGE_ID + " AND id_convoy=" + TEST_CONVOY_ID);
        st.executeUpdate("DELETE FROM carriage WHERE id_carriage=" + TEST_CARRIAGE_ID);
        st.executeUpdate("DELETE FROM convoy WHERE id_convoy=" + TEST_CONVOY_ID);
        st.executeUpdate("DELETE FROM staff WHERE id_staff=" + TEST_STAFF_ID);
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT n.id_carriage, c.model, n.id_convoy, n.work_type, n.notify_time, n.id_staff, s.name, s.surname, n.status FROM notification n JOIN carriage c ON n.id_carriage = c.id_carriage JOIN staff s ON n.id_staff = s.id_staff WHERE n.id_carriage=" + TEST_CARRIAGE_ID + " AND n.id_convoy=" + TEST_CONVOY_ID);
        assertTrue(rs.next());
        Notification notif = NotificationMapper.toDomain(rs);
        assertEquals(TEST_CARRIAGE_ID, notif.getIdCarriage());
        assertEquals(TEST_MODEL, notif.getTypeOfCarriage());
        assertEquals(TEST_CONVOY_ID, notif.getIdConvoy());
        assertEquals(TEST_WORK_TYPE, notif.getTypeOfNotification());
        assertEquals(TEST_NOTIFY_TIME, notif.getDateTimeOfNotification());
        assertEquals(TEST_STAFF_ID, notif.getIdStaff());
        assertEquals(TEST_NAME, notif.getStaffName());
        assertEquals(TEST_SURNAME, notif.getStaffSurname());
        assertEquals(TEST_STATUS, notif.getStatus());
        rs.close();
        st.close();
    }

    @Test
    void setAddNotificationParams() throws SQLException {
        // Pulizia preventiva per evitare chiave duplicata
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM notification WHERE id_carriage=" + TEST_CARRIAGE_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND notify_time='" + TEST_NOTIFY_TIME + "'");
        st.close();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO notification (id_carriage, id_convoy, notify_time, work_type, id_staff, status) VALUES (?, ?, ?, ?, ?, ?)");
        NotificationMapper.setAddNotificationParams(stmt, TEST_CARRIAGE_ID, TEST_CONVOY_ID, TEST_NOTIFY_TIME, TEST_WORK_TYPE, TEST_STAFF_ID);
        stmt.setString(6, TEST_STATUS); // aggiungo lo status manualmente
        int affected = stmt.executeUpdate();
        assertEquals(1, affected);
        stmt.close();
        // pulizia
        st = conn.createStatement();
        st.executeUpdate("DELETE FROM notification WHERE id_carriage=" + TEST_CARRIAGE_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND notify_time='" + TEST_NOTIFY_TIME + "'");
        st.close();
    }

    @Test
    void setDeleteNotificationParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM notification WHERE id_carriage=? AND id_convoy=? AND notify_time=?");
        NotificationMapper.setDeleteNotificationParams(stmt, TEST_CARRIAGE_ID, TEST_CONVOY_ID, TEST_NOTIFY_TIME);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected);
        stmt.close();
        // ripristino
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO notification (id_carriage, id_convoy, notify_time, work_type, id_staff, status) VALUES (" + TEST_CARRIAGE_ID + ", " + TEST_CONVOY_ID + ", '" + TEST_NOTIFY_TIME + "', '" + TEST_WORK_TYPE + "', " + TEST_STAFF_ID + ", '" + TEST_STATUS + "') ON CONFLICT DO NOTHING");
        st.close();
    }

    @Test
    void setInsertNotificationHistoryParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO notification_history (id_carriage, id_convoy, notify_time, work_type, id_staff, staff_name, staff_surname, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        NotificationMapper.setInsertNotificationHistoryParams(stmt, TEST_CARRIAGE_ID, TEST_CONVOY_ID, TEST_NOTIFY_TIME, TEST_WORK_TYPE, TEST_STAFF_ID, TEST_NAME, TEST_SURNAME, TEST_STATUS);
        int affected = stmt.executeUpdate();
        assertEquals(1, affected);
        stmt.close();
        // pulizia
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM notification_history WHERE id_carriage=" + TEST_CARRIAGE_ID + " AND id_convoy=" + TEST_CONVOY_ID + " AND notify_time='" + TEST_NOTIFY_TIME + "'");
        st.close();
    }

    @Test
    void setExistsNotificationOrHistoryParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM notification WHERE id_carriage=? AND id_staff=? AND work_type=? UNION SELECT 1 FROM notification_history WHERE id_carriage=? AND id_staff=? AND work_type=?");
        NotificationMapper.setExistsNotificationOrHistoryParams(stmt, TEST_CARRIAGE_ID, TEST_STAFF_ID, TEST_WORK_TYPE);
        ResultSet rs = stmt.executeQuery();
        assertNotNull(rs);
        rs.close();
        stmt.close();
    }

    @Test
    void setNotificationsByConvoyIdParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notification WHERE id_convoy=?");
        NotificationMapper.setNotificationsByConvoyIdParams(stmt, TEST_CONVOY_ID);
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_CONVOY_ID, rs.getInt("id_convoy"));
        rs.close();
        stmt.close();
    }

    @Test
    void setAllNotificationsForConvoyAndStaffParams() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT id_carriage, id_convoy, notify_time, work_type, id_staff, status FROM notification WHERE id_convoy=? AND id_staff=? " +
            "UNION SELECT id_carriage, id_convoy, notify_time, work_type, id_staff, status FROM notification_history WHERE id_convoy=? AND id_staff=?"
        );
        NotificationMapper.setAllNotificationsForConvoyAndStaffParams(stmt, TEST_CONVOY_ID, TEST_STAFF_ID);
        ResultSet rs = stmt.executeQuery();
        assertNotNull(rs);
        rs.close();
        stmt.close();
    }
}
