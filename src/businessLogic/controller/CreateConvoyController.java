package businessLogic.controller;

import businessLogic.service.ConvoyService;
import businessLogic.service.CreateConvoyService;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.Station;
import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

import java.util.List;

public class CreateConvoyController {
    @FXML private Label supervisorNameLabel;
    @FXML private MenuButton menuButton;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Label selectedStationLabel;
    @FXML private ComboBox<String> depotCarriageTypeComboBox;
    @FXML private TableView<Carriage> depotCarriageTableView;
    @FXML private TableColumn<Carriage, Boolean> selectCarriageColumn;
    @FXML private TableColumn<Carriage, Number> idCarriageColumn;
    @FXML private TableColumn<Carriage, String> modelCarriageColumn;
    @FXML private TableColumn<Carriage, Number> yearCarriageColumn;
    @FXML private TableColumn<Carriage, Number> capacityCarriageColumn;
    @FXML private Button confirmCreateConvoyButton;
    @FXML private Button cancelCreateConvoyButton;
    @FXML private Button backButton;

    private final ConvoyService convoyService = new ConvoyService();
    private final CreateConvoyService createConvoyService = new CreateConvoyService();
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private Station selectedStation;
    private final javafx.collections.ObservableList<Carriage> selectedDepotCarriages = FXCollections.observableArrayList();

    public void setSession(Staff staff, Station station) {
        this.selectedStation = station;
        selectedStationLabel.setText(station != null ? station.getLocation() : "");
        if (staff != null) supervisorNameLabel.setText(staff.getName() + " " + staff.getSurname());
        reloadDepotTypes();
    }

    @FXML
    public void initialize() {
        // Header
        supervisorNameLabel.setOnMouseClicked(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        logoutMenuItem.setOnAction(_ -> businessLogic.controller.UserSession.getInstance().clear());
        exitMenuItem.setOnAction(_ -> javafx.application.Platform.exit());

        // ComboBox tipi disponibili
        depotCarriageTypeComboBox.setOnAction(e -> onDepotTypeSelected());
        // Colonne tabella
        selectCarriageColumn.setCellValueFactory(cellData -> {
            Carriage carriage = cellData.getValue();
            return new javafx.beans.property.SimpleBooleanProperty(selectedDepotCarriages.contains(carriage));
        });
        selectCarriageColumn.setCellFactory(tc -> new CheckBoxTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    TableRow<Carriage> row = getTableRow();
                    if (row != null) {
                        Carriage carriage = row.getItem();
                        if (carriage != null) {
                            // Usa il valore del checkbox per la selezione
                            this.setGraphic(null);
                            CheckBox checkBox = new CheckBox();
                            checkBox.setSelected(selectedDepotCarriages.contains(carriage));
                            checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                                if (isNowSelected) {
                                    if (!selectedDepotCarriages.contains(carriage)) selectedDepotCarriages.add(carriage);
                                } else {
                                    selectedDepotCarriages.remove(carriage);
                                }
                            });
                            setGraphic(checkBox);
                        } else {
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        });
        idCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        modelCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        yearCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getYearProduced()));
        capacityCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));
        depotCarriageTableView.setVisible(false);
        confirmCreateConvoyButton.setOnAction(e -> handleConfirmCreateConvoy());
        cancelCreateConvoyButton.setOnAction(e -> goBackToManageConvoy());
        backButton.setOnAction(e -> goBackToManageConvoy());
    }

    private void reloadDepotTypes() {
        if (selectedStation == null) return;
        List<String> types = createConvoyService.getAvailableDepotCarriageTypes(selectedStation.getIdStation());
        depotCarriageTypeComboBox.setItems(FXCollections.observableArrayList(types));
        depotCarriageTypeComboBox.getSelectionModel().clearSelection();
        depotCarriageTableView.setVisible(false);
        selectedDepotCarriages.clear();
    }

    private void onDepotTypeSelected() {
        if (selectedStation == null) {
            depotCarriageTableView.setVisible(false);
            selectedDepotCarriages.clear();
            return;
        }
        String type = depotCarriageTypeComboBox.getValue();
        List<Carriage> available = createConvoyService.getAvailableDepotCarriages(selectedStation.getIdStation(), type);
        depotCarriageTableView.setItems(FXCollections.observableArrayList(available));
        depotCarriageTableView.setVisible(true);
        selectedDepotCarriages.clear();
    }

    private void handleConfirmCreateConvoy() {
        if (selectedDepotCarriages.isEmpty()) {
            showError("Seleziona almeno una vettura per creare il convoglio.");
            return;
        }
        // Controllo che tutte le carriage selezionate abbiano lo stesso model_type
        String selectedType = null;
        for (Carriage c : selectedDepotCarriages) {
            if (selectedType == null) {
                selectedType = c.getModelType();
            } else if (!selectedType.equals(c.getModelType())) {
                showError("Tutte le vetture devono essere dello stesso tipo per creare un convoglio.");
                return;
            }
        }
        try {
            createConvoyService.createConvoy(selectedDepotCarriages);
            goBackToManageConvoy();
        } catch (Exception e) {
            showError("Errore durante la creazione del convoglio: " + e.getMessage());
        }
    }

    private void goBackToManageConvoy() {
        businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageConvoy.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
