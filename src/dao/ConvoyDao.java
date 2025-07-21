package dao;

import domain.Carriage;
import domain.Convoy;

import java.sql.SQLException;
import java.util.List;

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
}
