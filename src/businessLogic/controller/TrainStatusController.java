package businessLogic.controller;

import businessLogic.service.LineService;
import businessLogic.service.ConvoyService;
import businessLogic.service.CarriageService;
import businessLogic.service.StaffService;
import businessLogic.RailSuiteFacade;
import domain.Line;
import domain.Convoy;
import domain.Carriage;
import domain.Staff;
import domain.Run;
import domain.LineStation;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class TrainStatusController {
    @FXML
    private Label supervisorNameLabel;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML private ComboBox<Line> lineComboBox;
    @FXML private ComboBox<Convoy> convoyComboBox;
    @FXML private ComboBox<Staff> operatorComboBox;
    @FXML private ListView<Carriage> carriageListView;
    @FXML private TextField carriageModelField;
    @FXML private TextField carriageTypeField;
    @FXML private Button assignButton;
    @FXML private Button createConvoyButton;
    @FXML private Button addCarriageButton;
    @FXML private TableView<SummaryRow> summaryTable;
    @FXML private TableColumn<SummaryRow, String> summaryLineColumn;
    @FXML private TableColumn<SummaryRow, String> summaryConvoyColumn;
    @FXML private TableColumn<SummaryRow, String> summaryOperatorColumn;
    @FXML private TableColumn<SummaryRow, String> summaryCarriagesColumn;
    @FXML private TableColumn<SummaryRow, Void> summaryDeleteColumn;
    @FXML private ComboBox<String> carriageTypeComboBox;
    @FXML private Spinner<Integer> carriageCountSpinner;
    @FXML private ComboBox<String> carriageModelComboBox;
    @FXML private ComboBox<Convoy> deleteConvoyComboBox;
    @FXML private Button deleteConvoyButton;

    private final LineService lineService = new LineService();
    private final ConvoyService convoyService = new ConvoyService();
    private final CarriageService carriageService = new CarriageService();
    private final StaffService staffService = new StaffService();
    private final RailSuiteFacade facade = new RailSuiteFacade();

    private List<Carriage> allCarriagesFinal = List.of();

    public static class SummaryRow {
        private final String line;
        private final String convoy;
        private final String operator;
        private final String carriages;
        public SummaryRow(String line, String convoy, String operator, String carriages) {
            this.line = line;
            this.convoy = convoy;
            this.operator = operator;
            this.carriages = carriages;
        }
        public String getLine() { return line; }
        public String getConvoy() { return convoy; }
        public String getOperator() { return operator; }
        public String getCarriages() { return carriages; }
    }

    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            supervisorNameLabel.setText(fullName);
            supervisorNameLabel.setOnMouseClicked(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        }
        logoutMenuItem.setOnAction(_ -> handleLogout());
        exitMenuItem.setOnAction(_ -> handleExit());
        // Popola ComboBox e ListView usando la facade
        try {
            lineComboBox.setItems(FXCollections.observableArrayList(facade.findAllLines()));
            convoyComboBox.setItems(FXCollections.observableArrayList(facade.selectAllConvoys()));
            operatorComboBox.setItems(FXCollections.observableArrayList(facade.findAllOperators()));
            carriageListView.setItems(FXCollections.observableArrayList(facade.selectAllCarriages()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        carriageListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Popola ComboBox tipi carrozza usando la facade
        updateAvailableCarriagesUI();
        carriageTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType != null) {
                List<String> models = allCarriagesFinal.stream()
                    .filter(c -> c.getModelType().equals(newType))
                    .map(Carriage::getModel)
                    .distinct()
                    .collect(Collectors.toList());
                carriageModelComboBox.setItems(FXCollections.observableArrayList(models));
                carriageModelComboBox.setDisable(false);
                if (!models.isEmpty()) {
                    carriageModelComboBox.getSelectionModel().selectFirst();
                }
            } else {
                carriageModelComboBox.setItems(FXCollections.observableArrayList());
                carriageModelComboBox.setDisable(true);
            }
            String selectedModel = carriageModelComboBox.getSelectionModel().getSelectedItem();
            updateCarriageCountSpinner(newType, selectedModel, allCarriagesFinal);
        });
        carriageModelComboBox.valueProperty().addListener((obs, oldModel, newModel) -> {
            String selectedType = carriageTypeComboBox.getSelectionModel().getSelectedItem();
            updateCarriageCountSpinner(selectedType, newModel, allCarriagesFinal);
        });
        // Visualizzazione leggibile
        lineComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Line item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        lineComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Line item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        convoyComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        convoyComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        operatorComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Staff item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getName() + " " + item.getSurname()));
            }
        });
        operatorComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Staff item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getName() + " " + item.getSurname()));
            }
        });
        carriageListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Carriage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getModel() + " (" + item.getId() + ")"));
            }
        });
        // Tabella riepilogo
        summaryLineColumn.setCellValueFactory(new PropertyValueFactory<>("line"));
        summaryConvoyColumn.setCellValueFactory(new PropertyValueFactory<>("convoy"));
        summaryOperatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        summaryCarriagesColumn.setCellValueFactory(new PropertyValueFactory<>("carriages"));
        addDeleteButtonToTable();
        refreshSummaryTable();
        // Pulsanti
        assignButton.setOnAction(e -> handleAssign());
        createConvoyButton.setOnAction(e -> handleCreateConvoy());
        addCarriageButton.setOnAction(e -> handleAddCarriage());
        // Inizializza la tendina per eliminare convogli
        try {
            deleteConvoyComboBox.setItems(FXCollections.observableArrayList(facade.selectAllConvoys()));
        } catch (Exception ex) {
            deleteConvoyComboBox.setItems(FXCollections.observableArrayList());
        }
        deleteConvoyComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        deleteConvoyComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        deleteConvoyButton.setOnAction(_ -> handleDeleteConvoy());
    }

    private void updateAvailableCarriagesUI() {
        try {
            allCarriagesFinal = facade.selectAllCarriages().stream()
                .filter(c -> c.getIdConvoy() == null)
                .collect(Collectors.toList());
            List<String> types = allCarriagesFinal.stream()
                .map(Carriage::getModelType)
                .distinct()
                .collect(Collectors.toList());
            carriageTypeComboBox.setItems(FXCollections.observableArrayList(types));
            carriageModelComboBox.setDisable(true);
            if (!types.isEmpty()) {
                carriageTypeComboBox.getSelectionModel().selectFirst();
                String firstType = types.get(0);
                List<String> models = allCarriagesFinal.stream()
                    .filter(c -> c.getModelType().equals(firstType))
                    .map(Carriage::getModel)
                    .distinct()
                    .collect(Collectors.toList());
                String firstModel = models.isEmpty() ? null : models.get(0);
                updateCarriageCountSpinner(firstType, firstModel, allCarriagesFinal);
                carriageModelComboBox.setItems(FXCollections.observableArrayList(models));
                carriageModelComboBox.setDisable(false);
                if (!models.isEmpty()) {
                    carriageModelComboBox.getSelectionModel().selectFirst();
                }
            } else {
                carriageModelComboBox.setItems(FXCollections.observableArrayList());
                updateCarriageCountSpinner(null, null, allCarriagesFinal);
            }
        } catch (Exception e) {
            carriageTypeComboBox.setItems(FXCollections.observableArrayList());
            carriageModelComboBox.setItems(FXCollections.observableArrayList());
            updateCarriageCountSpinner(null, null, List.of());
        }
    }

    private void updateCarriageCountSpinner(String type, String model, List<Carriage> allCarriages) {
        if (type == null || model == null) {
            carriageCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            carriageCountSpinner.getValueFactory().setValue(1);
            carriageCountSpinner.setDisable(true);
            return;
        }
        long count = allCarriages.stream().filter(c -> c.getModelType().equals(type) && c.getModel().equals(model)).count();
        int max = (int) Math.max(count, 1);
        carriageCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, 1));
        carriageCountSpinner.getValueFactory().setValue(1);
        carriageCountSpinner.setDisable(count == 0);
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }

    private void handleExit() {
        javafx.application.Platform.exit();
    }

    @FXML
    private void handleAssign() {
        Line selectedLine = lineComboBox.getValue();
        Convoy convoy = convoyComboBox.getValue();
        Staff operator = operatorComboBox.getValue();
        if (selectedLine == null || convoy == null || operator == null) return;
        try {
            // Controllo se esiste già una corsa con la stessa combinazione
            boolean alreadyExists = facade.selectAllRuns().stream().anyMatch(run ->
                run.getIdLine() == selectedLine.getIdLine() &&
                run.getIdConvoy() == convoy.getId() &&
                run.getIdStaff() == operator.getIdStaff()
            );
            if (alreadyExists) {
                // Mostra un alert all'utente
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore assegnazione corsa");
                alert.setHeaderText(null);
                alert.setContentText("Esiste già una corsa con questa combinazione di linea, convoglio e operatore.");
                alert.showAndWait();
                return;
            }
            // Recupera la lista delle stazioni della linea tramite la facade
            List<LineStation> stations = facade.findLineStationsByLineId(selectedLine.getIdLine());
            if (stations == null || stations.isEmpty()) return;
            int idFirstStation = stations.get(0).getStationId();
            int idLastStation = stations.get(stations.size() - 1).getStationId();
            Time timeDeparture = Time.valueOf(LocalTime.of(8, 0));
            Time timeArrival = Time.valueOf(LocalTime.of(12, 0));
            facade.createRun(selectedLine.getIdLine(), convoy.getId(), operator.getIdStaff(), timeDeparture, timeArrival, idFirstStation, idLastStation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshSummaryTable();
    }

    private void handleCreateConvoy() {
        String selectedType = carriageTypeComboBox.getValue();
        String selectedModel = carriageModelComboBox.getValue();
        int count = carriageCountSpinner.getValue();
        if (selectedType == null || selectedModel == null || count < 1) return;
        List<Carriage> available = allCarriagesFinal.stream()
            .filter(c -> c.getModelType().equals(selectedType) && c.getModel().equals(selectedModel))
            .limit(count)
            .collect(Collectors.toList());
        if (available.size() < count) return;
        convoyService.createConvoy(available);
        convoyComboBox.setItems(FXCollections.observableArrayList(convoyService.getAllConvoys()));
        refreshSummaryTable();
        updateAvailableCarriagesUI();
    }

    private void handleAddCarriage() {
        String model = carriageModelField.getText();
        String type = carriageTypeField.getText();
        if (model == null || model.isBlank() || type == null || type.isBlank()) return;
        Carriage newCarriage = Carriage.of(0, model, type, 2024, 100, null);
        carriageService.addCarriage(newCarriage);
        carriageListView.setItems(FXCollections.observableArrayList(carriageService.getAllCarriages()));
        carriageModelField.clear();
        carriageTypeField.clear();
        refreshSummaryTable();
    }

    private void refreshSummaryTable() {
        ObservableList<SummaryRow> rows = FXCollections.observableArrayList();
        try {
            for (Run run : facade.selectAllRuns()) {
                Optional<Line> line = lineService.getAllLines().stream().filter(l -> l.getIdLine() == run.getIdLine()).findFirst();
                Optional<Convoy> convoy = convoyService.getAllConvoys().stream().filter(c -> c.getId() == run.getIdConvoy()).findFirst();
                Optional<Staff> operator = staffService.getAllOperators().stream().filter(s -> s.getIdStaff() == run.getIdStaff()).findFirst();
                // Recupera le carrozze aggiornate tramite la facade
                String carriages = "";
                if (convoy.isPresent()) {
                    try {
                        List<Carriage> carriageList = facade.selectCarriagesByConvoyId(convoy.get().getId());
                        carriages = carriageList.stream()
                                .map(car -> car.getModel() + "(" + car.getId() + ")")
                                .reduce((a, b) -> a + ", " + b).orElse("");
                    } catch (Exception ex) {
                        carriages = "Errore";
                    }
                }
                rows.add(new SummaryRow(
                    line.map(Line::getName).orElse("-"),
                    convoy.map(c -> String.valueOf(c.getId())).orElse("-"),
                    operator.map(s -> s.getName() + " " + s.getSurname()).orElse("-"),
                    carriages
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        summaryTable.setItems(rows);
    }

    private void addDeleteButtonToTable() {
        summaryDeleteColumn.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Elimina");
            {
                deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    SummaryRow row = getTableView().getItems().get(getIndex());
                    try {
                        // Trova la run corrispondente
                        List<Run> runs = facade.selectAllRuns();
                        Run runToDelete = runs.stream()
                            .filter(r ->
                                row.getLine().equals(lineService.getAllLines().stream().filter(l -> l.getIdLine() == r.getIdLine()).map(Line::getName).findFirst().orElse("")) &&
                                row.getConvoy().equals(String.valueOf(r.getIdConvoy()))
                            )
                            .findFirst().orElse(null);
                        if (runToDelete != null) {
                            facade.removeRun(runToDelete.getIdLine(), runToDelete.getIdConvoy());
                            refreshSummaryTable();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void handleDeleteConvoy() {
        Convoy selected = deleteConvoyComboBox.getValue();
        if (selected == null) return;
        try {
            List<Carriage> carriages = facade.selectCarriagesByConvoyId(selected.getId());
            for (Carriage c : carriages) {
                try {
                    facade.updateCarriageConvoy(c.getId(), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            facade.removeConvoy(selected.getId());
            ObservableList<Convoy> convoys = FXCollections.observableArrayList(facade.selectAllConvoys());
            convoyComboBox.setItems(convoys);
            deleteConvoyComboBox.setItems(convoys);
            carriageListView.setItems(FXCollections.observableArrayList(facade.selectAllCarriages()));
            refreshSummaryTable();
            updateAvailableCarriagesUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
