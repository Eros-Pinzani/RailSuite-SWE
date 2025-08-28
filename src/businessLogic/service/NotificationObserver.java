package businessLogic.service;

import domain.Notification;

public interface NotificationObserver {
    void onNotificationAdded(Notification notification);
}

