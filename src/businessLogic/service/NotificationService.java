package businessLogic.service;

import dao.NotificationDao;
import domain.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final NotificationDao notificationDao;
    private final List<NotificationObserver> observers = new ArrayList<>();

    public NotificationService() {
        this.notificationDao = NotificationDao.of();
    }

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Notification notification) {
        for (NotificationObserver observer : observers) {
            observer.onNotificationAdded(notification);
        }
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationDao.getAllNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addNotification(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, java.sql.Timestamp notifyTime, int idStaff, String staffName, String staffSurname) {
        try {
            notificationDao.addNotification(idCarriage, idConvoy, notifyTime, typeOfNotification, idStaff);
            Notification notification = Notification.of(idCarriage, typeOfCarriage, idConvoy, typeOfNotification, notifyTime, idStaff, staffName, staffSurname);
            notifyObservers(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
