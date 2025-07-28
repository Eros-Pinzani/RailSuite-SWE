package businessLogic.controller;

import businessLogic.service.ConvoyService;
import businessLogic.service.ManageCarriagesService;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.ConvoyTableDTO;
import domain.Station;
import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class ManageCarriagesController {
    @FXML private Label supervisorNameLabel;
    @FXML private MenuButton menuButton;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Label selectedStationLabel;
    @FXML private Label selectedConvoyLabel;
    @FXML private TableView<domain.CarriageDepotDTO> manageCarriageTableView;
    @FXML private TableColumn<domain.CarriageDepotDTO, Number> idManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, String> modelManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, Number> yearManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, Number> capacityManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, String> statusManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, String> exitTimeManageCarriageColumn;
    @FXML private TableColumn<domain.CarriageDepotDTO, Void> removeManageCarriageColumn;
    @FXML private Button closeManageCarriagesButton;
    @FXML private Button openAddCarriageDialogButton;

    private final ConvoyService convoyService = new ConvoyService();
    private final ManageCarriagesService manageCarriagesService = new ManageCarriagesService();
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private Station selectedStation;
    private ConvoyTableDTO selectedConvoy;
    private final javafx.collections.ObservableList<domain.CarriageDepotDTO> manageCarriageList = FXCollections.observableArrayList();

    public void setSession(Staff staff, Station station, ConvoyTableDTO convoy) {
        this.selectedStation = station;
        this.selectedConvoy = convoy;
        selectedStationLabel.setText(station != null ? station.getLocation() : "");
        selectedConvoyLabel.setText(convoy != null ? String.valueOf(convoy.getIdConvoy()) : "");
        if (staff != null) supervisorNameLabel.setText(staff.getName() + " " + staff.getSurname());
        loadCarriages();
    }

    @FXML
    public void initialize() {
        supervisorNameLabel.setOnMouseClicked(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        logoutMenuItem.setOnAction(_ -> businessLogic.controller.UserSession.getInstance().clear());
        exitMenuItem.setOnAction(_ -> javafx.application.Platform.exit());
        idManageCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdCarriage()));
        modelManageCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        yearManageCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getYearProduced()));
        capacityManageCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));
        statusManageCarriageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDepotStatus()));
        exitTimeManageCarriageColumn.setCellValueFactory(data -> {
            String status = data.getValue().getDepotStatus();
            if ("CLEANING".equals(status) || "MAINTENANCE".equals(status)) {
                var ts = data.getValue().getTimeExited();
                return new javafx.beans.property.SimpleStringProperty(ts != null ? ts.toString() : "-");
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        removeManageCarriageColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Rimuovi");
            { btn.setOnAction(e -> removeCarriageFromConvoy(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        openAddCarriageDialogButton.setOnAction(e -> openAddCarriagesDialog());
        closeManageCarriagesButton.setOnAction(e -> goBackToManageConvoy());
    }

    private void loadCarriages() {
        if (selectedConvoy == null) return;
        manageCarriageList.setAll(manageCarriagesService.getCarriagesWithDepotStatusByConvoy(selectedConvoy.getIdConvoy()));
        manageCarriageTableView.setItems(manageCarriageList);
    }

    private void removeCarriageFromConvoy(domain.CarriageDepotDTO dto) {
        try {
            manageCarriagesService.removeCarriageFromConvoy(dto.getIdCarriage(), selectedConvoy.getIdConvoy());
            loadCarriages();
        } catch (Exception e) {
            showError("Errore durante la rimozione della vettura: " + e.getMessage());
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

    private void openAddCarriagesDialog() {
        if (selectedStation == null || selectedConvoy == null) return;
        String type = selectedConvoy.getType();
        List<Carriage> available = manageCarriagesService.getAvailableDepotCarriages(selectedStation.getIdStation(), type);
        Dialog<List<Carriage>> dialog = new Dialog<>();
        dialog.setTitle("Seleziona vetture da aggiungere");
        dialog.setHeaderText("Seleziona una o più vetture disponibili da aggiungere al convoglio");
        ButtonType confermaBtn = new ButtonType("Aggiungi selezionate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaBtn, ButtonType.CANCEL);

        TableView<Carriage> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn<Carriage, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        TableColumn<Carriage, String> modelCol = new TableColumn<>("Modello");
        modelCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        TableColumn<Carriage, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModelType()));
        TableColumn<Carriage, Number> yearCol = new TableColumn<>("Anno");
        yearCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getYearProduced()));
        TableColumn<Carriage, Number> capCol = new TableColumn<>("Capacità");
        capCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));
        table.getColumns().addAll(idCol, modelCol, typeCol, yearCol, capCol);
        table.setItems(FXCollections.observableArrayList(available));
        dialog.getDialogPane().setContent(table);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaBtn) {
                return table.getSelectionModel().getSelectedItems();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selected -> {
            if (!selected.isEmpty()) {
                for (Carriage c : selected) {
                    manageCarriagesService.addCarriageToConvoy(c.getId(), selectedConvoy.getIdConvoy());
                }
                loadCarriages();
            }
        });
    }
}
