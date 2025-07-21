package dao;

import domain.LineStation;
import java.sql.SQLException;
import java.util.List;

public interface LineStationDao {
    LineStation findById(int idLine, int idStation) throws SQLException;
    List<LineStation> findByLine(int idLine) throws SQLException;
}

