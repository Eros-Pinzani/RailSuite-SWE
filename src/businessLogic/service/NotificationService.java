package businessLogic.service;

import dao.NotificationDao;
import domain.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
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
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle notifiche", e);
            return List.of();
        }
    }

    public void addNotification(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, java.sql.Timestamp notifyTime, int idStaff, String staffName, String staffSurname, String status) {
        try {
            notificationDao.addNotification(idCarriage, idConvoy, notifyTime, typeOfNotification, idStaff);
            Notification notification = Notification.of(idCarriage, typeOfCarriage, idConvoy, typeOfNotification, notifyTime, idStaff, staffName, staffSurname, status);
            notifyObservers(notification);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta della notifica", e);
        }
    }

    /**
     * Verifica se esiste una notifica attiva o storicizzata (anche negata) per la stessa carrozza, operatore e tipo.
     */
    public boolean existsNotificationOrHistory(int idCarriage, int idStaff, String workType) {
        try {
            return notificationDao.existsNotificationOrHistory(idCarriage, idStaff, workType);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la verifica di notifiche esistenti", e);
            return false;
        }
    }
}
