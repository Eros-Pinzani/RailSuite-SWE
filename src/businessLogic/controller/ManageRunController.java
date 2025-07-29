package businessLogic.controller;

/**
 * Controller for the Manage Run screen.
 * Handles the display and management of train runs for a selected line and date.
 */
import businessLogic.service.LineService;
import businessLogic.service.ConvoyService;
import businessLogic.service.StaffService;
import businessLogic.service.ManageRunService;
import businessLogic.RailSuiteFacade;
import domain.Line;
import domain.Convoy;
import domain.Staff;
import domain.Run;
import dao.StationDao;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.logging.Logger;

public class ManageRunController {
    @FXML private ComboBox<String> filterLineComboBox;
    @FXML private ComboBox<String> filterConvoyComboBox;
    @FXML private ComboBox<String> filterOperatorComboBox;
    @FXML private TableView<RunSummaryRow> summaryTable;
    @FXML private TableColumn<RunSummaryRow, Void> summaryDeleteColumn;
    @FXML private javafx.scene.control.Label supervisorNameLabel;
    @FXML private javafx.scene.control.MenuItem logoutMenuItem;
    @FXML private javafx.scene.control.MenuItem exitMenuItem;
    @FXML private javafx.scene.control.MenuButton menuButton;
    @FXML private TableColumn<RunSummaryRow, String> operatorColumn;
    @FXML private TableColumn<RunSummaryRow, Integer> convoyIdColumn;
    @FXML private TableColumn<RunSummaryRow, Integer> lineIdColumn;
    @FXML private TableColumn<RunSummaryRow, String> startTimeColumn;
    @FXML private TableColumn<RunSummaryRow, String> endTimeColumn;
    @FXML private TableColumn<RunSummaryRow, String> startStationColumn;
    @FXML private TableColumn<RunSummaryRow, String> endStationColumn;
    @FXML private TableColumn<RunSummaryRow, RunStatus> statusColumn;
    @FXML private Button detailsButton;
    @FXML private Button backButton;
    @FXML private Button searchButton;
    @FXML private Button newRunButton;

    private final LineService lineService = new LineService();
    private final ConvoyService convoyService = new ConvoyService();
    private final StaffService staffService = new StaffService();
    private final ManageRunService manageRunService = new ManageRunService();
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private static final Logger logger = Logger.getLogger(ManageRunController.class.getName());

    // Enum for run status
    public enum RunStatus {
        BEFORE_DEPARTURE, IN_PROGRESS, AFTER_ARRIVAL
    }

    /**
     * DTO for the run summary table.
     */
    public static class RunSummaryRow {
        private final String name;
        private final String surname;
        private final int convoyId;
        private final int lineId;
        private final String departureTime;
        private final String arrivalTime;
        private final String origin;
        private final String destination;
        private final RunStatus status;

        public RunSummaryRow(String name, String surname, int convoyId, int lineId, String departureTime, String arrivalTime, String origin, String destination, RunStatus status) {
            this.name = name;
            this.surname = surname;
            this.convoyId = convoyId;
            this.lineId = lineId;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.origin = origin;
            this.destination = destination;
            this.status = status;
        }
        public String getName() { return name; }
        public String getSurname() { return surname; }
        public int getConvoyId() { return convoyId; }
        public int getLineId() { return lineId; }
        public String getDepartureTime() { return departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public RunStatus getStatus() { return status; }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads lines, convoys, and operators.
     */
    @FXML
    public void initialize() {
        ManageConvoyController.header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        try {
            filterLineComboBox.setItems(FXCollections.observableArrayList(getLineFilterItems()));
            filterConvoyComboBox.setItems(FXCollections.observableArrayList(getConvoyFilterItems()));
            filterOperatorComboBox.setItems(FXCollections.observableArrayList(getOperatorFilterItems()));
        } catch (Exception e) {
            logger.severe("Error initializing combo boxes: " + e.getMessage());
        }
        searchButton.setOnAction(e -> refreshRunSummaryTable());
        newRunButton.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/CreateRun.fxml"));
        // Listener solo per il tasto cerca
        // Table columns setup
        operatorColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName() + " " + data.getValue().getSurname()));
        convoyIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getConvoyId()));
        lineIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getLineId()));
        startTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDepartureTime()));
        endTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getArrivalTime()));
        startStationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrigin()));
        endStationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDestination()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStatus()));
        detailsButton.disableProperty().bind(summaryTable.getSelectionModel().selectedItemProperty().isNull());
        backButton.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        // Inizializza la tabella solo all'avvio
        refreshRunSummaryTable();
    }

    private List<String> getLineFilterItems() {
        try {
            List<String> items = facade.findAllLines().stream().map(Line::getName).toList();
            List<String> withAll = new java.util.ArrayList<>();
            withAll.add("-------");
            withAll.addAll(items);
            return withAll;
        } catch (Exception e) {
            logger.severe("Errore caricamento linee: " + e.getMessage());
            return java.util.Collections.singletonList("-------");
        }
    }
    private List<String> getConvoyFilterItems() {
        try {
            List<String> items = facade.selectAllConvoys().stream().map(c -> Integer.toString(c.getId())).toList();
            List<String> withAll = new java.util.ArrayList<>();
            withAll.add("-------");
            withAll.addAll(items);
            return withAll;
        } catch (Exception e) {
            logger.severe("Errore caricamento convogli: " + e.getMessage());
            return java.util.Collections.singletonList("-------");
        }
    }
    private List<String> getOperatorFilterItems() {
        try {
            List<String> items = facade.findAllOperators().stream().map(s -> s.getName() + " " + s.getSurname()).toList();
            List<String> withAll = new java.util.ArrayList<>();
            withAll.add("-------");
            withAll.addAll(items);
            return withAll;
        } catch (Exception e) {
            logger.severe("Errore caricamento operatori: " + e.getMessage());
            return java.util.Collections.singletonList("-------");
        }
    }

    private String getStationNameById(int idStation) {
        try {
            domain.Station station = StationDao.of().findById(idStation);
            return station != null ? station.getLocation() : "-";
        } catch (Exception e) {
            return "-";
        }
    }

    private void refreshRunSummaryTable() {
        ObservableList<RunSummaryRow> rows = FXCollections.observableArrayList();
        String selectedLineName = filterLineComboBox.getValue();
        String selectedConvoyIdStr = filterConvoyComboBox.getValue();
        String selectedOperatorName = filterOperatorComboBox.getValue();
        Integer idLine = null;
        Integer idConvoy = null;
        Integer idStaff = null;
        if (selectedLineName != null && !selectedLineName.equals("-------")) {
            Line line = lineService.getAllLines().stream().filter(l -> l.getName().equals(selectedLineName)).findFirst().orElse(null);
            if (line != null) idLine = line.getIdLine();
        }
        if (selectedConvoyIdStr != null && !selectedConvoyIdStr.equals("-------")) {
            try { idConvoy = Integer.parseInt(selectedConvoyIdStr); } catch (Exception ignored) {}
        }
        if (selectedOperatorName != null && !selectedOperatorName.equals("-------")) {
            Staff staff = staffService.getAllOperators().stream().filter(s -> (s.getName() + " " + s.getSurname()).equals(selectedOperatorName)).findFirst().orElse(null);
            if (staff != null) idStaff = staff.getIdStaff();
        }
        List<Run> filteredRuns = manageRunService.getFilteredRuns(idLine, idConvoy, idStaff);
        for (Run run : filteredRuns) {
            Staff staff = staffService.getAllOperators().stream().filter(s -> s.getIdStaff() == run.getIdStaff()).findFirst().orElse(null);
            Line line = lineService.getAllLines().stream().filter(l -> l.getIdLine() == run.getIdLine()).findFirst().orElse(null);
            Convoy convoy = convoyService.getAllConvoys().stream().filter(c -> c.getId() == run.getIdConvoy()).findFirst().orElse(null);
            String name = staff != null ? staff.getName() : "-";
            String surname = staff != null ? staff.getSurname() : "-";
            int convoyId = convoy != null ? convoy.getId() : -1;
            int lineId = line != null ? line.getIdLine() : -1;
            String departureTime = run.getTimeDeparture().toString();
            String arrivalTime = run.getTimeArrival().toString();
            String origin = getStationNameById(run.getIdFirstStation());
            String destination = getStationNameById(run.getIdLastStation());
            RunStatus status;
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime dep = run.getTimeDeparture().toLocalTime();
            java.time.LocalTime arr = run.getTimeArrival().toLocalTime();
            if (now.isBefore(dep)) status = RunStatus.BEFORE_DEPARTURE;
            else if (now.isAfter(arr)) status = RunStatus.AFTER_ARRIVAL;
            else status = RunStatus.IN_PROGRESS;
            rows.add(new RunSummaryRow(name, surname, convoyId, lineId, departureTime, arrivalTime, origin, destination, status));
        }
        summaryTable.setItems(rows);
    }
}
