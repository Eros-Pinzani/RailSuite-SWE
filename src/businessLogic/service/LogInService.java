package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Staff;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for user authentication.
 * Provides business logic for verifying staff credentials and returning staff data.
 */
public class LogInService {
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private static final Logger logger = Logger.getLogger(LogInService.class.getName());

    /**
     * Authenticates a staff member by email and password.
     * Used to verify credentials during the login process.
     * @param email The staff email.
     * @param password The staff password.
     * @return The Staff object if credentials are valid, otherwise null.
     */
    public Staff authenticate(String email, String password) {
        try {
            Staff staff = facade.findStaffByEmail(email);
            if (staff != null && staff.getPassword().equals(password)) {
                return staff;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during authentication", e);
        }
        return null;
    }
}
