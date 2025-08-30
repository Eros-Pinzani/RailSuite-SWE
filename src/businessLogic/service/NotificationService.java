package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final RailSuiteFacade facade;
    private final List<NotificationObserver> observers = new ArrayList<>();

    public NotificationService(RailSuiteFacade facade) {
        this.facade = facade;
    }

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Notification notification) {
        for (NotificationObserver observer : observers) {
            observer.onNotificationAdded(notification);
        }
    }

    public List<Notification> getAllNotifications() {
        try {
            return facade.getAllNotifications();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle notifiche", e);
            return List.of();
        }
    }

    public void addNotification(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, java.sql.Timestamp notifyTime, int idStaff, String staffName, String staffSurname, String status) {
        try {
            // Passa solo i parametri necessari alla Facade
            facade.addNotification(idCarriage, idConvoy, notifyTime, typeOfNotification, idStaff);
            Notification notification = Notification.of(idCarriage, typeOfCarriage, idConvoy, typeOfNotification, notifyTime, idStaff, staffName, staffSurname, status);
            notifyObservers(notification);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta della notifica", e);
        }
    }
}
