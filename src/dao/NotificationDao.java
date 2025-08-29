package dao;

import domain.Notification;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface NotificationDao {
    static NotificationDao of() {
        return new NotificationDaoImp();
    }

    List<Notification> getAllNotifications() throws SQLException;
    void addNotification(int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff) throws SQLException;
    boolean deleteNotification(int idCarriage, int idConvoy, Timestamp notifyTime) throws SQLException;

    /**
     * Sposta una notifica dalla tabella principale allo storico con stato.
     */
    void moveNotificationToHistory(int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff, String staffName, String staffSurname, String status) throws SQLException;

    /**
     * Verifica se esiste una notifica (aperta o storicizzata) per la stessa carrozza, operatore e tipo.
     */
    boolean existsNotificationOrHistory(int idCarriage, int idStaff, String workType) throws SQLException;
    void updateNotificationStatus(int idCarriage, int idConvoy, java.sql.Timestamp notifyTime, String status) throws SQLException;
    List<Notification> getNotificationsByConvoyId(int convoyId) throws SQLException;
    List<Notification> getAllNotificationsForConvoyAndStaff(int convoyId, int staffId) throws SQLException;
}
