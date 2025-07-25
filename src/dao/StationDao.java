package dao;

import domain.Station;

import java.sql.SQLException;
import java.util.List;

public interface StationDao {
    static StationDao of() {
        return new StationDaoImp();
    }

    Station findById(int id) throws SQLException;
    Station findByLocation(String location) throws SQLException;
    List<Station> findAll() throws SQLException;
    List<Station> findAllHeadStations() throws SQLException;
}