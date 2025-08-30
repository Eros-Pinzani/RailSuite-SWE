package mapper;

import domain.Convoy;
import domain.Carriage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ConvoyMapper {
    /**
     * Maps a ResultSet row and a list of carriages to a Convoy domain object.
     * @param rs the ResultSet containing convoy data
     * @param carriages the list of carriages belonging to the convoy
     * @return a Convoy domain object
     * @throws SQLException if a database access error occurs
     */
    public static Convoy toDomain(ResultSet rs, List<Carriage> carriages) throws SQLException {
        return Convoy.of(
            rs.getInt("id_convoy"),
            carriages
        );
    }

    /**
     * Popola un PreparedStatement per la query selectConvoy o selectCarriagesByConvoyId.
     */
    public static void toPreparedStatementForSelectConvoy(PreparedStatement stmt, int id) throws SQLException {
        stmt.setInt(1, id);
    }

    /**
     * Popola un PreparedStatement per la query deleteConvoy o updateCarriageConvoy (singolo id).
     */
    public static void toPreparedStatementForConvoyId(PreparedStatement stmt, int id) throws SQLException {
        stmt.setInt(1, id);
    }

    /**
     * Popola un PreparedStatement per la query removeCarriageFromConvoy.
     */
    public static void toPreparedStatementForRemoveCarriageFromConvoy(PreparedStatement stmt, Carriage carriage) throws SQLException {
        stmt.setInt(1, carriage.getId());
    }

    /**
     * Popola un PreparedStatement per la query updateCarriageConvoy (batch).
     */
    public static void toPreparedStatementForUpdateCarriageConvoy(PreparedStatement stmt, int convoyId, Carriage carriage) throws SQLException {
        stmt.setInt(1, convoyId);
        stmt.setInt(2, carriage.getId());
    }

    /**
     * Popola un PreparedStatement per la query selectConvoyDetailsById.
     */
    public static void toPreparedStatementForSelectConvoyDetailsById(PreparedStatement stmt, int id) throws SQLException {
        stmt.setInt(1, id);
    }

    /**
     * Popola un PreparedStatement per la query selectAssignedConvoysRowsByStaff.
     */
    public static void toPreparedStatementForSelectAssignedConvoysRowsByStaff(PreparedStatement stmt, int staffId) throws SQLException {
        stmt.setInt(1, staffId);
    }

    /**
     * Popola un PreparedStatement per la query getConvoysForNewRun.
     */
    public static void toPreparedStatementForGetConvoysForNewRun(PreparedStatement stmt, int idStation, java.sql.Timestamp departureTimestamp, int idLine) throws SQLException {
        stmt.setInt(1, idStation);
        stmt.setTimestamp(2, departureTimestamp);
        stmt.setTimestamp(3, departureTimestamp);
        stmt.setInt(4, idLine);
    }

    /**
     * Popola un PreparedStatement per la query addCarriagesToConvoy.
     */
    public static void toPreparedStatementForAddCarriagesToConvoy(PreparedStatement stmt, int id, List<Carriage> carriages) throws SQLException {
        stmt.setInt(1, id);
        for (int j = 0; j < carriages.size(); j++) {
            stmt.setInt(j + 2, carriages.get(j).getId());
        }
    }

    /**
     * Mappa una riga del ResultSet in un ConvoyAssignedRow.
     */
    public static dao.ConvoyDao.ConvoyAssignedRow toConvoyAssignedRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new dao.ConvoyDao.ConvoyAssignedRow(
            rs.getInt("id_convoy"),
            rs.getInt("id_line"),
            rs.getInt("id_staff"),
            rs.getInt("id_first_station"),
            rs.getTimestamp("time_departure"),
            rs.getString("departure_station") != null ? rs.getString("departure_station") : "",
            rs.getString("arrival_station") != null ? rs.getString("arrival_station") : "",
            rs.getString("time_arrival") != null ? rs.getString("time_arrival") : ""
        );
    }

    /**
     * Mappa una riga del ResultSet in una StationRow.
     */
    public static businessLogic.service.ConvoyDetailsService.StationRow toStationRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        String stationName = rs.getString("station_name");
        if (stationName == null) return null;
        return new businessLogic.service.ConvoyDetailsService.StationRow(stationName, "", "");
    }

    // Nuovi metodi per separare get/set dal DAO:

    public static void setConvoyId(PreparedStatement stmt, int id) throws SQLException {
        stmt.setInt(1, id);
    }

    public static int getConvoyId(ResultSet rs) throws SQLException {
        return rs.getInt("id_convoy");
    }

    public static void setCarriageId(PreparedStatement stmt, Carriage carriage) throws SQLException {
        stmt.setInt(1, carriage.getId());
    }

    public static void setConvoyAndCarriageId(PreparedStatement stmt, int convoyId, Carriage carriage) throws SQLException {
        stmt.setInt(1, convoyId);
        stmt.setInt(2, carriage.getId());
    }

    public static void setStaffId(PreparedStatement stmt, int staffId) throws SQLException {
        stmt.setInt(1, staffId);
    }

    public static void setConvoysForNewRunParams(PreparedStatement stmt, int idStation, java.sql.Timestamp departureTimestamp, int idLine) throws SQLException {
        stmt.setInt(1, idStation);
        stmt.setTimestamp(2, departureTimestamp);
        stmt.setTimestamp(3, departureTimestamp);
        stmt.setInt(4, idLine);
    }

    public static void setAddCarriagesToConvoyParams(PreparedStatement stmt, int id, List<Carriage> carriages) throws SQLException {
        stmt.setInt(1, id);
        for (int j = 0; j < carriages.size(); j++) {
            stmt.setInt(j + 2, carriages.get(j).getId());
        }
    }

    public static List<Carriage> toCarriageList(ResultSet rs) throws SQLException {
        List<Carriage> carriages = new ArrayList<>();
        while (rs.next()) {
            carriages.add(mapper.CarriageMapper.toDomain(rs));
        }
        return carriages;
    }

    public static List<Convoy> toConvoyListForNewRun(ResultSet rs) throws SQLException {
        List<Convoy> convoys = new ArrayList<>();
        int lastConvoyId = -1;
        List<Carriage> carriages = new ArrayList<>();
        while (rs.next()) {
            int convoyId = rs.getInt("id_convoy");
            if (convoyId != lastConvoyId && lastConvoyId != -1) {
                convoys.add(domain.Convoy.of(lastConvoyId, new ArrayList<>(carriages)));
                carriages.clear();
            }
            lastConvoyId = convoyId;
            if (rs.getInt("id_carriage") != 0) {
                carriages.add(domain.Carriage.of(
                        rs.getInt("id_carriage"),
                        rs.getString("model"),
                        rs.getString("model_type"),
                        rs.getInt("year_produced"),
                        rs.getInt("capacity"),
                        convoyId
                ));
            }
        }
        if (lastConvoyId != -1) {
            convoys.add(domain.Convoy.of(lastConvoyId, carriages));
        }
        return convoys;
    }

    public static businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw toConvoyDetailsRaw(ResultSet rs, int id) throws SQLException {
        businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw raw = new businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw();
        raw.convoyId = id;
        raw.lineName = "";
        raw.staffName = "";
        raw.idStaff = -1;
        raw.departureStation = "";
        raw.departureTime = "";
        raw.arrivalStation = "";
        raw.arrivalTime = "";
        java.util.List<domain.Carriage> carriages = new java.util.ArrayList<>();
        java.util.Set<Integer> carriageIds = new java.util.HashSet<>();
        java.util.List<businessLogic.service.ConvoyDetailsService.StationRow> stationRows = new java.util.ArrayList<>();
        while (rs.next()) {
            int carriageId = rs.getInt("id_carriage");
            if (carriageId > 0 && !carriageIds.contains(carriageId)) {
                carriages.add(domain.Carriage.of(
                        carriageId,
                        rs.getString("model"),
                        rs.getString("model_type"),
                        rs.getInt("year_produced"),
                        rs.getInt("capacity"),
                        id
                ));
                carriageIds.add(carriageId);
            }
            if (raw.lineName.isEmpty() && rs.getString("line_name") != null) {
                raw.lineName = rs.getString("line_name");
            }
            if (raw.staffName.isEmpty() && rs.getString("staff_name") != null) {
                raw.staffName = rs.getString("staff_name") + " " + rs.getString("staff_surname");
            }
            if (raw.departureStation.isEmpty() && rs.getString("departure_station") != null) {
                raw.departureStation = rs.getString("departure_station");
            }
            if (raw.arrivalStation.isEmpty() && rs.getString("arrival_station") != null) {
                raw.arrivalStation = rs.getString("arrival_station");
            }
            if (raw.departureTime.isEmpty() && rs.getString("time_departure") != null) {
                raw.departureTime = rs.getString("time_departure");
            }
            if (raw.arrivalTime.isEmpty() && rs.getString("time_arrival") != null) {
                raw.arrivalTime = rs.getString("time_arrival");
            }
            int stationId = rs.getInt("id_station");
            String stationName = rs.getString("station_name");
            if (stationId > 0 && stationName != null && stationRows.stream().noneMatch(s -> s.stationName.equals(stationName))) {
                businessLogic.service.ConvoyDetailsService.StationRow row = toStationRow(rs);
                if (row != null) stationRows.add(row);
            }
            if (raw.idStaff == -1 && rs.getInt("id_staff") > 0) {
                raw.idStaff = rs.getInt("id_staff");
            }
        }
        raw.carriages = carriages;
        raw.stationRows = stationRows;
        return raw;
    }
}
