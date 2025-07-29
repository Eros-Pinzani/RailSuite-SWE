package businessLogic.service;

import dao.ConvoyDao;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for operator home functionalities.
 * Provides business logic for retrieving convoys assigned to an operator.
 */
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

    /**
     * Returns a list of convoys assigned to a specific operator (staff member).
     * Used to display the operator's assigned convoys in the home view.
     * @param staffId The staff member's ID.
     * @return List of AssignedConvoyInfo objects for the operator.
     */
    public List<AssignedConvoyInfo> getAssignedConvoysForOperator(int staffId) throws SQLException {
        ConvoyDao dao = ConvoyDao.of();
        List<ConvoyDao.ConvoyAssignedRow> rows = dao.selectAssignedConvoysRowsByStaff(staffId);
        return rows.stream()
            .map(r -> new AssignedConvoyInfo(r.convoyId, r.departureStation, r.departureTime, r.arrivalStation, r.arrivalTime))
            .collect(Collectors.toList());
    }
}
