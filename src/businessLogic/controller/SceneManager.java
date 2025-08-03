package businessLogic.controller;

/**
 * Utility class for managing scene transitions in the JavaFX application.
 * Provides methods to switch between different FXML screens.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.DTO.ConvoyTableDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    private static SceneManager instance;
    private static Stage primaryStage;

    private SceneManager() {}

    /**
     * Returns the singleton instance of SceneManager.
     * @return the SceneManager instance
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Sets the primary stage for the application.
     * @param stage the main JavaFX stage
     */
    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switches the current scene to the one specified by the FXML path.
     * @param fxmlPath the path to the FXML file
     */
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

    /**
     * Opens the Create Convoy scene and sets the session for the controller.
     * @param staff the staff member
     * @param station the selected station
     */
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

    /**
     * Opens the Manage Carriages scene and sets the session for the controller.
     * @param staff the staff member
     * @param station the selected station
     * @param convoy the selected convoy
     */
    public void openManageCarriagesScene(domain.Staff staff, domain.Station station, ConvoyTableDTO convoy) {
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
