package test.businesslogic;

import businessLogic.service.NotificationService;
import businessLogic.service.NotificationObserver;
import businessLogic.RailSuiteFacade;
import dao.NotificationDao;
import domain.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService service;
    private TestNotificationObserver observer;
    private final int testCarriageId = 99999;
    private final int testStaffId = 99999;
    private final String testTypeOfCarriage = "TestType";
    private final String testTypeOfNotification = "MAINTENANCE";
    private final String testStaffName = "Mario";
    private final String testStaffSurname = "Rossi";
    private int testConvoyId; // Modificato in variabile d'istanza

    static class TestNotificationObserver implements NotificationObserver {
        Notification receivedNotification;
        @Override
        public void onNotificationAdded(Notification notification) {
            this.receivedNotification = notification;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        RailSuiteFacade facade = new RailSuiteFacade();
        service = new NotificationService(facade);
        NotificationDao notificationDao = NotificationDao.of();
        observer = new TestNotificationObserver();
        Timestamp testTimestamp = new Timestamp(System.currentTimeMillis());
        // Inserisci la carrozza di test se non esiste
        try {
            PreparedStatement ps = getPreparedStatement();
            ps.close();
        } catch (Exception e) {
            // Ignora errore di chiave duplicata
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Inserisci lo staff di test se non esiste
        try {
            PreparedStatement ps = getStatement();
            ps.close();
        } catch (Exception e) {
            // Ignora errore di chiave duplicata
            if (!e.getMessage().contains("duplicate key value")) throw e;
        }
        // Crea il convoglio di test con la carrozza
        domain.Carriage carriage = domain.Carriage.of(testCarriageId, "JUnitTestModel", testTypeOfCarriage, 2025, 99, null);
        dao.ConvoyDao convoyDao = dao.ConvoyDao.of();
        domain.Convoy convoy = convoyDao.createConvoy(java.util.List.of(carriage));
        testConvoyId = convoy.getId(); // Aggiorna l'id reale del convoglio
        // Pulisci tutte le notifiche residue che referenziano la carriage, lo staff o il convoglio di test
        try {
            String deleteAllTestNotifications = "DELETE FROM notification WHERE (id_carriage = ? OR id_staff = ?) AND id_convoy = ?";
            java.sql.Connection conn = dao.PostgresConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(deleteAllTestNotifications);
            ps.setInt(1, testCarriageId);
            ps.setInt(2, testStaffId);
            ps.setInt(3, testConvoyId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception ignored) {}
        // Inserisci una notifica di test
        notificationDao.addNotification(testCarriageId, testConvoyId, testTimestamp, testTypeOfNotification, testStaffId);
    }

    private PreparedStatement getStatement() throws SQLException {
        String insertStaff = "INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, ?, ?, ?, ?, ?, ?)";
        java.sql.Connection conn = dao.PostgresConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(insertStaff);
        ps.setInt(1, testStaffId);
        ps.setString(2, testStaffName);
        ps.setString(3, testStaffSurname);
        ps.setString(4, "TestAddress");
        ps.setString(5, "test99999@example.com");
        ps.setString(6, "testpassword");
        ps.setString(7, "OPERATOR");
        ps.executeUpdate();
        return ps;
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        String insertCarriage = "INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity) VALUES (?, ?, ?, ?, ?)";
        java.sql.Connection conn = dao.PostgresConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(insertCarriage);
        ps.setInt(1, testCarriageId);
        ps.setString(2, "JUnitTestModel");
        ps.setString(3, testTypeOfCarriage);
        ps.setInt(4, 2025);
        ps.setInt(5, 99);
        ps.executeUpdate();
        return ps;
    }

    @AfterEach
    void tearDown() {
        // Rimuovi tutte le notifiche di test prima di eliminare la carriage, lo staff o il convoglio
        try {
            String deleteAllTestNotifications = "DELETE FROM notification WHERE (id_carriage = ? OR id_staff = ?) AND id_convoy = ?";
            java.sql.Connection conn = dao.PostgresConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(deleteAllTestNotifications);
            ps.setInt(1, testCarriageId);
            ps.setInt(2, testStaffId);
            ps.setInt(3, testConvoyId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception ignored) {}
        // Rimuovi la carrozza di test
        try {
            String deleteCarriage = "DELETE FROM carriage WHERE id_carriage = ?";
            java.sql.Connection conn = dao.PostgresConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(deleteCarriage);
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception ignored) {}
        // Rimuovi il convoglio di test
        try {
            dao.ConvoyDao convoyDao = dao.ConvoyDao.of();
            convoyDao.removeConvoy(testConvoyId);
        } catch (Exception ignored) {}
        // Rimuovi lo staff di test
        try {
            String deleteStaff = "DELETE FROM staff WHERE id_staff = ?";
            java.sql.Connection conn = dao.PostgresConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(deleteStaff);
            ps.setInt(1, testStaffId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception ignored) {}
    }

    @Test
    void getAllNotifications_returnsInsertedNotification() {
        List<Notification> notifications = service.getAllNotifications();
        if (notifications == null) notifications = List.of();
        boolean found = notifications.stream().anyMatch(n -> n.getIdCarriage() == testCarriageId && n.getIdConvoy() == testConvoyId);
        assertTrue(found, "La notifica di test deve essere presente");
    }

    @Test
    void addNotification_insertsNotificationAndNotifiesObserver() {
        service.addObserver(observer);
        Timestamp ts = new Timestamp(System.currentTimeMillis() + 1000); // timestamp diverso
        String testStatus = "NEW";
        service.addNotification(testCarriageId, testTypeOfCarriage, testConvoyId, testTypeOfNotification, ts, testStaffId, testStaffName, testStaffSurname, testStatus);
        List<Notification> notifications = service.getAllNotifications();
        if (notifications == null) notifications = List.of();
        boolean found = notifications.stream().anyMatch(n -> n.getIdCarriage() == testCarriageId && n.getIdConvoy() == testConvoyId && n.getDateTimeOfNotification().equals(ts));
        assertTrue(found, "La notifica aggiunta deve essere presente");
        assertNotNull(observer.receivedNotification, "L'observer deve essere notificato");
        assertEquals(testCarriageId, observer.receivedNotification.getIdCarriage());
    }

    @Test
    void getNotificationsByConvoyId_returnsCorrectNotifications() {
        List<Notification> notifications = service.getNotificationsByConvoyId(testConvoyId);
        if (notifications == null) notifications = List.of();
        boolean found = notifications.stream().anyMatch(n -> n.getIdConvoy() == testConvoyId);
        assertTrue(found, "La notifica di test deve essere presente per il convoglio");
    }

    @Test
    void getAllNotificationsForConvoyAndStaff_returnsCorrectNotifications() {
        List<Notification> notifications = service.getAllNotificationsForConvoyAndStaff(testConvoyId, testStaffId);
        if (notifications == null) notifications = List.of();
        boolean found = notifications.stream().anyMatch(n -> n.getIdConvoy() == testConvoyId && n.getIdStaff() == testStaffId);
        assertTrue(found, "La notifica di test deve essere presente per convoglio e staff");
    }
}