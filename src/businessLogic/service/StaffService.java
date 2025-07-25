package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Staff;
import java.sql.SQLException;
import java.util.List;

public class StaffService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public List<Staff> getAllOperators() {
        try {
            return facade.findAllOperators();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
