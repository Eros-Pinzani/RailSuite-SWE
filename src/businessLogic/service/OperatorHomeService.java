package businessLogic.service;

import businessLogic.RailSuiteFacade;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for operator home functionalities.
 * Provides business logic for retrieving convoys assigned to an operator.
 */
public class OperatorHomeService {

    private final RailSuiteFacade facade;

    public OperatorHomeService() {
        this.facade = new RailSuiteFacade();
    }

    public static class AssignedConvoyInfo {
        public int convoyId;
        public int idLine;
        public int idStaff;
        public int idFirstStation;
        public java.sql.Timestamp timeDeparture;
        public String departureStation;
        public String arrivalStation;
        public String arrivalTime;

        public AssignedConvoyInfo(int convoyId, int idLine, int idStaff, int idFirstStation, java.sql.Timestamp timeDeparture, String departureStation, String arrivalStation, String arrivalTime) {
            this.convoyId = convoyId;
            this.idLine = idLine;
            this.idStaff = idStaff;
            this.idFirstStation = idFirstStation;
            this.timeDeparture = timeDeparture;
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            this.arrivalTime = arrivalTime;
        }
    }

    /**
     * Returns a list of convoys assigned to a specific operator (staff member).
     * Used to display the operator's assigned convoys in the home view.
     * @param staffId The staff member's ID.
     * @return List of AssignedConvoyInfo objects for the operator.
     */
    public List<AssignedConvoyInfo> getAssignedConvoysForOperator(int staffId) throws SQLException {
        List<AssignedConvoyInfo> rows = facade.getAssignedConvoysInfoByStaff(staffId);
        java.time.LocalDate today = java.time.LocalDate.now();
        return rows.stream()
            .filter(r -> {
                try {
                    java.time.LocalDate depDate = r.timeDeparture.toLocalDateTime().toLocalDate();
                    return !depDate.isBefore(today);
                } catch (Exception e) {
                    return false;
                }
            })
            .map(r -> {
                String arrivalTime = r.arrivalTime;
                try {
                    String depTimeStr = r.timeDeparture.toLocalDateTime().toLocalTime().toString();
                    List<domain.TimeTable.StationArrAndDep> timeTable = facade.findTimeTableForRun(r.idLine, r.idFirstStation, depTimeStr);
                    if (timeTable != null && !timeTable.isEmpty()) {
                        String lastArr = timeTable.get(timeTable.size() - 1).getArriveTime();
                        if (lastArr != null && !lastArr.isBlank() && !lastArr.equals("------")) {
                            java.time.LocalDate depDate = r.timeDeparture.toLocalDateTime().toLocalDate();
                            java.time.LocalTime arrTime = java.time.LocalTime.parse(lastArr);
                            java.time.LocalDateTime arrDateTime = java.time.LocalDateTime.of(depDate, arrTime);
                            arrivalTime = arrDateTime.toString().replace('T', ' ');
                        }
                    }
                } catch (Exception e) {
                    // In caso di errore, fallback su quello del DB
                }
                return new AssignedConvoyInfo(r.convoyId, r.idLine, r.idStaff, r.idFirstStation, r.timeDeparture, r.departureStation, r.arrivalStation, arrivalTime);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
