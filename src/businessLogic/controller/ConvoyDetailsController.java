package businessLogic.controller;

import businessLogic.service.ConvoyService;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.Convoy;
import domain.Line;
import domain.Run;
import domain.Station;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ConvoyDetailsController {
    @FXML private Button addCarriageButton;
    @FXML private Button removeCarriageButton;
    @FXML private Button createConvoyButton;
    @FXML private TextField convoyIdField;
    @FXML private TableView<Carriage> carriageTable;
    @FXML private TableColumn<Carriage, Integer> carriageIdColumn;
    @FXML private TableColumn<Carriage, String> carriageModelColumn;
    @FXML private TableColumn<Carriage, String> carriageTypeColumn;
    @FXML private TableColumn<Carriage, Integer> carriageYearColumn;
    @FXML private TableColumn<Carriage, Integer> carriageCapacityColumn;
    @FXML private TextField newConvoyCarriageIdsField;
    @FXML private Button loadButton;
    @FXML private Label convoyIdLabel;
    @FXML private Label lineNameLabel;
    @FXML private Label departureStationLabel;
    @FXML private Label departureTimeLabel;
    @FXML private Label arrivalStationLabel;
    @FXML private Label arrivalTimeLabel;

    private final ConvoyService convoyService = new ConvoyService();

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
        if (convoyInfo == null) return;
        try {
            RailSuiteFacade facade = new RailSuiteFacade();
            convoyIdLabel.setText(String.valueOf(convoyInfo.convoyId));
            departureStationLabel.setText(convoyInfo.departureStation);
            departureTimeLabel.setText(convoyInfo.departureTime);
            arrivalStationLabel.setText(convoyInfo.arrivalStation);
            arrivalTimeLabel.setText(convoyInfo.arrivalTime);
            // Recupera la corsa (run) associata
            List<Run> runs = facade.selectRunsByConvoy(convoyInfo.convoyId);
            if (!runs.isEmpty()) {
                Run run = runs.get(0);
                Line line = facade.findLineById(run.getIdLine());
                if (line != null) {
                    lineNameLabel.setText(line.getName());
                } else {
                    lineNameLabel.setText("");
                }
            } else {
                lineNameLabel.setText("");
            }
            // Carrozze
            List<Carriage> carriages = facade.selectCarriagesByConvoyId(convoyInfo.convoyId);
            ObservableList<Carriage> obs = FXCollections.observableArrayList(carriages);
            carriageTable.setItems(obs);
        } catch (Exception e) {
            convoyIdLabel.setText("Errore");
        }
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
        if (!convoyService.convoyExists(convoyId)) {
            showError("Il convoglio con ID " + convoyId + " non esiste.");
            carriageTable.setItems(FXCollections.observableArrayList());
            return;
        }
        if (!convoyService.convoyHasCarriages(convoyId)) {
            showInfo("Il convoglio con ID " + convoyId + " non ha carrozze associate.");
            carriageTable.setItems(FXCollections.observableArrayList());
            return;
        }
        ObservableList<Carriage> carriages = FXCollections.observableArrayList(convoyService.getCarriagesForConvoy(convoyId));
        carriageTable.setItems(carriages);
    }

    @FXML
    private void onAddCarriage() {
        int convoyId;
        try {
            convoyId = Integer.parseInt(convoyIdField.getText());
        } catch (NumberFormatException e) {
            showError("ID convoglio non valido.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Inserisci l'ID della carrozza da aggiungere:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                int carriageId = Integer.parseInt(input);
                boolean success;
                try {
                    success = convoyService.addCarriageToConvoy(convoyId, carriageId);
                } catch (IllegalStateException e) {
                    showError(e.getMessage()); // messaggio in italiano
                    return;
                }
                if (success) onLoadConvoy();
                else showError("Impossibile aggiungere la carrozza.");
            } catch (NumberFormatException e) {
                showError("ID carrozza non valido.");
            }
        });
    }

    @FXML
    private void onRemoveCarriage() {
        int convoyId;
        try {
            convoyId = Integer.parseInt(convoyIdField.getText());
        } catch (NumberFormatException e) {
            showError("ID convoglio non valido.");
            return;
        }
        Carriage selected = carriageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean success = convoyService.removeCarriageFromConvoy(convoyId, selected.getId());
            if (success) onLoadConvoy();
            else showError("Impossibile rimuovere la carrozza.");
        } else {
            showError("Seleziona una carrozza dalla tabella.");
        }
    }

    @FXML
    private void onCreateConvoy() {
        String idsText = newConvoyCarriageIdsField.getText();
        java.util.List<Carriage> carriages = new java.util.ArrayList<>();
        try {
            java.util.List<Integer> ids = convoyService.parseCarriageIds(idsText);
            for (Integer carriageId : ids) {
                Carriage carriage = convoyService.selectCarriage(carriageId);
                if (carriage != null) {
                    carriages.add(carriage);
                } else {
                    showError("Carrozza con ID " + carriageId + " non trovata.");
                    return;
                }
            }
            domain.Convoy nuovoConvoglio;
            try {
                nuovoConvoglio = convoyService.createConvoy(carriages);
            } catch (IllegalArgumentException e) {
                showError(e.getMessage()); // messaggio in italiano
                return;
            }
            if (nuovoConvoglio != null) {
                showInfo("Convoglio creato con ID: " + nuovoConvoglio.getId());
                convoyIdField.setText(String.valueOf(nuovoConvoglio.getId()));
                onLoadConvoy();
            } else {
                showError("Impossibile creare il convoglio.");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Errore nella creazione del convoglio.");
        }
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
