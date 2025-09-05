package test.businesslogic;

import businessLogic.service.SupervisorHomeService;
import dao.PostgresConnection;
import domain.Notification;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupervisorHomeServiceTest {
    private SupervisorHomeService service;
    private static Connection conn;
    private final int testConvoyId = 88888;
    private final int testCarriageId = 88888;
    private final int testStaffId = 88888;

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setUp() throws Exception {
        service = new SupervisorHomeService();
        // Inserisci convoglio di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO convoy (id_convoy) VALUES (?) ON CONFLICT DO NOTHING")) {
            ps.setInt(1, testConvoyId);
            ps.executeUpdate();
        }
        // Inserisci carrozza di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO carriage (id_carriage, model, model_type, year_produced, capacity, id_convoy) VALUES (?, 'TEST', 'TEST', 2020, 10, ?) ON CONFLICT DO NOTHING")) {
            ps.setInt(1, testCarriageId);
            ps.setInt(2, testConvoyId);
            ps.executeUpdate();
        }
        // Inserisci staff di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (?, 'Test', 'Test', 'Test', 'test@test.com', 'pwd', 'OPERATOR') ON CONFLICT DO NOTHING")) {
            ps.setInt(1, testStaffId);
            ps.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        // Elimina la notifica di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE id_carriage = ? AND id_convoy = ?")) {
            ps.setInt(1, testCarriageId);
            ps.setInt(2, testConvoyId);
            ps.executeUpdate();
        }
        // Elimina la carrozza di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM carriage WHERE id_carriage = ?")) {
            ps.setInt(1, testCarriageId);
            ps.executeUpdate();
        }
        // Elimina il convoglio di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM convoy WHERE id_convoy = ?")) {
            ps.setInt(1, testConvoyId);
            ps.executeUpdate();
        }
        // Elimina lo staff di test
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE id_staff = ?")) {
            ps.setInt(1, testStaffId);
            ps.executeUpdate();
        }
    }

    @Test
    void testGetAllNotifications_NotNull() throws Exception {
        // Usa gli id di test
        int idConvoy = testConvoyId;
        int idCarriage = testCarriageId;
        String typeOfCarriage = "TEST";
        String staffName = "Test";
        String staffSurname = "Test";
        String typeOfNotification = "CLEANING";
        String status = "INVIATA";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // Aggiungi la notifica
        var notificationServiceField = SupervisorHomeService.class.getDeclaredField("notificationService");
        notificationServiceField.setAccessible(true);
        var notificationService = notificationServiceField.get(service);
        var addNotificationMethod = notificationService.getClass().getMethod("addNotification", int.class, String.class, int.class, String.class, Timestamp.class, int.class, String.class, String.class, String.class);
        addNotificationMethod.invoke(notificationService, idCarriage, typeOfCarriage, idConvoy, typeOfNotification, now, testStaffId, staffName, staffSurname, status);

        // Verifica che la notifica sia presente
        List<Notification> notifications = service.getAllNotifications();
        assertNotNull(notifications);
        boolean found = notifications.stream().anyMatch(n ->
            n.getIdCarriage() == idCarriage &&
            n.getIdConvoy() == idConvoy &&
            Math.abs(n.getDateTimeOfNotification().getTime() - now.getTime()) < 1000
        );
        assertTrue(found, "La notifica di test non è stata trovata tra quelle restituite.");
    }

    @Test
    void testIsNotificationSend() {
        Notification nSent = Notification.of(1, "A", 1, "CLEANING", Timestamp.valueOf(LocalDateTime.now()), 1, "Mario", "Rossi", "INVIATA");
        Notification nDraft = Notification.of(1, "A", 1, "CLEANING", Timestamp.valueOf(LocalDateTime.now()), 1, "Mario", "Rossi", "BOZZA");
        Notification nNullStatus = Notification.of(1, "A", 1, "CLEANING", Timestamp.valueOf(LocalDateTime.now()), 1, "Mario", "Rossi", null);
        assertTrue(service.isNotificationSend(nSent));
        assertFalse(service.isNotificationSend(nDraft));
        assertFalse(service.isNotificationSend(nNullStatus));
        assertFalse(service.isNotificationSend(null));
    }

    @Test
    void testToNotificationRow() {
        Notification n = Notification.of(1, "A", 1, "CLEANING", Timestamp.valueOf(LocalDateTime.now()), 1, "Mario", "Rossi", "INVIATA");
        SupervisorHomeService.NotificationRow row = service.toNotificationRow(n);
        assertNotNull(row);
        assertEquals(n.getIdCarriage(), row.getIdCarriage());
        assertEquals(n.getTypeOfCarriage(), row.getTypeOfCarriage());
        assertEquals(n.getStaffSurname(), row.getStaffSurname());
        assertEquals(n.getTypeOfNotification(), row.getTypeOfNotification());
    }
}
