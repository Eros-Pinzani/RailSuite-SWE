package businessLogic.controller;

/**
 * Controller for the Manage Convoy screen.
 * Handles the display, creation, and removal of convoys for a selected station.
 */
import businessLogic.service.ConvoyService;
import domain.ConvoyTableDTO;
import domain.Station;
import dao.StationDao;
import domain.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.util.List;

public class ManageConvoyController {
    @FXML private Button createConvoyButton;
    @FXML private Button deleteConvoyButton;
    @FXML private ComboBox<Station> stationComboBox;
    @FXML private TableView<ConvoyTableDTO> convoyTableView;
    @FXML private TableColumn<ConvoyTableDTO, Number> convoyIdColumn;
    @FXML private TableColumn<ConvoyTableDTO, String> typeColumn;
    @FXML private TableColumn<ConvoyTableDTO, String> statusColumn;
    @FXML private TableColumn<ConvoyTableDTO, Number> carriageCountColumn;
    @FXML private Button manageCarriagesButton;
    @FXML private javafx.scene.control.Label supervisorNameLabel;
    @FXML private javafx.scene.control.MenuItem logoutMenuItem;
    @FXML private javafx.scene.control.MenuItem exitMenuItem;
    @FXML private javafx.scene.control.MenuButton menuButton;
    @FXML private Button backButton;

    private final ConvoyService convoyService = new ConvoyService();

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads stations and convoys.
     */
    @FXML
    public void initialize() {
        header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        backButton.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));

        // Imposta la visualizzazione del nome stazione nel ComboBox
        stationComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Station item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getLocation());
            }
        });
        stationComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Station item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getLocation());
            }
        });

        // Carica le stazioni head nella ComboBox
        try {
            List<Station> stations = StationDao.of().findAllHeadStations();
            stationComboBox.setItems(FXCollections.observableArrayList(stations));
        } catch (Exception e) {
            stationComboBox.setItems(FXCollections.observableArrayList());
        }
        stationComboBox.setOnAction(e -> onStationSelected());

        // Configura colonne tabella
        convoyIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdConvoy()));
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        carriageCountColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCarriageCount()));

        // Gestione selezione tabella
        convoyTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            manageCarriagesButton.setDisable(!selected);
            deleteConvoyButton.setDisable(!selected);
        });

        // Pulsanti
        manageCarriagesButton.setOnAction(e -> {
            depotAvailabilityObserver.run();
            openManageCarriagesScene();
        });
        createConvoyButton.setOnAction(e -> openCreateConvoyScene());
        deleteConvoyButton.setOnAction(e -> handleDeleteConvoy());

        // Disabilita il pulsante "Crea Convoglio" se non è selezionata una stazione
        createConvoyButton.setDisable(true);
        stationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            createConvoyButton.setDisable(newVal == null);
        });
    }

    /**
     * Sets up the supervisor header, including name and menu actions.
     * @param supervisorNameLabel The label for the supervisor's name.
     * @param logoutMenuItem The logout menu item.
     * @param exitMenuItem The exit menu item.
     */
    static void header(Label supervisorNameLabel, MenuItem logoutMenuItem, MenuItem exitMenuItem) {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            supervisorNameLabel.setText(fullName);
        }
        supervisorNameLabel.setOnMouseClicked(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        logoutMenuItem.setOnAction(_ -> UserSession.getInstance().clear());
        exitMenuItem.setOnAction(_ -> javafx.application.Platform.exit());
    }

    /**
     * Handles the event when a station is selected from the combo box.
     * Loads and displays the convoys for the selected station.
     */
    private void onStationSelected() {
        Station selected = stationComboBox.getValue();
        if (selected == null) {
            convoyTableView.setItems(FXCollections.observableArrayList());
            return;
        }
        // Query e popolamento tabella convogli per la stazione selezionata
        List<ConvoyTableDTO> convoyList = convoyService.getConvoyTableByStation(selected.getIdStation());
        convoyTableView.setItems(FXCollections.observableArrayList(convoyList));
    }

    /**
     * Opens the Create Convoy screen for the selected station and staff.
     * Also updates depot carriage availability before opening.
     */
    private void openCreateConvoyScene() {
        depotAvailabilityObserver.run();
        Station selectedStation = stationComboBox.getValue();
        Staff staff = UserSession.getInstance().getStaff();
        businessLogic.controller.SceneManager.getInstance().openCreateConvoyScene(staff, selectedStation);
    }

    /**
     * Opens the Manage Carriages screen for the selected station and convoy.
     * Also updates depot carriage availability before opening.
     */
    private void openManageCarriagesScene() {
        depotAvailabilityObserver.run();
        Station selectedStation = stationComboBox.getValue();
        ConvoyTableDTO selectedConvoy = convoyTableView.getSelectionModel().getSelectedItem();
        Staff staff = UserSession.getInstance().getStaff();
        if (selectedStation != null && selectedConvoy != null) {
            businessLogic.controller.SceneManager.getInstance().openManageCarriagesScene(staff, selectedStation, selectedConvoy);
        }
    }

    /**
     * Handles the deletion of the selected convoy, if allowed by status.
     * Shows an error if the operation is not permitted or fails.
     */
    private void handleDeleteConvoy() {
        ConvoyTableDTO selected = convoyTableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String status = selected.getStatus();
        if ("DEPOT".equals(status) || "WAITING".equals(status)) {
            try {
                Station selectedStation = stationComboBox.getValue();
                if (selectedStation == null) {
                    showError("Seleziona una stazione per eliminare il convoglio.");
                    return;
                }
                convoyService.deleteConvoy(selected.getIdConvoy(), selectedStation.getIdStation());
                onStationSelected();
            } catch (Exception e) {
                showError("Errore durante l'eliminazione del convoglio: " + e.getMessage());
            }
        } else {
            showError("Il convoglio può essere eliminato solo se in stato DEPOT o WAITING.");
        }
    }

    /**
     * Observer that updates the availability of depot carriages for the selected station.
     * Called before opening certain screens or after changes.
     */
    private final Runnable depotAvailabilityObserver = () -> {
        Station selectedStation = stationComboBox.getValue();
        if (selectedStation != null) {
            try {
                // Chiama il service/dao per aggiornare la disponibilità delle vetture nel deposito associato
                convoyService.updateDepotCarriageAvailability(selectedStation.getIdStation());
            } catch (Exception e) {
                showError("Errore durante l'aggiornamento della disponibilità delle vetture in deposito: " + e.getMessage());
            }
        }
    };

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
