package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Run;
import domain.Station;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorHomeService {

    public static class AssignedConvoyInfo {
        public int convoyId;
        public String departureStation;
        public String departureTime;
        public String arrivalStation;
        public String arrivalTime;

        public AssignedConvoyInfo(int convoyId, String departureStation, String departureTime, String arrivalStation, String arrivalTime) {
            this.convoyId = convoyId;
            this.departureStation = departureStation;
            this.departureTime = departureTime;
            this.arrivalStation = arrivalStation;
            this.arrivalTime = arrivalTime;
        }
    }

    public List<AssignedConvoyInfo> getAssignedConvoysForOperator(int staffId) throws SQLException {
        RailSuiteFacade facade = new RailSuiteFacade();
        List<Run> runs = facade.selectRunsByStaff(staffId);
        List<AssignedConvoyInfo> result = new ArrayList<>();
        for (Run run : runs) {
            int convoyId = run.getIdConvoy();
            String departureStation = "";
            String arrivalStation = "";
            String departureTime = run.getTimeDeparture() != null ? run.getTimeDeparture().toString() : "";
            String arrivalTime = run.getTimeArrival() != null ? run.getTimeArrival().toString() : "";
            try {
                Station dep = facade.findStationById(run.getIdFirstStation());
                Station arr = facade.findStationById(run.getIdLastStation());
                if (dep != null) departureStation = dep.getLocation();
                if (arr != null) arrivalStation = arr.getLocation();
            } catch (Exception ignored) {
            }
            result.add(new AssignedConvoyInfo(convoyId, departureStation, departureTime, arrivalStation, arrivalTime));
        }
        return result;
    }
}
