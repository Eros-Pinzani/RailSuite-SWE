package businessLogic.controller;

/**
 * Controller for the Manage Run screen.
 * Handles the display and management of train runs for a selected line and date.
 */
import businessLogic.service.LineService;
import businessLogic.service.ConvoyService;
import businessLogic.service.StaffService;
import businessLogic.RailSuiteFacade;
import domain.Line;
import domain.Convoy;
import domain.Staff;
import domain.Run;
import domain.LineStation;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ManageRunController {
    @FXML private ComboBox<Line> lineComboBox;
    @FXML private ComboBox<Convoy> convoyComboBox;
    @FXML private ComboBox<Staff> operatorComboBox;
    @FXML private Button assignButton;
    @FXML private TableView<SummaryRow> summaryTable;
    @FXML private TableColumn<SummaryRow, String> summaryLineColumn;
    @FXML private TableColumn<SummaryRow, String> summaryConvoyColumn;
    @FXML private TableColumn<SummaryRow, String> summaryOperatorColumn;
    @FXML private TableColumn<SummaryRow, String> summaryCarriagesColumn;
    @FXML private TableColumn<SummaryRow, Void> summaryDeleteColumn;
    @FXML private javafx.scene.control.Label supervisorNameLabel;
    @FXML private javafx.scene.control.MenuItem logoutMenuItem;
    @FXML private javafx.scene.control.MenuItem exitMenuItem;
    @FXML private javafx.scene.control.MenuButton menuButton;

    private final LineService lineService = new LineService();
    private final ConvoyService convoyService = new ConvoyService();
    private final StaffService staffService = new StaffService();
    private final RailSuiteFacade facade = new RailSuiteFacade();

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

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads lines, convoys, and operators.
     */
    @FXML
    public void initialize() {
        ManageConvoyController.header(supervisorNameLabel, logoutMenuItem, exitMenuItem);

        try {
            lineComboBox.setItems(FXCollections.observableArrayList(facade.findAllLines()));
            convoyComboBox.setItems(FXCollections.observableArrayList(facade.selectAllConvoys()));
            operatorComboBox.setItems(FXCollections.observableArrayList(facade.findAllOperators()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        summaryLineColumn.setCellValueFactory(new PropertyValueFactory<>("line"));
        summaryConvoyColumn.setCellValueFactory(new PropertyValueFactory<>("convoy"));
        summaryOperatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        summaryCarriagesColumn.setCellValueFactory(new PropertyValueFactory<>("carriages"));
        addDeleteButtonToTable();
        refreshSummaryTable();
        assignButton.setOnAction(e -> handleAssign());
    }

    /**
     * Handles the assignment of a run to a line, convoy, and operator.
     * Validates for duplicates and creates a new run if valid.
     * Refreshes the summary table after assignment.
     */
    private void handleAssign() {
        Line selectedLine = lineComboBox.getValue();
        Convoy convoy = convoyComboBox.getValue();
        Staff operator = operatorComboBox.getValue();
        if (selectedLine == null || convoy == null || operator == null) return;
        try {
            boolean alreadyExists = facade.selectAllRuns().stream().anyMatch(run ->
                run.getIdLine() == selectedLine.getIdLine() &&
                run.getIdConvoy() == convoy.getId() &&
                run.getIdStaff() == operator.getIdStaff()
            );
            if (alreadyExists) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore assegnazione corsa");
                alert.setHeaderText(null);
                alert.setContentText("Esiste già una corsa con questa combinazione di linea, convoglio e operatore.");
                alert.showAndWait();
                return;
            }
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

    /**
     * Refreshes the summary table with the current list of runs and their details.
     */
    private void refreshSummaryTable() {
        ObservableList<SummaryRow> rows = FXCollections.observableArrayList();
        try {
            for (Run run : facade.selectAllRuns()) {
                Optional<Line> line = lineService.getAllLines().stream().filter(l -> l.getIdLine() == run.getIdLine()).findFirst();
                Optional<Convoy> convoy = convoyService.getAllConvoys().stream().filter(c -> c.getId() == run.getIdConvoy()).findFirst();
                Optional<Staff> operator = staffService.getAllOperators().stream().filter(s -> s.getIdStaff() == run.getIdStaff()).findFirst();
                String carriages = "";
                if (convoy.isPresent()) {
                    try {
                        List<domain.Carriage> carriageList = facade.selectCarriagesByConvoyId(convoy.get().getId());
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

    /**
     * Adds a delete button to each row in the summary table, allowing removal of runs.
     * Handles the deletion and refreshes the table after removal.
     */
    private void addDeleteButtonToTable() {
        summaryDeleteColumn.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Elimina");
            {
                deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    SummaryRow row = getTableView().getItems().get(getIndex());
                    try {
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
}
