package dao;

import domain.Depot;

import java.sql.SQLException;
import java.util.List;

public interface DepotDao {
    static DepotDao of() {
        return new DepotDaoImp();
    }
    Depot getDepot(int idDepot) throws SQLException;
    List<Depot> getAllDepots() throws SQLException;
    void insertDepot(int idDepot) throws SQLException;
    void deleteDepot(int idDepot) throws SQLException;
}

