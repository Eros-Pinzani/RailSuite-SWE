package test.domain;

import domain.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NotificationImpTest {
    private Notification notification;
    private final int idCarriage = 10;
    private final String typeOfCarriage = "Passeggeri";
    private final int idConvoy = 20;
    private final String typeOfNotification = "TEST";
    private final Timestamp dateTimeOfNotification = Timestamp.valueOf("2025-09-05 10:30:00");
    private final int idStaff = 99;
    private final String staffName = "Mario";
    private final String staffSurname = "Rossi";
    private final String status = "INVIATA";

    @BeforeEach
    void setUp() {
        notification = Notification.of(idCarriage, typeOfCarriage, idConvoy, typeOfNotification, dateTimeOfNotification, idStaff, staffName, staffSurname, status);
    }

    @AfterEach
    void tearDown() {
        notification = null;
    }

    @Test
    void getIdCarriage() {
        assertEquals(idCarriage, notification.getIdCarriage());
    }

    @Test
    void getTypeOfCarriage() {
        assertEquals(typeOfCarriage, notification.getTypeOfCarriage());
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, notification.getIdConvoy());
    }

    @Test
    void getTypeOfNotification() {
        assertEquals(typeOfNotification, notification.getTypeOfNotification());
    }

    @Test
    void getDateTimeOfNotification() {
        assertEquals(dateTimeOfNotification, notification.getDateTimeOfNotification());
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, notification.getIdStaff());
    }

    @Test
    void getStaffName() {
        assertEquals(staffName, notification.getStaffName());
    }

    @Test
    void getStaffSurname() {
        assertEquals(staffSurname, notification.getStaffSurname());
    }

    @Test
    void getStatus() {
        assertEquals(status, notification.getStatus());
    }
}