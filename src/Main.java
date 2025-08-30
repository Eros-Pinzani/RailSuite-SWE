import businessLogic.controller.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Aggiorna lo stato delle carrozze in deposito all'avvio
        new businessLogic.service.ManageRunService().updateCarriageDepotStatuses();
        SceneManager.getInstance().setStage(primaryStage);
        var resource = getClass().getResource("/businessLogic/fxml/LogIn.fxml");
        if (resource == null) {
            throw new IllegalStateException("LogIn.fxml resource not found");
        }
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RailSuite");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
