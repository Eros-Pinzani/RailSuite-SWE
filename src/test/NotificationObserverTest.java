package test;

import businessLogic.service.NotificationObserver;
import domain.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationObserverTest {
    private TestNotificationObserver observer;

    static class TestNotificationObserver implements NotificationObserver {
        Notification receivedNotification;
        @Override
        public void onNotificationAdded(Notification notification) {
            this.receivedNotification = notification;
        }
    }

    @BeforeEach
    void setUp() {
        observer = new TestNotificationObserver();
    }

    @AfterEach
    void tearDown() {
        observer = null;
    }

    @Test
    void onNotificationAdded() {
        Notification notification = Notification.of(
            1, // idCarriage
            "TestType", // typeOfCarriage
            2, // idConvoy
            "INFO", // typeOfNotification
            new java.sql.Timestamp(System.currentTimeMillis()), // dateTimeOfNotification
            3, // idStaff
            "Mario", // staffName
            "Rossi", // staffSurname
            "NEW" // status
        );
        observer.onNotificationAdded(notification);
        assertNotNull(observer.receivedNotification);
        assertEquals(notification, observer.receivedNotification);
    }
}