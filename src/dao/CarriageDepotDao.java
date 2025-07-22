package dao;

import domain.CarriageDepot;

import java.sql.SQLException;
import java.util.List;

public interface CarriageDepotDao {
    CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException;
    List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException;
    void insertCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
    void updateCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
    void deleteCarriageDepot(CarriageDepot carriageDepot) throws SQLException;
}

