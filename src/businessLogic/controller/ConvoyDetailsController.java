package businessLogic.controller;

import businessLogic.service.ConvoyDetailsService;
import businessLogic.service.ConvoyDetailsService.StationRow;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;

import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;


public class ConvoyDetailsController {
    @FXML private TableView<domain.Carriage> carriageTable;
    @FXML private TableColumn<domain.Carriage, Integer> carriageIdColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageModelColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageTypeColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageYearColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageCapacityColumn;
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

    private final ConvoyDetailsService convoyDetailsService = new ConvoyDetailsService();
    private AssignedConvoyInfo convoyInfo;
    private static AssignedConvoyInfo staticConvoyInfo;

    public static void setStaticConvoyInfo(AssignedConvoyInfo info) {
        staticConvoyInfo = info;
    }

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

    public void setConvoyInfo(AssignedConvoyInfo info) {
        this.convoyInfo = info;
        populateDetails();
    }

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

    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }
    private void handleExit() {
        javafx.application.Platform.exit();
    }
}
