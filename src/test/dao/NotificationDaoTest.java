package test.dao;

import dao.NotificationDao;
import dao.PostgresConnection;
import domain.Notification;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationDaoTest {
    private static Connection conn;
    private NotificationDao notificationDao;
    private final List<Integer> testCarriageIds = new ArrayList<>();
    private final List<Integer> testConvoyIds = new ArrayList<>();
    private final List<Integer> testStaffIds = new ArrayList<>();
    private final List<NotificationKey> testNotificationKeys = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        notificationDao = NotificationDao.of();
        testCarriageIds.clear();
        testConvoyIds.clear();
        testStaffIds.clear();
        testNotificationKeys.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulisci notification
        for (NotificationKey key : testNotificationKeys) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ? AND id_convoy = ? AND notify_time = ?")) {
                ps.setInt(1, key.idCarriage);
                ps.setInt(2, key.idConvoy);
                ps.setTimestamp(3, key.notifyTime);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification_history WHERE id_carriage = ? AND id_convoy = ? AND notify_time = ?")) {
                ps.setInt(1, key.idCarriage);
                ps.setInt(2, key.idConvoy);
                ps.setTimestamp(3, key.notifyTime);
                ps.executeUpdate();
            }
        }
        // Pulisci carriage
        if (!testCarriageIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testCarriageIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM carriage WHERE id_carriage IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testCarriageIds.size(); i++) {
                    ps.setInt(i + 1, testCarriageIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Pulisci convoy
        if (!testConvoyIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testConvoyIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM convoy WHERE id_convoy IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testConvoyIds.size(); i++) {
                    ps.setInt(i + 1, testConvoyIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Pulisci staff
        if (!testStaffIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testStaffIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM staff WHERE id_staff IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testStaffIds.size(); i++) {
                    ps.setInt(i + 1, testStaffIds.get(i));
                }
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testAddAndGetAndDeleteNotification() throws Exception {
        int carriageId = 88888;
        int convoyId = 88888;
        int staffId = 88888;
        Timestamp notifyTime = new Timestamp(System.currentTimeMillis());
        String workType = "CLEANING";
        insertCarriage(carriageId);
        insertConvoy(convoyId);
        insertStaff(staffId);
        testCarriageIds.add(carriageId);
        testConvoyIds.add(convoyId);
        testStaffIds.add(staffId);
        notificationDao.addNotification(carriageId, convoyId, notifyTime, workType, staffId);
        testNotificationKeys.add(new NotificationKey(carriageId, convoyId, notifyTime));
        List<Notification> all = notificationDao.getAllNotifications();
        assertTrue(all.stream().anyMatch(n -> n.getIdCarriage() == carriageId && n.getIdConvoy() == convoyId && n.getDateTimeOfNotification().equals(notifyTime)));
        List<Notification> byConvoy = notificationDao.getNotificationsByConvoyId(convoyId);
        assertTrue(byConvoy.stream().anyMatch(n -> n.getIdCarriage() == carriageId && n.getDateTimeOfNotification().equals(notifyTime)));
        notificationDao.deleteNotification(carriageId, convoyId, notifyTime);
        List<Notification> afterDelete = notificationDao.getAllNotifications();
        assertFalse(afterDelete.stream().anyMatch(n -> n.getIdCarriage() == carriageId && n.getIdConvoy() == convoyId && n.getDateTimeOfNotification().equals(notifyTime)));
    }

    // --- Helper methods ---
    private void insertCarriage(int id) throws SQLException {
        String sql = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (?, 'TEST', 'TEST', 2020, 10, NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
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
    private void insertStaff(int id) throws SQLException {
        String sql = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, 'Test', 'Test', 'Test', 'test@test.com', 'pwd', 'OPERATOR')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private static class NotificationKey {
        int idCarriage, idConvoy;
        Timestamp notifyTime;
        NotificationKey(int idCarriage, int idConvoy, Timestamp notifyTime) {
            this.idCarriage = idCarriage;
            this.idConvoy = idConvoy;
            this.notifyTime = notifyTime;
        }
    }
}
