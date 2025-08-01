package businessLogic.controller;

/**
 * Controller for the Create Convoy screen.
 * Handles the selection of depot carriages, type/model filtering,
 * and the confirmation of convoy creation.
 */
import businessLogic.service.CreateConvoyService;
import domain.Carriage;
import domain.Station;
import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;


import java.util.List;

public class CreateConvoyController {
    @FXML private Label supervisorNameLabel;
    @FXML private MenuButton menuButton;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Label selectedStationLabel;
    @FXML private ComboBox<String> depotCarriageTypeComboBox;
    @FXML private ComboBox<String> depotCarriageModelComboBox;
    @FXML private TableView<Carriage> depotCarriageTableView;
    @FXML private TableColumn<Carriage, Boolean> selectCarriageColumn;
    @FXML private TableColumn<Carriage, Number> idCarriageColumn;
    @FXML private TableColumn<Carriage, String> modelCarriageColumn;
    @FXML private TableColumn<Carriage, Number> yearCarriageColumn;
    @FXML private TableColumn<Carriage, Number> capacityCarriageColumn;
    @FXML private Button confirmCreateConvoyButton;
    @FXML private Button cancelCreateConvoyButton;
    @FXML private Button backButton;
    @FXML private Label depotCarriageModelLabel;

    private final CreateConvoyService createConvoyService = new CreateConvoyService();
    private Station selectedStation;
    private final javafx.collections.ObservableList<Carriage> selectedDepotCarriages = FXCollections.observableArrayList();

    /**
     * Sets the current session for the controller, including staff and selected station.
     * Updates the UI with the selected station and supervisor name, and reloads depot types.
     * @param staff The staff member currently logged in.
     * @param station The station selected for convoy creation.
     */
    public void setSession(Staff staff, Station station) {
        this.selectedStation = station;
        selectedStationLabel.setText(station != null ? station.getLocation() : "");
        if (staff != null) supervisorNameLabel.setText(staff.getName() + " " + staff.getSurname());
        reloadDepotTypes();
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and prepares the table and combo boxes.
     */
    @FXML
    public void initialize() {
        // Header
        supervisorNameLabel.setOnMouseClicked(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        logoutMenuItem.setOnAction(_ -> businessLogic.controller.UserSession.getInstance().clear());
        exitMenuItem.setOnAction(_ -> javafx.application.Platform.exit());

        // ComboBox and TableView
        depotCarriageTypeComboBox.setOnAction(e -> onDepotTypeSelected());
        depotCarriageModelComboBox.setOnAction(e -> onDepotModelSelected());
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
                        this.setGraphic(null);
                        if (carriage != null) {
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
        backButton.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageConvoy.fxml"));
    }

    /**
     * Reloads the available depot carriage types for the selected station.
     * Clears selections and hides the table if no station is selected.
     */
    private void reloadDepotTypes() {
        if (selectedStation == null) return;
        List<String> types = createConvoyService.getAvailableDepotCarriageTypes(selectedStation.getIdStation());
        depotCarriageTypeComboBox.setItems(FXCollections.observableArrayList(types));
        depotCarriageTypeComboBox.getSelectionModel().clearSelection();
        depotCarriageTableView.setVisible(false);
        selectedDepotCarriages.clear();
    }

    /**
     * Handles the event when a depot carriage type is selected.
     * Updates the model combo box and table visibility based on the selected type.
     */
    private void onDepotTypeSelected() {
        if (selectedStation == null) {
            depotCarriageTableView.setVisible(false);
            selectedDepotCarriages.clear();
            depotCarriageModelComboBox.setVisible(false);
            depotCarriageModelComboBox.setManaged(false);
            depotCarriageModelLabel.setVisible(false);
            depotCarriageModelLabel.setManaged(false);
            return;
        }
        String type = depotCarriageTypeComboBox.getValue();
        if (type == null) {
            depotCarriageTableView.setVisible(false);
            selectedDepotCarriages.clear();
            depotCarriageModelComboBox.setVisible(false);
            depotCarriageModelComboBox.setManaged(false);
            depotCarriageModelLabel.setVisible(false);
            depotCarriageModelLabel.setManaged(false);
            return;
        }
        // Se il tipo è passeggeri, mostra e popola la comboBox dei modelli e la label
        if (type.equalsIgnoreCase("passeggeri")) {
            List<String> models = createConvoyService.getAvailableDepotCarriageModels(selectedStation.getIdStation(), type);
            depotCarriageModelComboBox.setItems(FXCollections.observableArrayList(models));
            depotCarriageModelComboBox.setVisible(true);
            depotCarriageModelComboBox.setManaged(true);
            depotCarriageModelComboBox.getSelectionModel().clearSelection();
            depotCarriageModelLabel.setVisible(true);
            depotCarriageModelLabel.setManaged(true);
            depotCarriageTableView.setVisible(false);
            selectedDepotCarriages.clear();
        } else {
            depotCarriageModelComboBox.setVisible(false);
            depotCarriageModelComboBox.setManaged(false);
            depotCarriageModelLabel.setVisible(false);
            depotCarriageModelLabel.setManaged(false);
            List<Carriage> available = createConvoyService.getAvailableDepotCarriages(selectedStation.getIdStation(), type);
            depotCarriageTableView.setItems(FXCollections.observableArrayList(available));
            depotCarriageTableView.setVisible(true);
            selectedDepotCarriages.clear();
        }
    }

    /**
     * Handles the event when a depot carriage model is selected.
     * Filters the available carriages in the table by the selected model.
     */
    private void onDepotModelSelected() {
        if (selectedStation == null) return;
        String type = depotCarriageTypeComboBox.getValue();
        String model = depotCarriageModelComboBox.getValue();
        if (type == null || model == null) {
            depotCarriageTableView.setVisible(false);
            selectedDepotCarriages.clear();
            return;
        }
        List<Carriage> available = createConvoyService.getAvailableDepotCarriages(selectedStation.getIdStation(), type)
            .stream().filter(c -> model.equals(c.getModel())).toList();
        depotCarriageTableView.setItems(FXCollections.observableArrayList(available));
        depotCarriageTableView.setVisible(true);
        selectedDepotCarriages.clear();
    }

    /**
     * Handles the confirmation of convoy creation.
     * Validates the selected carriages and calls the service to create the convoy.
     * Shows an error if validation fails or creation throws an exception.
     */
    private void handleConfirmCreateConvoy() {
        if (selectedDepotCarriages.isEmpty()) {
            showError("Seleziona almeno una vettura per creare il convoglio.");
            return;
        }
        String selectedType = null;
        String selectedModel = null;
        for (Carriage c : selectedDepotCarriages) {
            if (selectedType == null) {
                selectedType = c.getModelType();
                selectedModel = c.getModel();
            } else {
                if (!selectedType.equals(c.getModelType())) {
                    showError("Tutte le vetture devono essere dello stesso tipo per creare un convoglio.");
                    return;
                }
                // Se tipo passeggeri, controlla anche il modello
                if (selectedType.equalsIgnoreCase("passeggeri") && !selectedModel.equals(c.getModel())) {
                    showError("Per i passeggeri, tutte le vetture devono essere dello stesso modello.");
                    return;
                }
            }
        }
        try {
            createConvoyService.createConvoy(selectedDepotCarriages);
            goBackToManageConvoy();
        } catch (Exception e) {
            showError("Errore durante la creazione del convoglio: " + e.getMessage());
        }
    }

    /**
     * Navigates back to the Manage Convoy screen.
     */
    private void goBackToManageConvoy() {
        businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageConvoy.fxml");
    }

    /**
     * Displays an error dialog with the given message.
     * @param message The error message to display.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
