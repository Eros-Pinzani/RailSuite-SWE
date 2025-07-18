package dao;

import domain.Carriage;

import java.sql.SQLException;
import java.util.List;

public interface CarriageDao {
    static CarriageDao of() {
        return new CarriageDaoImp();
    }
    Carriage selectCarriage(int id) throws SQLException;
    List<Carriage> selectAllCarriages() throws SQLException;
}
