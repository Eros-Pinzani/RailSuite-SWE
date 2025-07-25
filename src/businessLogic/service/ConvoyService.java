package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Convoy;
import domain.Carriage;
import java.sql.SQLException;
import java.util.List;

public class ConvoyService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public List<Convoy> getAllConvoys() {
        try {
            return facade.selectAllConvoys();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createConvoy(List<Carriage> carriages) {
        try {
            facade.createConvoy(carriages);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
