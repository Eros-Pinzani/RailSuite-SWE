package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Notification;
import java.util.List;

public class SupervisorHomeService {
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private final NotificationService notificationService = new NotificationService(facade);

    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    public void addNotificationObserver(NotificationObserver observer) {
        notificationService.addObserver(observer);
    }

    public static class NotificationRow {
        private final Notification notification;
        private final int idCarriage;
        private final javafx.beans.property.SimpleStringProperty typeOfCarriage;
        private final javafx.beans.property.SimpleStringProperty dateTimeOfNotification;
        private final javafx.beans.property.SimpleStringProperty staffSurname;
        private final javafx.beans.property.SimpleStringProperty typeOfNotification;

        public NotificationRow(Notification notification, int idCarriage, String typeOfCarriage, String dateTimeOfNotification, String staffSurname, String typeOfNotification) {
            this.notification = notification;
            this.idCarriage = idCarriage;
            this.typeOfCarriage = new javafx.beans.property.SimpleStringProperty(typeOfCarriage);
            this.dateTimeOfNotification = new javafx.beans.property.SimpleStringProperty(dateTimeOfNotification);
            this.staffSurname = new javafx.beans.property.SimpleStringProperty(staffSurname);
            this.typeOfNotification = new javafx.beans.property.SimpleStringProperty(typeOfNotification);
        }
        public Notification getNotification() { return notification; }
        public int getIdCarriage() { return idCarriage; }
        public String getTypeOfCarriage() { return typeOfCarriage.get(); }
        public String getDateTimeOfNotification() { return dateTimeOfNotification.get(); }
        public String getStaffSurname() { return staffSurname.get(); }
        public String getTypeOfNotification() { return typeOfNotification.get(); }
    }

    public boolean isNotificationSend(Notification n) {
        return n != null && n.getStatus() != null && n.getStatus().equals("INVIATA");
    }

    public NotificationRow toNotificationRow(Notification n) {
        return new NotificationRow(
            n,
            n.getIdCarriage(),
            n.getTypeOfCarriage(),
            n.getDateTimeOfNotification().toString(),
            n.getStaffSurname(),
            n.getTypeOfNotification()
        );
    }

    public java.util.List<NotificationRow> getAllNotificationRows() {
        java.util.List<Notification> notifications = getAllNotifications();
        if (notifications == null) notifications = java.util.Collections.emptyList();
        java.util.List<NotificationRow> rows = new java.util.ArrayList<>();
        for (Notification n : notifications) {
            if (isNotificationSend(n)) {
                rows.add(toNotificationRow(n));
            }
        }
        return rows;
    }
}
