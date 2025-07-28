package businessLogic.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    private static SceneManager instance;
    private static Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while switching scene: " + fxmlPath, e);
        }
    }

    public void openCreateConvoyScene(domain.Staff staff, domain.Station station) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/businessLogic/fxml/CreateConvoy.fxml"));
            Parent root = loader.load();
            businessLogic.controller.CreateConvoyController controller = loader.getController();
            controller.setSession(staff, station);
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while opening CreateConvoy scene", e);
        }
    }

    public void openManageCarriagesScene(domain.Staff staff, domain.Station station, domain.ConvoyTableDTO convoy) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/businessLogic/fxml/ManageCarriages.fxml"));
            Parent root = loader.load();
            businessLogic.controller.ManageCarriagesController controller = loader.getController();
            controller.setSession(staff, station, convoy);
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while opening ManageCarriages scene", e);
        }
    }
}
