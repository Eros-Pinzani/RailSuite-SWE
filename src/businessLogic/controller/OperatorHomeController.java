package businessLogic.controller;

import businessLogic.service.OperatorHomeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import domain.Staff;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;

public class OperatorHomeController {
    @FXML
    private Label operatorNameLabel;
    @FXML
    private ImageView profileImage;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;

    private final OperatorHomeService operatorHomeService = new OperatorHomeService();

    public void setUserData(String nome, String cognome) {
        String fullName = nome + " " + cognome;
        operatorNameLabel.setText(fullName);
        Image img = operatorHomeService.getProfileImage();
        profileImage.setImage(img);
    }

    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            operatorNameLabel.setText(fullName);
            Image img = operatorHomeService.getProfileImage();
            profileImage.setImage(img);
        }
        profileImage.setOnMouseClicked(this::handleProfileClick);
        logoutMenuItem.setOnAction(e -> handleLogout());
        exitMenuItem.setOnAction(e -> handleExit());
    }

    private void handleProfileClick(MouseEvent event) {
        menuButton.show();
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }

    private void handleExit() {
        Platform.exit();
    }
}
