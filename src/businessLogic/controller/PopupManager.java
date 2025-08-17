package businessLogic.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import domain.DTO.ConvoyTableDTO;
import javafx.collections.ObservableList;
import businessLogic.controller.ChangeConvoyPopupController;

public class PopupManager {
    /**
     * Opens a fixed-size popup window displaying either an FXML scene, a text message, or any JavaFX Node.
     * If fxmlPath is provided, loads the FXML. Otherwise, uses the provided Node or text.
     * @param title window title
     * @param header optional header text (can be null)
     * @param contentNode optional Node to display (can be null)
     * @param contentText optional text to display (can be null)
     * @param fxmlPath optional FXML path (can be null)
     */
    public static void openPopup(String title, String header, Node contentNode, String contentText, String fxmlPath) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        VBox vbox = new VBox();
        vbox.setStyle("-fx-padding: 20;");
        if (header != null && !header.isBlank()) {
            Label headerLabel = new Label(header);
            headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; margin-bottom: 10px;");
            vbox.getChildren().add(headerLabel);
        }
        if (fxmlPath != null && !fxmlPath.isBlank()) {
            try {
                FXMLLoader loader = new FXMLLoader(PopupManager.class.getResource(fxmlPath));
                Parent root = loader.load();
                vbox.getChildren().add(root);
            } catch (Exception e) {
                Label errorLabel = new Label("Error loading FXML: " + fxmlPath);
                vbox.getChildren().add(errorLabel);
            }
        } else if (contentNode != null) {
            vbox.getChildren().add(contentNode);
        } else if (contentText != null && !contentText.isBlank()) {
            TextArea textArea = new TextArea(contentText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(660, 380);
            vbox.getChildren().add(textArea);
        }
        Scene scene = new Scene(vbox, 700, 500);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setResizable(false);
        popupStage.centerOnScreen();
        popupStage.showAndWait();
    }

    /**
     * Apre un popup FXML e restituisce il controller associato.
     */
    public static <T> T openPopupWithController(String title, String header, String fxmlPath) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        VBox vbox = new VBox();
        vbox.setStyle("-fx-padding: 20;");
        if (header != null && !header.isBlank()) {
            Label headerLabel = new Label(header);
            headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; margin-bottom: 10px;");
            vbox.getChildren().add(headerLabel);
        }
        T controller = null;
        if (fxmlPath != null && !fxmlPath.isBlank()) {
            try {
                FXMLLoader loader = new FXMLLoader(PopupManager.class.getResource(fxmlPath));
                Parent root = loader.load();
                controller = loader.getController();
                vbox.getChildren().add(root);
            } catch (Exception e) {
                Label errorLabel = new Label("Error loading FXML: " + fxmlPath);
                vbox.getChildren().add(errorLabel);
            }
        }
        Scene scene = new Scene(vbox, 700, 500);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setResizable(false);
        popupStage.centerOnScreen();
        popupStage.showAndWait();
        return controller;
    }

    /**
     * Centralizza apertura popup cambio convoglio con passaggio lista e callback.
     */
    public static void showChangeConvoyPopup(ObservableList<ConvoyTableDTO> convoysAvailable, String title, String header, javafx.util.Callback<ConvoyTableDTO, Void> onConfirm) {
        ChangeConvoyPopupController popupController = openPopupWithController(
            title,
            header,
            "/businessLogic/fxml/ChangeConvoyPopup.fxml"
        );
        if (popupController != null) {
            popupController.setConvoys(convoysAvailable);
            popupController.setConfirmCallback(onConfirm);
        }
    }
}
