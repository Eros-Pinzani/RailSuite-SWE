package mapper;

import domain.Notification;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}

