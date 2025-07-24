package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Staff;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInService {
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private static final Logger logger = Logger.getLogger(LogInService.class.getName());

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
