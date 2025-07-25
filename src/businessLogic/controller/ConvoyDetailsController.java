package businessLogic.controller;

import businessLogic.service.ConvoyDetailsService;
import businessLogic.service.ConvoyDetailsService.StationRow;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import domain.Carriage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConvoyDetailsController {
    @FXML private TextField convoyIdField;
    @FXML private TableView<domain.Carriage> carriageTable;
    @FXML private TableColumn<domain.Carriage, Integer> carriageIdColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageModelColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageTypeColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageYearColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageCapacityColumn;
    @FXML private TextField newConvoyCarriageIdsField;
    @FXML private Button loadButton;
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

    private final ConvoyDetailsService convoyDetailsService = new ConvoyDetailsService();
    private AssignedConvoyInfo convoyInfo;
    private static AssignedConvoyInfo staticConvoyInfo;

    public static void setStaticConvoyInfo(AssignedConvoyInfo info) {
        staticConvoyInfo = info;
    }

    @FXML
    public void initialize() {
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

    @FXML
    private void onLoadConvoy() {
        int convoyId;
        try {
            convoyId = Integer.parseInt(convoyIdField.getText());
        } catch (NumberFormatException e) {
            showError("ID convoglio non valido.");
            return;
        }
        if (!convoyDetailsService.convoyExists(convoyId)) {
            showError("Il convoglio con ID " + convoyId + " non esiste.");
            carriageTable.setItems(FXCollections.observableArrayList());
            return;
        }
        if (!convoyDetailsService.convoyHasCarriages(convoyId)) {
            showInfo("Il convoglio con ID " + convoyId + " non ha carrozze associate.");
            carriageTable.setItems(FXCollections.observableArrayList());
            return;
        }
        javafx.collections.ObservableList<domain.Carriage> carriages = FXCollections.observableArrayList(convoyDetailsService.getCarriagesForConvoy(convoyId));
        carriageTable.setItems(carriages);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
