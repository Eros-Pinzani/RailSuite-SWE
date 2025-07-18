//package domain;
//
//public class staff_pool {
//    public boolean canInsertShift(int idStaff, LocalDateTime newShiftStart, LocalDateTime newShiftEnd, DataSource dataSource) throws SQLException {
//        // 1. Controlla che tra la fine dell'ultimo turno e l'inizio del nuovo ci siano almeno 15 minuti
//        String lastShiftQuery = "SELECT shift_end FROM staff_pool WHERE id_staff = ? ORDER BY shift_end DESC LIMIT 1";
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(lastShiftQuery)) {
//            ps.setInt(1, idStaff);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                LocalDateTime lastShiftEnd = rs.getTimestamp("shift_end").toLocalDateTime();
//                if (Duration.between(lastShiftEnd, newShiftStart).toMinutes() < 15) {
//                    return false; // Pausa insufficiente
//                }
//            }
//        }
//
//        // 2. Calcola le ore lavorate nella giornata
//        String hoursQuery = "SELECT SUM(EXTRACT(EPOCH FROM (shift_end - shift_start))/3600) as hours " +
//                "FROM staff_pool WHERE id_staff = ? AND DATE(shift_start) = ?";
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(hoursQuery)) {
//            ps.setInt(1, idStaff);
//            ps.setDate(2, java.sql.Date.valueOf(newShiftStart.toLocalDate()));
//            ResultSet rs = ps.executeQuery();
//            double hours = 0;
//            if (rs.next()) {
//                hours = rs.getDouble("hours");
//            }
//            double newShiftHours = Duration.between(newShiftStart, newShiftEnd).toHours();
//            if (hours + newShiftHours > 12) {
//                return false; // Supera le 12h
//            }
//        }
//        return true; // Tutti i vincoli rispettati
//    }
//}
