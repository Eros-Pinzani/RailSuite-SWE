package businessLogic.controller;

import domain.DTO.ConvoyTableDTO;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

public class ChangeConvoyPopupController {
    @FXML private TextField filterField;
    @FXML private TableView<ConvoyTableDTO> convoyTable;
    @FXML private TableColumn<ConvoyTableDTO, Number> idColumn;
    @FXML private TableColumn<ConvoyTableDTO, String> modelColumn;
    @FXML private TableColumn<ConvoyTableDTO, String> modelTypeColumn;
    @FXML private TableColumn<ConvoyTableDTO, Number> carriageCountColumn;
    @FXML private TableColumn<ConvoyTableDTO, Number> capacityColumn;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private FilteredList<ConvoyTableDTO> filteredData;
    private Callback<ConvoyTableDTO, Void> confirmCallback;

    public void setConvoys(ObservableList<ConvoyTableDTO> convoys) {
        filteredData = new FilteredList<>(convoys, convoy -> true);
        convoyTable.setItems(filteredData);
    }

    public void setConfirmCallback(Callback<ConvoyTableDTO, Void> callback) {
        this.confirmCallback = callback;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdConvoy()));
        modelColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        modelTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModelType()));
        carriageCountColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCarriageCount()));
        capacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));

        filterField.textProperty().addListener((observable, oldVal, newVal) -> {
            filteredData.setPredicate(convoy -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return String.valueOf(convoy.getIdConvoy()).contains(lower)
                    || convoy.getModel().toLowerCase().contains(lower)
                    || convoy.getModelType().toLowerCase().contains(lower)
                    || String.valueOf(convoy.getCarriageCount()).contains(lower)
                    || String.valueOf(convoy.getCapacity()).contains(lower);
            });
        });

        confirmButton.setOnAction(event -> {
            ConvoyTableDTO selected = convoyTable.getSelectionModel().getSelectedItem();
            if (selected != null && confirmCallback != null) {
                confirmCallback.call(selected);
                confirmButton.getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attenzione");
                alert.setHeaderText("Selezione mancante");
                if (selected == null) {
                    alert.setContentText("Seleziona un convoglio dalla tabella prima di confermare.");
                } else {
                    alert.setContentText("Errore interno: callback non impostato.");
                }
                alert.showAndWait();
            }
        });
        cancelButton.setOnAction(event -> {
            if (confirmCallback != null) {
                confirmCallback.call(null);
            }
            cancelButton.getScene().getWindow().hide();
        });
    }
}
