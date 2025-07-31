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

import java.util.ArrayList;
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
        // Carica le liste una sola volta
        allLines = manageRunService.getAllLines();
        allConvoys = manageRunService.getAllConvoys();
        allOperators = manageRunService.getAllOperators();
        try {
            updateAllFilters();
        } catch (Exception e) {
            logger.severe("Error initializing combo boxes: " + e.getMessage());
        }
        filterLineComboBox.setOnAction(e -> updateFiltersFromLine());
        filterConvoyComboBox.setOnAction(e -> updateFiltersFromConvoy());
        filterOperatorComboBox.setOnAction(e -> updateFiltersFromOperator());
        searchButton.setOnAction(e -> refreshRunSummaryTable());
        newRunButton.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/CreateRun.fxml"));
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

    private boolean isUpdatingFilters = false;

    private void updateAllFilters() {
        if (isUpdatingFilters) return;
        isUpdatingFilters = true;
        try {
            String selectedLineName = filterLineComboBox.getValue();
            String selectedConvoyIdStr = filterConvoyComboBox.getValue();
            String selectedOperatorName = filterOperatorComboBox.getValue();

            Integer idLine = null;
            Integer idConvoy = null;
            Integer idStaff = null;
            if (selectedLineName != null && !selectedLineName.equals("-------")) {
                Line line = allLines.stream().filter(l -> l.getName().equals(selectedLineName)).findFirst().orElse(null);
                if (line != null) idLine = line.getIdLine();
            }
            if (selectedConvoyIdStr != null && !selectedConvoyIdStr.equals("-------")) {
                try { idConvoy = Integer.parseInt(selectedConvoyIdStr); } catch (Exception ignored) {}
            }
            if (selectedOperatorName != null && !selectedOperatorName.equals("-------")) {
                Staff staff = allOperators.stream().filter(s -> (s.getName() + " " + s.getSurname()).equals(selectedOperatorName)).findFirst().orElse(null);
                if (staff != null) idStaff = staff.getIdStaff();
            }

            List<String> filteredLines = new ArrayList<>();
            filteredLines.add("-------");
            for (Line l : allLines) {
                boolean match = true;
                if (idConvoy != null && idStaff != null) {
                    match = !manageRunService.getAssociationsByLineConvoyAndStaff(l.getIdLine(), idConvoy, idStaff).isEmpty();
                } else if (idConvoy != null) {
                    match = !manageRunService.getAssociationsByLineAndConvoy(l.getIdLine(), idConvoy).isEmpty();
                } else if (idStaff != null) {
                    match = !manageRunService.getAssociationsByLineAndStaff(l.getIdLine(), idStaff).isEmpty();
                }
                if (match) filteredLines.add(l.getName());
            }
            filterLineComboBox.setItems(FXCollections.observableArrayList(filteredLines));
            if (!filteredLines.contains(selectedLineName)) {
                filterLineComboBox.setValue("-------");
            } else {
                filterLineComboBox.setValue(selectedLineName);
            }

            List<String> filteredConvoys = new ArrayList<>();
            filteredConvoys.add("-------");
            for (Convoy c : allConvoys) {
                boolean match = true;
                if (idLine != null && idStaff != null) {
                    match = !manageRunService.getAssociationsByLineConvoyAndStaff(idLine, c.getId(), idStaff).isEmpty();
                } else if (idLine != null) {
                    match = !manageRunService.getAssociationsByLineAndConvoy(idLine, c.getId()).isEmpty();
                } else if (idStaff != null) {
                    match = !manageRunService.getAssociationsByConvoyAndStaff(c.getId(), idStaff).isEmpty();
                }
                if (match) filteredConvoys.add(Integer.toString(c.getId()));
            }
            filterConvoyComboBox.setItems(FXCollections.observableArrayList(filteredConvoys));
            if (!filteredConvoys.contains(selectedConvoyIdStr)) {
                filterConvoyComboBox.setValue("-------");
            } else {
                filterConvoyComboBox.setValue(selectedConvoyIdStr);
            }

            List<String> filteredOperators = new ArrayList<>();
            filteredOperators.add("-------");
            for (Staff s : allOperators) {
                boolean match = true;
                if (idLine != null && idConvoy != null) {
                    match = !manageRunService.getAssociationsByLineConvoyAndStaff(idLine, idConvoy, s.getIdStaff()).isEmpty();
                } else if (idLine != null) {
                    match = !manageRunService.getAssociationsByLineAndStaff(idLine, s.getIdStaff()).isEmpty();
                } else if (idConvoy != null) {
                    match = !manageRunService.getAssociationsByConvoyAndStaff(idConvoy, s.getIdStaff()).isEmpty();
                }
                if (match) filteredOperators.add(s.getName() + " " + s.getSurname());
            }
            filterOperatorComboBox.setItems(FXCollections.observableArrayList(filteredOperators));
            if (!filteredOperators.contains(selectedOperatorName)) {
                filterOperatorComboBox.setValue("-------");
            } else {
                filterOperatorComboBox.setValue(selectedOperatorName);
            }
        } finally {
            isUpdatingFilters = false;
        }
    }

    // Liste caricate una sola volta all'avvio
    private List<Line> allLines;
    private List<Convoy> allConvoys;
    private List<Staff> allOperators;

    private List<String> getLineFilterItems() {
        List<String> items = allLines.stream().map(Line::getName).toList();
        List<String> withAll = new java.util.ArrayList<>();
        withAll.add("-------");
        withAll.addAll(items);
        return withAll;
    }
    private List<String> getConvoyFilterItems() {
        List<String> items = allConvoys.stream().map(c -> Integer.toString(c.getId())).toList();
        List<String> withAll = new java.util.ArrayList<>();
        withAll.add("-------");
        withAll.addAll(items);
        return withAll;
    }
    private List<String> getOperatorFilterItems() {
        List<String> items = allOperators.stream().map(s -> s.getName() + " " + s.getSurname()).toList();
        List<String> withAll = new java.util.ArrayList<>();
        withAll.add("-------");
        withAll.addAll(items);
        return withAll;
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

    // Aggiorna i ComboBox in base alla selezione della linea
    private void updateFiltersFromLine() {
        if (isUpdatingFilters) return;
        isUpdatingFilters = true;
        try {
            String selectedLineName = filterLineComboBox.getValue();
            if (selectedLineName == null || selectedLineName.equals("-------")) {
                filterConvoyComboBox.setItems(FXCollections.observableArrayList(getConvoyFilterItems()));
                filterOperatorComboBox.setItems(FXCollections.observableArrayList(getOperatorFilterItems()));
                return;
            }
            Line line = manageRunService.getAllLines().stream().filter(l -> l.getName().equals(selectedLineName)).findFirst().orElse(null);
            if (line == null) return;
            // Convogli associati a questa linea
            List<String> convoysList = new ArrayList<>();
            convoysList.add("-------");
            manageRunService.getAllConvoys().forEach(c -> {
                if (!manageRunService.getAssociationsByLineAndConvoy(line.getIdLine(), c.getId()).isEmpty())
                    convoysList.add(Integer.toString(c.getId()));
            });
            filterConvoyComboBox.setItems(FXCollections.observableArrayList(convoysList));
            // Operatori associati a questa linea
            List<String> operatorsList = new ArrayList<>();
            operatorsList.add("-------");
            manageRunService.getAllOperators().forEach(s -> {
                if (!manageRunService.getAssociationsByLineAndStaff(line.getIdLine(), s.getIdStaff()).isEmpty())
                    operatorsList.add(s.getName() + " " + s.getSurname());
            });
            filterOperatorComboBox.setItems(FXCollections.observableArrayList(operatorsList));
        } finally {
            isUpdatingFilters = false;
        }
    }

    // Aggiorna i ComboBox in base alla selezione del convoglio
    private void updateFiltersFromConvoy() {
        if (isUpdatingFilters) return;
        isUpdatingFilters = true;
        try {
            String selectedConvoyIdStr = filterConvoyComboBox.getValue();
            if (selectedConvoyIdStr == null || selectedConvoyIdStr.equals("-------")) {
                filterLineComboBox.setItems(FXCollections.observableArrayList(getLineFilterItems()));
                filterOperatorComboBox.setItems(FXCollections.observableArrayList(getOperatorFilterItems()));
                return;
            }
            int idConvoy;
            try { idConvoy = Integer.parseInt(selectedConvoyIdStr); } catch (Exception ex) { return; }
            // Linee associate a questo convoglio
            List<String> linesList = new ArrayList<>();
            linesList.add("-------");
            manageRunService.getAllLines().forEach(l -> {
                if (!manageRunService.getAssociationsByLineAndConvoy(l.getIdLine(), idConvoy).isEmpty())
                    linesList.add(l.getName());
            });
            filterLineComboBox.setItems(FXCollections.observableArrayList(linesList));
            // Operatori associati a questo convoglio
            List<String> operatorsList = new ArrayList<>();
            operatorsList.add("-------");
            manageRunService.getAllOperators().forEach(s -> {
                if (!manageRunService.getAssociationsByConvoyAndStaff(idConvoy, s.getIdStaff()).isEmpty())
                    operatorsList.add(s.getName() + " " + s.getSurname());
            });
            filterOperatorComboBox.setItems(FXCollections.observableArrayList(operatorsList));
        } finally {
            isUpdatingFilters = false;
        }
    }

    // Aggiorna i ComboBox in base alla selezione dell'operatore
    private void updateFiltersFromOperator() {
        if (isUpdatingFilters) return;
        isUpdatingFilters = true;
        try {
            String selectedOperatorName = filterOperatorComboBox.getValue();
            if (selectedOperatorName == null || selectedOperatorName.equals("-------")) {
                filterLineComboBox.setItems(FXCollections.observableArrayList(getLineFilterItems()));
                filterConvoyComboBox.setItems(FXCollections.observableArrayList(getConvoyFilterItems()));
                return;
            }
            Staff staff = manageRunService.getAllOperators().stream().filter(s -> (s.getName() + " " + s.getSurname()).equals(selectedOperatorName)).findFirst().orElse(null);
            if (staff == null) return;
            // Linee associate a questo operatore
            List<String> linesList = new ArrayList<>();
            linesList.add("-------");
            manageRunService.getAllLines().forEach(l -> {
                if (!manageRunService.getAssociationsByLineAndStaff(l.getIdLine(), staff.getIdStaff()).isEmpty())
                    linesList.add(l.getName());
            });
            filterLineComboBox.setItems(FXCollections.observableArrayList(linesList));
            // Convogli associati a questo operatore
            List<String> convoysList = new ArrayList<>();
            convoysList.add("-------");
            manageRunService.getAllConvoys().forEach(c -> {
                if (!manageRunService.getAssociationsByConvoyAndStaff(c.getId(), staff.getIdStaff()).isEmpty())
                    convoysList.add(Integer.toString(c.getId()));
            });
            filterConvoyComboBox.setItems(FXCollections.observableArrayList(convoysList));
        } finally {
            isUpdatingFilters = false;
        }
    }
}
