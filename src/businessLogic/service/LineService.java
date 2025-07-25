package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Line;
import java.sql.SQLException;
import java.util.List;

public class LineService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public List<Line> getAllLines() {
        try {
            return facade.findAllLines();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
