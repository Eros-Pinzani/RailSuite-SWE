package dao;

import domain.Line;
import java.sql.SQLException;
import java.util.List;

public interface LineDao {
    static LineDao of() {
        return new LineDaoImp();
    }

    Line findById(int idLine) throws SQLException;
    List<Line> findAll() throws SQLException;
    List<Line> findByStation(int idStation) throws SQLException;
}
