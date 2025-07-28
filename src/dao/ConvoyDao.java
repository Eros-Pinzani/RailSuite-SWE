package dao;

import domain.Carriage;
import domain.Convoy;
import java.sql.SQLException;
import java.util.List;
import businessLogic.service.ConvoyDetailsService;

public interface ConvoyDao {
    static ConvoyDao of() {
        return new ConvoyDaoImp();
    }
    Convoy selectConvoy(int id) throws SQLException;
    List<Convoy> selectAllConvoys() throws SQLException;
    boolean removeConvoy(int id) throws SQLException;
    boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException;
    boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException;
    Integer findConvoyIdByCarriageId(int carriageId) throws SQLException;
    Convoy createConvoy(List<Carriage> carriages) throws SQLException;

    ConvoyDetailsService.ConvoyDetailsRaw selectConvoyDetailsById(int id) throws SQLException;

    class ConvoyAssignedRow {
        public final int convoyId;
        public final String departureStation;
        public final String departureTime;
        public final String arrivalStation;
        public final String arrivalTime;
        public ConvoyAssignedRow(int convoyId, String departureStation, String departureTime, String arrivalStation, String arrivalTime) {
            this.convoyId = convoyId;
            this.departureStation = departureStation;
            this.departureTime = departureTime;
            this.arrivalStation = arrivalStation;
            this.arrivalTime = arrivalTime;
        }
    }
    List<ConvoyAssignedRow> selectAssignedConvoysRowsByStaff(int staffId) throws SQLException;
}
