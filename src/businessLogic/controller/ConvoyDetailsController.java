package businessLogic.controller;

import businessLogic.service.ConvoyDetailsService;
import businessLogic.service.ConvoyDetailsService.StationRow;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;

import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


/**
 * Controller for the Convoy Details screen.
 * Handles the display of convoy information, carriages, stations,
 * and user actions related to convoy details.
 */
public class ConvoyDetailsController {
    @FXML private TableView<domain.Carriage> carriageTable;
    @FXML private TableColumn<domain.Carriage, Integer> carriageIdColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageModelColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageTypeColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageYearColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageCapacityColumn;
    @FXML private TableColumn<domain.Carriage, Void> notifyColumn;
    @FXML private Label convoyIdLabel;
    @FXML private Label lineNameLabel;
    @FXML private Label departureStationLabel;
    @FXML private Label departureTimeLabel;
    @FXML private Label arrivalStationLabel;
    @FXML private Label arrivalTimeLabel;
    @FXML private Label staffNameLabel;
    @FXML private TableView<StationRow> stationTable;
    @FXML private TableColumn<StationRow, String> stationNameColumn;
    @FXML private TableColumn<StationRow, String> arrivalTimeColumn;
    @FXML private TableColumn<StationRow, String> departureTimeColumn;
    @FXML private Label operatorNameLabel;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Button toggleCarriageTableButton;
    @FXML private Button toggleStationTableButton;

    private final ConvoyDetailsService convoyDetailsService = new ConvoyDetailsService();
    private AssignedConvoyInfo convoyInfo;
    private static AssignedConvoyInfo staticConvoyInfo;

    /**
     * Sets the static convoy info to be used when initializing the details view.
     * @param info The AssignedConvoyInfo object containing convoy details.
     */
    public static void setStaticConvoyInfo(AssignedConvoyInfo info) {
        staticConvoyInfo = info;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads convoy details if available.
     */
    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null && operatorNameLabel != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            operatorNameLabel.setText(fullName);
        }
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(_ -> handleLogout());
        }
        if (exitMenuItem != null) {
            exitMenuItem.setOnAction(_ -> handleExit());
        }

        carriageIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        carriageModelColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        carriageTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModelType()));
        carriageYearColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getYearProduced()).asObject());
        carriageCapacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()).asObject());

        if (stationNameColumn != null) {
            stationNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().stationName));
        }
        if (arrivalTimeColumn != null) {
            arrivalTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().arrivalTime));
        }
        if (departureTimeColumn != null) {
            departureTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().departureTime));
        }

        if (notifyColumn != null) {
            notifyColumn.setCellFactory(col -> new TableCell<domain.Carriage, Void>() {
                private final Button btnGuasto = new Button("Guasto tecnico");
                private final Button btnPulizie = new Button("Pulizie");
                private final HBox box = new HBox(10, btnGuasto, btnPulizie);
                {
                    btnGuasto.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");
                    btnPulizie.setStyle("-fx-background-color: #64b5f6; -fx-text-fill: white;");
                    btnGuasto.setFocusTraversable(false);
                    btnPulizie.setFocusTraversable(false);
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(box);
                    }
                }
            });
        }

        if (toggleCarriageTableButton != null && carriageTable != null) {
            toggleCarriageTableButton.setOnAction(e -> {
                boolean isVisible = carriageTable.isVisible();
                carriageTable.setVisible(!isVisible);
                carriageTable.setManaged(!isVisible);
                toggleCarriageTableButton.setText(isVisible ? "Mostra tabella convoglio" : "Nascondi tabella convoglio");
            });
        }
        if (toggleStationTableButton != null && stationTable != null) {
            toggleStationTableButton.setOnAction(e -> {
                boolean isVisible = stationTable.isVisible();
                stationTable.setVisible(!isVisible);
                stationTable.setManaged(!isVisible);
                toggleStationTableButton.setText(isVisible ? "Mostra dettagli corsa" : "Nascondi dettagli corsa");
            });
        }

        if (staticConvoyInfo != null) {
            setConvoyInfo(staticConvoyInfo);
            staticConvoyInfo = null;
        }

        if (operatorNameLabel != null) {
            operatorNameLabel.setCursor(Cursor.HAND);
            operatorNameLabel.setOnMouseClicked(event -> {
                SceneManager.getInstance().switchScene("/businessLogic/fxml/OperatorHome.fxml");
            });
        }
    }

    /**
     * Sets the convoy information to be displayed and populates the details.
     * @param info The AssignedConvoyInfo object containing convoy details.
     */
    public void setConvoyInfo(AssignedConvoyInfo info) {
        this.convoyInfo = info;
        populateDetails();
    }

    /**
     * Populates the UI fields with the details of the selected convoy.
     * Fetches data from the service and updates the labels and tables.
     */
    private void populateDetails() {
        ConvoyDetailsService.ConvoyDetailsDTO dto = convoyDetailsService.getConvoyDetailsDTO(convoyInfo);
        if (dto == null) {
            if (convoyIdLabel != null) convoyIdLabel.setText("Errore");
            return;
        }
        if (convoyIdLabel != null) convoyIdLabel.setText(dto.convoyId);
        if (lineNameLabel != null) lineNameLabel.setText(dto.lineName);
        if (staffNameLabel != null) staffNameLabel.setText(dto.staffName);
        if (departureStationLabel != null) departureStationLabel.setText(dto.departureStation);
        if (departureTimeLabel != null) departureTimeLabel.setText(dto.departureTime);
        if (arrivalStationLabel != null) arrivalStationLabel.setText(dto.arrivalStation);
        if (arrivalTimeLabel != null) arrivalTimeLabel.setText(dto.arrivalTime);
        if (carriageTable != null) carriageTable.setItems(FXCollections.observableArrayList(dto.carriages));
        if (stationTable != null) stationTable.setItems(FXCollections.observableArrayList(dto.stationRows));
    }

    /**
     * Handles the logout action, clearing the user session and returning to the login screen.
     */
    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }
    /**
     * Handles the exit action, closing the application.
     */
    private void handleExit() {
        javafx.application.Platform.exit();
    }
}
