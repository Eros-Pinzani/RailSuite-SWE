package mapper;

import domain.Notification;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class NotificationMapper {
    /**
     * Crea un oggetto Notification a partire da un ResultSet.
     * Il ResultSet deve avere le colonne: id_carriage, model, id_convoy, work_type, notify_time, id_staff, name, surname, status
     */
    public static Notification toDomain(ResultSet rs) throws SQLException {
        return Notification.of(
            rs.getInt("id_carriage"),
            rs.getString("model"),
            rs.getInt("id_convoy"),
            rs.getString("work_type"),
            rs.getTimestamp("notify_time"),
            rs.getInt("id_staff"),
            rs.getString("name"),
            rs.getString("surname"),
            rs.getString("status")
        );
    }

    public static void setAddNotificationParams(PreparedStatement stmt, int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff) throws SQLException {
        stmt.setInt(1, idCarriage);
        stmt.setInt(2, idConvoy);
        stmt.setTimestamp(3, notifyTime);
        stmt.setString(4, workType);
        stmt.setInt(5, idStaff);
    }

    public static void setDeleteNotificationParams(PreparedStatement stmt, int idCarriage, int idConvoy, Timestamp notifyTime) throws SQLException {
        stmt.setInt(1, idCarriage);
        stmt.setInt(2, idConvoy);
        stmt.setTimestamp(3, notifyTime);
    }

    public static void setInsertNotificationHistoryParams(PreparedStatement stmt, int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff, String staffName, String staffSurname, String status) throws SQLException {
        stmt.setInt(1, idCarriage);
        stmt.setInt(2, idConvoy);
        stmt.setTimestamp(3, notifyTime);
        stmt.setString(4, workType);
        stmt.setInt(5, idStaff);
        stmt.setString(6, staffName);
        stmt.setString(7, staffSurname);
        stmt.setString(8, status);
    }

    public static void setExistsNotificationOrHistoryParams(PreparedStatement stmt, int idCarriage, int idStaff, String workType) throws SQLException {
        stmt.setInt(1, idCarriage);
        stmt.setInt(2, idStaff);
        stmt.setString(3, workType);
        stmt.setInt(4, idCarriage);
        stmt.setInt(5, idStaff);
        stmt.setString(6, workType);
    }

    public static void setNotificationsByConvoyIdParams(PreparedStatement stmt, int convoyId) throws SQLException {
        stmt.setInt(1, convoyId);
    }

    public static void setAllNotificationsForConvoyAndStaffParams(PreparedStatement stmt, int convoyId, int staffId) throws SQLException {
        stmt.setInt(1, convoyId);
        stmt.setInt(2, staffId);
        stmt.setInt(3, convoyId);
        stmt.setInt(4, staffId);
    }
}
