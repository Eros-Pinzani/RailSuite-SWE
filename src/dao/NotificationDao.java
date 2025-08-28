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
}
