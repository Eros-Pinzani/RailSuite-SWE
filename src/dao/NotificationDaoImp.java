package dao;

import domain.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class NotificationDaoImp implements NotificationDao {
    private static final String allNotificationsQuery = """
            SELECT n.id_carriage, n.id_convoy, n.notify_time, n.work_type, n.id_staff, s.name, s.surname, c.model_type
            FROM notification n JOIN staff s on s.id_staff = n.id_staff JOIN carriage c on c.id_carriage = n.id_carriage
            """;

    private static final String addNotificationQuery = """
            INSERT INTO notification (id_carriage, id_convoy, notify_time, work_type, id_staff)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String deleteNotificationQuery = """
            DELETE FROM notification WHERE id_carriage = ? AND id_convoy = ? AND notify_time = ?
            """;

    public NotificationDaoImp() {
    }

    @Override
    public List<Notification> getAllNotifications() throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(allNotificationsQuery)) {
            ResultSet rs = stmt.executeQuery();
            return resultSetToNotificationList(rs);
        } catch (SQLException e) {
            throw new SQLException("Error finding notification: ", e);
        }
    }

    private List<Notification> resultSetToNotificationList(ResultSet rs) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        while (rs.next()) {
            notifications.add(resultSetToNotification(rs));
        }
        if (notifications.isEmpty()) {
            return null;
        }
        return notifications;
    }

    private Notification resultSetToNotification(ResultSet rs) throws SQLException {
        return Notification.of(
                rs.getInt("id_carriage"),
                rs.getString("model_type"),
                rs.getInt("id_convoy"),
                rs.getString("work_type"),
                rs.getTimestamp("notify_time"),
                rs.getInt("id_staff"),
                rs.getString("name"),
                rs.getString("surname")
        );
    }

    @Override
    public void addNotification(int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(addNotificationQuery)) {
            stmt.setInt(1, idCarriage);
            stmt.setInt(2, idConvoy);
            stmt.setTimestamp(3, notifyTime);
            stmt.setString(4, workType);
            stmt.setInt(5, idStaff);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error adding notification: ", e);
        }
    }

    @Override
    public boolean deleteNotification(int idCarriage, int idConvoy, Timestamp notifyTime) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteNotificationQuery)) {
            stmt.setInt(1, idCarriage);
            stmt.setInt(2, idConvoy);
            stmt.setTimestamp(3, notifyTime);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error deleting notification: ", e);
        }
    }
}
