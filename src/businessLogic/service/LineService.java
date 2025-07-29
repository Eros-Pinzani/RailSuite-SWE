package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Line;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing railway lines.
 * Provides business logic for retrieving and processing line data.
 */
public class LineService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    /**
     * Returns a list of all lines in the system.
     * Used to display or manage all lines from the controller.
     * @return List of all Line objects.
     */
    public List<Line> getAllLines() {
        try {
            return facade.findAllLines();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
