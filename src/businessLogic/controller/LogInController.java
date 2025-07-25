package businessLogic.controller;

import businessLogic.service.LogInService;
import domain.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private final LogInService logInService = new LogInService();
    private static final Logger logger = Logger.getLogger(LogInController.class.getName());

    @FXML
    public void initialize() {
        emailField.setOnAction(e -> loginButton.fire());
        passwordField.setOnAction(e -> loginButton.fire());
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        errorLabel.setText("");
        Staff staff = logInService.authenticate(email, password);
        if (staff == null) {
            errorLabel.setText("Credenziali errate");
            return;
        }
        UserSession.getInstance().setStaff(staff);
        try {
            String type = staff.getTypeOfStaff().toString();
            if ("OPERATOR".equalsIgnoreCase(type)) {
                SceneManager.getInstance().switchScene("/businessLogic/fxml/OperatorHome.fxml");
            } else {
                if ("SUPERVISOR".equalsIgnoreCase(type)){
                    SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml");
                } else {
                    errorLabel.setText("Ruolo non supportato");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while switching scene", e);
            errorLabel.setText("Errore interno: impossibile cambiare schermata");
        }
    }
}
