package businessLogic.controller;

/**
 * Controller for the Manage Run screen.
 * Handles the display and management of train runs for a selected line and date.
 */

import businessLogic.service.ManageRunService;
import domain.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.EventHandler;

public class ManageRunController {
    @FXML
    private ComboBox<String> filterLineComboBox;
    @FXML
    private ComboBox<String> filterConvoyComboBox;
    @FXML
    private ComboBox<String> filterOperatorComboBox;
    @FXML
    private ComboBox<String> filterFirstStationComboBox;
    @FXML
    private TableView<Run> summaryTable;
    @FXML
    private javafx.scene.control.Label supervisorNameLabel;
    @FXML
    private javafx.scene.control.MenuItem logoutMenuItem;
    @FXML
    private javafx.scene.control.MenuItem exitMenuItem;
    @FXML
    private javafx.scene.control.MenuButton menuButton;
    @FXML
    private TableColumn<Run, String> operatorColumn;
    @FXML
    private TableColumn<Run, Integer> convoyIdColumn;
    @FXML
    private TableColumn<Run, String> lineNameColumn;
    @FXML
    private TableColumn<Run, Timestamp> startTimeColumn;
    @FXML
    private TableColumn<Run, Timestamp> endTimeColumn;
    @FXML
    private TableColumn<Run, String> startStationColumn;
    @FXML
    private TableColumn<Run, String> endStationColumn;
    @FXML
    private TableColumn<Run, String> statusColumn;
    @FXML
    private Button detailsButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button newRunButton;
    @FXML
    private javafx.scene.control.DatePicker filterDatePicker;


    private final ManageRunService manageRunService = new ManageRunService();
    private static final Logger logger = Logger.getLogger(ManageRunController.class.getName());
    private EventHandler<ActionEvent> lineComboHandler;
    private EventHandler<ActionEvent> convoyComboHandler;
    private EventHandler<ActionEvent> operatorComboHandler;


    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads lines, convoys, and operators.
     */
    @FXML
    public void initialize() {
        // TODO: Aggiornare lo stato di convogli, carrozze (manutenzione) e personale all'avvio della schermata ManageRun sotto forma di TASK
        ManageConvoyController.header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        try {
            initFilters();
        } catch (Exception e) {
            logger.severe("Error initializing combo boxes: " + e.getMessage());
        }
        lineComboHandler = e -> updateAllFilters();
        convoyComboHandler = e -> updateAllFilters();
        operatorComboHandler = e -> updateAllFilters();
        filterLineComboBox.setOnAction(lineComboHandler);
        filterConvoyComboBox.setOnAction(convoyComboHandler);
        filterOperatorComboBox.setOnAction(operatorComboHandler);
        searchButton.setOnAction(e -> refreshRunSummaryTable());
        newRunButton.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/CreateRun.fxml"));
        operatorColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStaffNameSurname()));
        convoyIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIdConvoy()));
        lineNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLineName()));
        startTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTimeDeparture()));
        endTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTimeArrival()));
        startStationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstStationName()));
        endStationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastStationName()));
        statusColumn.setCellValueFactory(data -> {
            Run.RunStatus status = data.getValue().getStatus();
            String display;
            switch (status) {
                case BEFORE_RUN -> display = "✏️ Modificabile";
                case RUN, AFTER_RUN -> display = "🔒 Bloccato";
                default -> display = status.toString();
            }
            return new javafx.beans.property.SimpleStringProperty(display);
        });
        detailsButton.disableProperty().bind(summaryTable.getSelectionModel().selectedItemProperty().isNull());
        detailsButton.setOnAction(e -> {
            Run selectedRun = summaryTable.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                SceneManager.getInstance().openRunDetailsScene(
                        selectedRun.getIdLine(),
                        selectedRun.getIdConvoy(),
                        selectedRun.getIdStaff(),
                        selectedRun.getTimeDeparture(),
                        selectedRun.getIdFirstStation()
                );
            }
        });
        backButton.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));

    }


    /**
     * Initializes filter combo boxes with available run data.
     * Loads lines, convoys, operators, and first stations for filtering.
     */
    private void initFilters() {
        List<Run> runsRaw = manageRunService.getAllRun();
        if (runsRaw.isEmpty()) {
            logger.warning("No runs available to initialize filters.");
            return;
        }

        filterLineComboBox.getItems().add("-------");
        filterLineComboBox.setValue("-------");
        filterConvoyComboBox.getItems().add("-------");
        filterConvoyComboBox.setValue("-------");
        filterOperatorComboBox.getItems().add("-------");
        filterOperatorComboBox.setValue("-------");
        filterFirstStationComboBox.getItems().add("-------");
        filterFirstStationComboBox.setValue("-------");

        for (Run run : runsRaw) {
            String lineName = run.getLineName();
            String convoyId = String.valueOf(run.getIdConvoy());
            String operatorName = run.getStaffNameSurname();
            String firstStationName = run.getFirstStationName();

            if (!filterLineComboBox.getItems().contains(lineName)) {
                filterLineComboBox.getItems().add(lineName);
            }
            if (!filterConvoyComboBox.getItems().contains(convoyId)) {
                filterConvoyComboBox.getItems().add(convoyId);
            }
            if (!filterOperatorComboBox.getItems().contains(operatorName)) {
                filterOperatorComboBox.getItems().add(operatorName);
            }
            if (!filterFirstStationComboBox.getItems().contains(firstStationName)) {
                filterFirstStationComboBox.getItems().add(firstStationName);
            }
        }
    }

    /**
     * Updates all filter combo boxes based on current selections and available data.
     * Ensures that only valid filter options are shown after a selection change.
     */
    private void updateAllFilters() {
        offHandlers();
        String selectedLine = filterLineComboBox.getValue();
        String selectedConvoy = filterConvoyComboBox.getValue();
        String selectedOperator = filterOperatorComboBox.getValue();
        String selectedFirstStation = filterFirstStationComboBox.getValue();
        if ("-------".equals(selectedLine)) selectedLine = null;
        if ("-------".equals(selectedConvoy)) selectedConvoy = null;
        if ("-------".equals(selectedOperator)) selectedOperator = null;
        if (selectedFirstStation == null || "-------".equals(selectedFirstStation)) selectedFirstStation = null;
        java.time.LocalDate selectedDate = filterDatePicker.getValue();
        java.sql.Timestamp dayStart = null;
        if (selectedDate != null) {
            dayStart = java.sql.Timestamp.valueOf(selectedDate.atStartOfDay());
        }
        List<Run> filtered = manageRunService.filterRunRaws(selectedLine, selectedConvoy, selectedOperator, selectedFirstStation, dayStart);
        filterLineComboBox.getItems().clear();
        filterConvoyComboBox.getItems().clear();
        filterOperatorComboBox.getItems().clear();
        filterFirstStationComboBox.getItems().clear();
        filterLineComboBox.getItems().add("-------");
        filterConvoyComboBox.getItems().add("-------");
        filterOperatorComboBox.getItems().add("-------");
        filterFirstStationComboBox.getItems().add("-------");
        for (Run run : filtered) {
            if (run.getLineName() != null && !filterLineComboBox.getItems().contains(run.getLineName())) {
                filterLineComboBox.getItems().add(run.getLineName());
            }
            if (run.getIdConvoy() != null && run.getIdConvoy() > 0 && !filterConvoyComboBox.getItems().contains(String.valueOf(run.getIdConvoy()))) {
                filterConvoyComboBox.getItems().add(String.valueOf(run.getIdConvoy()));
            }
            if (run.getStaffNameSurname() != null && !filterOperatorComboBox.getItems().contains(run.getStaffNameSurname())) {
                filterOperatorComboBox.getItems().add(run.getStaffNameSurname());
            }
            if (run.getFirstStationName() != null && !filterFirstStationComboBox.getItems().contains(run.getFirstStationName())) {
                filterFirstStationComboBox.getItems().add(run.getFirstStationName());
            }
        }
        if (selectedLine != null && filterLineComboBox.getItems().contains(selectedLine))
            filterLineComboBox.setValue(selectedLine);
        else filterLineComboBox.setValue("-------");
        if (selectedConvoy != null && filterConvoyComboBox.getItems().contains(selectedConvoy))
            filterConvoyComboBox.setValue(selectedConvoy);
        else filterConvoyComboBox.setValue("-------");
        if (selectedOperator != null && filterOperatorComboBox.getItems().contains(selectedOperator))
            filterOperatorComboBox.setValue(selectedOperator);
        else filterOperatorComboBox.setValue("-------");
        if (selectedFirstStation != null && filterFirstStationComboBox.getItems().contains(selectedFirstStation))
            filterFirstStationComboBox.setValue(selectedFirstStation);
        else filterFirstStationComboBox.setValue("-------");
        onHandlers();
    }

    /**
     * Disables filter combo box event handlers to prevent unwanted updates during batch changes.
     */
    private void offHandlers() {
        filterLineComboBox.setOnAction(null);
        filterConvoyComboBox.setOnAction(null);
        filterOperatorComboBox.setOnAction(null);
    }

    /**
     * Re-enables filter combo box event handlers after batch changes are complete.
     */
    private void onHandlers() {
        filterLineComboBox.setOnAction(lineComboHandler);
        filterConvoyComboBox.setOnAction(convoyComboHandler);
        filterOperatorComboBox.setOnAction(operatorComboHandler);
    }

    /**
     * Refreshes the summary table of runs based on the current filter selections.
     * Loads and displays runs for the selected date and filters.
     */
    private void refreshRunSummaryTable() {
        summaryTable.getItems().clear();
        String selectedLine = filterLineComboBox.getValue();
        String selectedConvoy = filterConvoyComboBox.getValue();
        String selectedOperator = filterOperatorComboBox.getValue();
        String selectedFirstStation = filterFirstStationComboBox.getValue();
        if ("-------".equals(selectedLine)) selectedLine = null;
        if ("-------".equals(selectedConvoy)) selectedConvoy = null;
        if ("-------".equals(selectedOperator)) selectedOperator = null;
        if (selectedFirstStation == null || "-------".equals(selectedFirstStation)) selectedFirstStation = null;
        java.time.LocalDate selectedDate = filterDatePicker.getValue();
        if (selectedDate == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Attenzione");
            alert.setHeaderText(null);
            alert.setContentText("Seleziona un giorno per la ricerca delle corse.");
            alert.showAndWait();
            return;
        }
        java.sql.Timestamp dayStart = java.sql.Timestamp.valueOf(selectedDate.atStartOfDay());
        java.sql.Timestamp dayEnd = java.sql.Timestamp.valueOf(selectedDate.atTime(23, 59, 59, 999_000_000));
        List<Run> runs;
        try {
            runs = manageRunService.searchRunsByDay(selectedLine, selectedConvoy, selectedOperator, selectedFirstStation, dayStart, dayEnd);
        } catch (Exception e) {
            logger.severe("Errore SQL durante il caricamento delle corse: " + e.getMessage());
            runs = List.of();
        }
        summaryTable.getItems().setAll(runs);
    }
}
