package businessLogic.service;

import dao.ConvoyDao;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
        ConvoyDao dao = ConvoyDao.of();
        List<ConvoyDao.ConvoyAssignedRow> rows = dao.selectAssignedConvoysRowsByStaff(staffId);
        return rows.stream()
            .map(r -> new AssignedConvoyInfo(r.convoyId, r.departureStation, r.departureTime, r.arrivalStation, r.arrivalTime))
            .collect(Collectors.toList());
    }
}
