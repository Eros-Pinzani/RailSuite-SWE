package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Staff;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing staff data.
 * Provides business logic for retrieving and processing staff information.
 */
public class StaffService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    /**
     * Returns a list of all operators (staff) in the system.
     * Used to display or manage all operators from the controller.
     * @return List of all Staff objects.
     */
    public List<Staff> getAllOperators() {
        try {
            return facade.findAllOperators();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
