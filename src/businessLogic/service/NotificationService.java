package businessLogic.service;

import dao.NotificationDao;
import domain.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDao notificationDao;

    public NotificationService() {
        this.notificationDao = NotificationDao.of();
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationDao.getAllNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}

