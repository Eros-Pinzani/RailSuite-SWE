package test;

import businessLogic.service.SupervisorHomeService;
import domain.Notification;
import org.junit.jupiter.api.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupervisorHomeServiceTest {
    private SupervisorHomeService service;

    @BeforeEach
    void setUp() {
        service = new SupervisorHomeService();
    }

    @Test
    void testGetAllNotifications_NotNull() throws Exception {
        // Recupera id reali dal DB tramite facade
        var facadeField = SupervisorHomeService.class.getDeclaredField("facade");
        facadeField.setAccessible(true);
        var facade = facadeField.get(service);
        // Recupera primo convoglio
        var selectAllConvoys = facade.getClass().getMethod("selectAllConvoys");
        @SuppressWarnings("unchecked")
        List<domain.Convoy> convoys = (List<domain.Convoy>) selectAllConvoys.invoke(facade);
        assertFalse(convoys.isEmpty(), "Nessun convoglio presente nel DB");
        int idConvoy = convoys.getFirst().getId();
        // Recupera le carrozze del primo convoglio tramite l'interfaccia
        var carriageDaoField = facade.getClass().getDeclaredField("carriageDao");
        carriageDaoField.setAccessible(true);
        dao.CarriageDao carriageDao = (dao.CarriageDao) carriageDaoField.get(facade);
        List<domain.Carriage> carriages = carriageDao.selectCarriagesByConvoyId(idConvoy);
        assertFalse(carriages.isEmpty(), "Nessuna carrozza presente nel DB per il convoglio");
        int idCarriage = carriages.getFirst().getId();
        String typeOfCarriage = carriages.getFirst().getModelType();
        // Recupera primo staff
        var findAllOperators = facade.getClass().getMethod("findAllOperators");
        @SuppressWarnings("unchecked")
        List<domain.Staff> staffList = (List<domain.Staff>) findAllOperators.invoke(facade);
        assertFalse(staffList.isEmpty(), "Nessuno staff presente nel DB");
        int idStaff = staffList.getFirst().getIdStaff();
        String staffName = staffList.getFirst().getName();
        String staffSurname = staffList.getFirst().getSurname();
        String typeOfNotification = "CLEANING";
        String status = "INVIATA";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // Aggiungi la notifica
        var notificationServiceField = SupervisorHomeService.class.getDeclaredField("notificationService");
        notificationServiceField.setAccessible(true);
        var notificationService = notificationServiceField.get(service);
        var addNotificationMethod = notificationService.getClass().getMethod("addNotification", int.class, String.class, int.class, String.class, Timestamp.class, int.class, String.class, String.class, String.class);
        addNotificationMethod.invoke(notificationService, idCarriage, typeOfCarriage, idConvoy, typeOfNotification, now, idStaff, staffName, staffSurname, status);

        // Verifica che la notifica sia presente
        List<Notification> notifications = service.getAllNotifications();
        assertNotNull(notifications);
        boolean found = notifications.stream().anyMatch(n ->
            n.getIdCarriage() == idCarriage &&
            n.getIdConvoy() == idConvoy &&
            n.getDateTimeOfNotification().equals(now)
        );
        assertTrue(found, "La notifica di test non è stata trovata tra quelle restituite.");

        // Pulizia: rimuovi la notifica
        var deleteNotificationMethod = facade.getClass().getMethod("deleteNotification", int.class, int.class, Timestamp.class);
        deleteNotificationMethod.invoke(facade, idCarriage, idConvoy, now);
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
