package businessLogic.controller;

import businessLogic.RailSuiteFacade;
import domain.Convoy;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.collections.ObservableList;
import domain.Carriage;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import businessLogic.service.ConvoyEditPopupService;
import javafx.collections.FXCollections;

import java.util.logging.Logger;

public class ConvoyEditPopupController implements Initializable {
    @FXML
    private TableView<Carriage> carriageTable;
    @FXML
    private TableColumn<Carriage, Integer> idColumn;
    @FXML
    private TableColumn<Carriage, String> modelColumn;
    @FXML
    private TableColumn<Carriage, String> typeColumn;
    @FXML
    private TableColumn<Carriage, Integer> capacityColumn;
    @FXML
    private Button removeCarriageButton;
    @FXML
    private Button addCarriageButton;
    @FXML
    private Button confirmButton;

    // Lista delle carrozze visualizzate
    private Convoy convoy;
    private final ConvoyEditPopupService service = new ConvoyEditPopupService();
    private static final Logger logger = Logger.getLogger(ConvoyEditPopupController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModelType()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        carriageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean enable = newSel != null && carriageTable.getSelectionModel().getSelectedItems().size() == 1 && convoy != null && convoy.convoySize() > 1;
            removeCarriageButton.setDisable(!enable);
        });
        removeCarriageButton.setDisable(true);
    }

    public void setConvoy(Convoy convoy) {
        this.convoy = convoy;
        if (convoy != null && convoy.getCarriages() != null) {
            carriageTable.setItems(FXCollections.observableArrayList(convoy.getCarriages()));
        } else {
            carriageTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void removeCarriageEvent() {
        if (convoy == null) {
            PopupManager.openPopup(
                    "Carriage removal error",
                    null,
                    null,
                    "No convoy selected.",
                    null
            );
            return;
        }
        Carriage selectedCarriage = carriageTable.getSelectionModel().getSelectedItem();
        if (convoy.convoySize() <= 1) {
            PopupManager.openPopup(
                    "Operation not allowed",
                    null,
                    null,
                    "You cannot remove the last carriage from the convoy.",
                    null
            );
            return;
        }
        if (selectedCarriage != null) {
            try {
                service.removeCarriageFromConvoy(convoy, selectedCarriage);
                carriageTable.getItems().remove(selectedCarriage);
            } catch (Exception e) {
                logger.severe("Error while removing carriage: " + e.getMessage());
                PopupManager.openPopup(
                        "Carriage removal error",
                        null,
                        null,
                        "Error while removing carriage: " + e.getMessage(),
                        null
                );
            }
        }
    }

    // TODO: Metodo per gestire l'aggiunta di una carrozza
    @FXML
    private void addCarriageEvent() {
        if (convoy == null) {
            PopupManager.openPopup(
                    "Carriage addition error",
                    null,
                    null,
                    "No convoy selected.",
                    null
            );
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/businessLogic/fxml/CarriageSelectionPopup.fxml"));
            Parent root = loader.load();
            CarriageSelectionPopupController selectionController = loader.getController();
            selectionController.setConvoy(convoy);
            Stage popupStage = new Stage();
            popupStage.setTitle("Seleziona carrozze da aggiungere");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
            if (selectionController.getSelectedCarriages() != null && !selectionController.getSelectedCarriages().isEmpty()) {
                service.addCarriagesToConvoy(convoy, selectionController.getSelectedCarriages());
                for (Carriage c : selectionController.getSelectedCarriages()) {
                    carriageTable.getItems().add(c);
                }
            }
        } catch (Exception e) {
            logger.severe("Error while adding carriages: " + e.getMessage());
            PopupManager.openPopup(
                "Carriage addition error",
                null,
                null,
                "Error while adding carriages: " + e.getMessage(),
                null
            );
        }
    }

    @FXML
    private void confirmEvent() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
