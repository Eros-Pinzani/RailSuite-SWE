package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Carriage;
import java.sql.SQLException;
import java.util.List;

public class CarriageService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public List<Carriage> getAllCarriages() {
        try {
            return facade.selectAllCarriages();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCarriage(Carriage carriage) {
        try {
            facade.insertCarriage(carriage);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
