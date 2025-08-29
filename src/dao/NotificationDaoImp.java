package dao;

import domain.Notification;
import mapper.NotificationMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class NotificationDaoImp implements NotificationDao {
    private static final String allNotificationsQuery = """
            SELECT n.id_carriage, n.id_convoy, n.notify_time, n.work_type, n.id_staff, s.name, s.surname, c.model, n.status
            FROM notification n JOIN staff s on s.id_staff = n.id_staff JOIN carriage c on c.id_carriage = n.id_carriage
            """;

    private static final String addNotificationQuery = """
            INSERT INTO notification (id_carriage, id_convoy, notify_time, work_type, id_staff)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String deleteNotificationQuery = """
            DELETE FROM notification WHERE id_carriage = ? AND id_convoy = ? AND notify_time = ?
            """;

    private static final String insertNotificationHistoryQuery = """
        INSERT INTO notification_history (id_carriage, id_convoy, notify_time, work_type, id_staff, staff_name, staff_surname, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String existsNotificationOrHistoryQuery = """
        SELECT COUNT(*) as cnt FROM (
            SELECT id_carriage, id_staff, work_type FROM notification WHERE id_carriage = ? AND id_staff = ? AND work_type = ?
            UNION ALL
            SELECT id_carriage, id_staff, work_type FROM notification_history WHERE id_carriage = ? AND id_staff = ? AND work_type = ?
        ) AS combined
        """;

    private static final String notificationsByConvoyIdQuery = """
            SELECT n.id_carriage, n.id_convoy, n.notify_time, n.work_type, n.id_staff, s.name, s.surname, c.model, n.status
            FROM notification n JOIN staff s on s.id_staff = n.id_staff JOIN carriage c on c.id_carriage = n.id_carriage
            WHERE n.id_convoy = ?
            """;

    private static final String allNotificationsForConvoyAndStaffQuery = """
    SELECT n.id_carriage, n.id_convoy, n.notify_time, n.work_type, n.id_staff, s.name, s.surname, c.model, n.status
    FROM notification n
    JOIN staff s on s.id_staff = n.id_staff
    JOIN carriage c on c.id_carriage = n.id_carriage
    WHERE n.id_convoy = ? AND n.id_staff = ?
    UNION ALL
    SELECT nh.id_carriage, nh.id_convoy, nh.notify_time, nh.work_type, nh.id_staff, nh.staff_name as name, nh.staff_surname as surname, c.model, nh.status
    FROM notification_history nh
    JOIN carriage c on c.id_carriage = nh.id_carriage
    WHERE nh.id_convoy = ? AND nh.id_staff = ?
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
    public void deleteNotification(int idCarriage, int idConvoy, Timestamp notifyTime) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteNotificationQuery)) {
            stmt.setInt(1, idCarriage);
            stmt.setInt(2, idConvoy);
            stmt.setTimestamp(3, notifyTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting notification: ", e);
        }
    }

    @Override
    public void moveNotificationToHistory(int idCarriage, int idConvoy, Timestamp notifyTime, String workType, int idStaff, String staffName, String staffSurname, String status) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertNotificationHistoryQuery)) {
            stmt.setInt(1, idCarriage);
            stmt.setInt(2, idConvoy);
            stmt.setTimestamp(3, notifyTime);
            stmt.setString(4, workType);
            stmt.setInt(5, idStaff);
            stmt.setString(6, staffName);
            stmt.setString(7, staffSurname);
            stmt.setString(8, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error moving notification to history: ", e);
        }
        // Dopo aver inserito nello storico, cancella dalla tabella principale
        deleteNotification(idCarriage, idConvoy, notifyTime);
    }

    @Override
    public boolean existsNotificationOrHistory(int idCarriage, int idStaff, String workType) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(existsNotificationOrHistoryQuery)) {
            stmt.setInt(1, idCarriage);
            stmt.setInt(2, idStaff);
            stmt.setString(3, workType);
            stmt.setInt(4, idCarriage);
            stmt.setInt(5, idStaff);
            stmt.setString(6, workType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new SQLException("Error checking notification or history existence: ", e);
        }
    }

    @Override
    public List<Notification> getNotificationsByConvoyId(int convoyId) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(notificationsByConvoyIdQuery)) {
            stmt.setInt(1, convoyId);
            ResultSet rs = stmt.executeQuery();
            return resultSetToNotificationList(rs);
        } catch (SQLException e) {
            throw new SQLException("Error finding notification by convoyId: ", e);
        }
    }

    @Override
    public List<Notification> getAllNotificationsForConvoyAndStaff(int convoyId, int staffId) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(allNotificationsForConvoyAndStaffQuery)) {
            stmt.setInt(1, convoyId);
            stmt.setInt(2, staffId);
            stmt.setInt(3, convoyId);
            stmt.setInt(4, staffId);
            ResultSet rs = stmt.executeQuery();
            return resultSetToNotificationList(rs);
        } catch (SQLException e) {
            throw new SQLException("Error finding notifications for convoy and staff: ", e);
        }
    }

    @Override
    public List<Notification> selectApprovedNotificationsByConvoy(int convoyId) throws SQLException {
        List<Notification> approved = new ArrayList<>();
        String sql = "SELECT n.id_carriage, n.id_convoy, n.notify_time, n.work_type, n.id_staff, s.name, s.surname, c.model, n.status " +
                "FROM notification_history n " +
                "JOIN staff s ON s.id_staff = n.id_staff " +
                "JOIN carriage c ON c.id_carriage = n.id_carriage " +
                "WHERE n.id_convoy = ? AND n.status = 'APPROVATA'";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, convoyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                approved.add(NotificationMapper.toDomain(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore nel recupero delle notifiche APPROVATE per il convoglio: " + convoyId, e);
        }
        return approved;
    }
}
