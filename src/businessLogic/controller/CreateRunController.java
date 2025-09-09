package businessLogic.controller;

import businessLogic.service.CreateRunService;
import domain.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import domain.DTO.StaffDTO;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the Create Run2 view.
 * Handles UI logic and interactions for creating a new run.
 */
public class CreateRunController {
    @FXML
    private ComboBox<Line> lineComboBox;
    @FXML
    private ComboBox<String> startStationComboBox;
    private EventHandler<ActionEvent> startStationComboHandler;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> departureTimePicker;
    @FXML
    private ComboBox<String> typeOfConvoyComboBox;
    private EventHandler<ActionEvent> typeOfConvoyComboHandler;
    @FXML
    private ComboBox<Convoy> convoyComboBox;
    private EventHandler<ActionEvent> convoyComboHandler;
    @FXML
    private ComboBox<StaffDTO> operatorComboBox;
    @FXML
    private Button recapButton;
    @FXML
    private Button createRunButton;
    @FXML
    private MenuButton menuButton;
    @FXML
    private Label supervisorNameLabel;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private Button backButton;
    @FXML
    private Label staffNameSurnameRecap;
    @FXML
    private Label convoyIdRecap;
    @FXML
    private Label dateRecap;
    @FXML
    private Label timeRecap;
    @FXML
    private TableView<TimeTable.StationArrAndDep> routeTableView;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> stationColumn;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> arriveColumn;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> departureColumn;
    @FXML
    private TableView<domain.Carriage> carriagesTableView;
    @FXML
    private TableColumn<domain.Carriage, Number> carriageIdColumn;
    @FXML
    private TableColumn<domain.Carriage, String> carriageModelColumn;
    @FXML
    private TableColumn<domain.Carriage, Number> carriageCapacityColumn;

    private final CreateRunService createRunService = new CreateRunService();
    private static final Logger logger = Logger.getLogger(CreateRunController.class.getName());

    /**
     * Sets up the header and menu actions for the supervisor.
     *
     * @param supervisorNameLabel the label for supervisor name
     * @param logoutMenuItem      the logout menu item
     * @param exitMenuItem        the exit menu item
     */
    public static void header(Label supervisorNameLabel, MenuItem logoutMenuItem, MenuItem exitMenuItem) {
        if (UserSession.getInstance().getStaff() != null) {
            supervisorNameLabel.setText(UserSession.getInstance().getStaff().getName() + " " + UserSession.getInstance().getStaff().getSurname());
        }
        logoutMenuItem.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml"));
        exitMenuItem.setOnAction(e -> System.exit(0));
    }

    /**
     * Initializes the UI components and sets up event handlers.
     */
    @FXML
    public void initialize() {
        header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        try {
            // Line: mostra solo nomi unici, ma salva l'oggetto domain.LineRaw
            List<Line> allLines = createRunService.getAllLines();
            List<String> uniqueLineNames = new java.util.ArrayList<>();
            List<Line> uniqueLines = new java.util.ArrayList<>();
            for (Line line : allLines) {
                if (!uniqueLineNames.contains(line.getLineName())) {
                    uniqueLineNames.add(line.getLineName());
                    uniqueLines.add(line);
                }
            }
            lineComboBox.setItems(FXCollections.observableArrayList(uniqueLines));
            lineComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Line item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getLineName());
                }
            });
            lineComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Line item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getLineName());
                }
            });
            // Operatore disponibile: mostra domain.DTO.StaffDTO
            operatorComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(StaffDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getStaffNameSurname());
                }
            });
            operatorComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(StaffDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getStaffNameSurname());
                }
            });
            convoyComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Convoy item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        int capacity = item.getCarriages().stream().mapToInt(Carriage::getCapacity).sum();
                        setText(item.getCarriages().getFirst().getModel() + " - " + capacity);
                    }
                }
            });
            convoyComboBox.setButtonCell(convoyComboBox.getCellFactory().call(null));
            stationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStationName()));
            arriveColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArriveTime()));
            departureColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartureTime()));
            carriageIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()));
            carriageModelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModel()));
            carriageCapacityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacity()));

            stationColumn.setCellFactory(tc -> new TableCell<TimeTable.StationArrAndDep, String>() {
                private final Label label = new Label();
                {
                    label.setWrapText(true);
                    label.setMaxWidth(150);
                    setGraphic(label);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        label.setText("");
                    } else {
                        label.setText(item);
                    }
                }
            });
        } catch (Exception e) {
            logger.severe("Error initializing fields: " + e.getMessage());
        }
        lineComboBox.setOnAction(e -> onLineSelected());
        startStationComboHandler = e -> updatePoolsAvailability();
        startStationComboBox.setOnAction(startStationComboHandler);
        // Aggiorna la ComboBox degli orari quando cambia la data
        datePicker.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            departureTimePicker.setItems(FXCollections.observableArrayList(createRunService.getAvailableDepartureTimes(selectedDate)));
            departureTimePicker.getSelectionModel().clearSelection();
            updatePoolsAvailability();
        });
        departureTimePicker.setOnAction(e -> updatePoolsAvailability());

        // Second set of filters
        typeOfConvoyComboHandler = e -> updateConvoyAvailability();
        convoyComboHandler = e -> {
        };
        convoyComboBox.setOnAction(convoyComboHandler);
        typeOfConvoyComboBox.setOnAction(typeOfConvoyComboHandler);
        operatorComboBox.setOnAction(e -> {
        });

        typeOfConvoyComboBox.setDisable(true);
        convoyComboBox.setDisable(true);
        operatorComboBox.setDisable(true);
        backButton.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml"));
    }

    private void onLineSelected() {
        startStationComboBox.setOnAction(null);
        Line selectedLine = lineComboBox.getValue();
        if (selectedLine != null) {
            startStationComboBox.setDisable(false);
            startStationComboBox.setItems(FXCollections.observableArrayList(selectedLine.getFirstStationLocation(), selectedLine.getLastStationLocation()));
            startStationComboBox.getSelectionModel().selectFirst();
            calculateTravelTimeAsync(selectedLine);
        } else {
            startStationComboBox.setDisable(true);
            convoyComboBox.getItems().clear();
            operatorComboBox.getItems().clear();
            createRunService.setTravelTime(null);
        }
        startStationComboBox.setOnAction(startStationComboHandler);
    }

    /**
     * Calculates travel time asynchronously for the selected line.
     *
     * @param selectedLine the selected line
     */
    private void calculateTravelTimeAsync(Line selectedLine) {
        Task<Duration> task = new Task<>() {
            @Override
            protected Duration call() {
                try {
                    return createRunService.calculateTravelTime(selectedLine);
                } catch (Exception e) {
                    logger.severe("Error during travel time calculation: " + e.getMessage());
                    return Duration.ZERO;
                }
            }

            @Override
            protected void succeeded() {
                createRunService.setTravelTime(getValue());
            }
        };
        new Thread(task).start();
    }

    /**
     * Mostra un popup di errore con il messaggio specificato.
     */
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the available pools and operators based on selected filters.
     */
    private void updatePoolsAvailability() {
        Line selectedLine = lineComboBox.getValue();
        String startStation = startStationComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        int idStation;
        if (selectedLine != null && startStation != null && date != null && time != null) {
            if (selectedLine.getFirstStationLocation().equals(startStation))
                idStation = selectedLine.getIdFirstStation();
            else if (selectedLine.getLastStationLocation().equals(startStation))
                idStation = selectedLine.getIdLastStation();
            else {
                showErrorPopup("Server error - Seleziona una direzione valida.");
                return;
            }
            createRunService.setConvoyPools(idStation, time, date, selectedLine.getIdLine());
            List<Convoy> convoys = createRunService.getConvoysPoolAvailable();
            if (convoys.isEmpty()) {
                typeOfConvoyComboBox.getItems().clear();
                convoyComboBox.getItems().clear();
                typeOfConvoyComboBox.setDisable(true);
                convoyComboBox.setDisable(true);
                showErrorPopup("Nessun convoglio disponibile per i filtri selezionati.");
            } else {
                typeOfConvoyComboBox.setItems(FXCollections.observableArrayList(
                        createRunService.getConvoyTypes(convoys)));
                typeOfConvoyComboBox.setDisable(false);
            }
            List<StaffDTO> operators = createRunService.getStaffPools(idStation, date, time);
            if (operators.isEmpty()) {
                operatorComboBox.getItems().clear();
                operatorComboBox.setDisable(true);
                showErrorPopup("Nessun operatore disponibile per i filtri selezionati.");
            } else {
                operatorComboBox.setItems(FXCollections.observableArrayList(operators));
                operatorComboBox.setDisable(false);
            }
        } else {
            typeOfConvoyComboBox.getItems().clear();
            convoyComboBox.getItems().clear();
            operatorComboBox.getItems().clear();
            typeOfConvoyComboBox.setDisable(true);
            convoyComboBox.setDisable(true);
            operatorComboBox.setDisable(true);
        }
    }

    /**
     * Updates the available convoys based on selected type.
     */
    private void updateConvoyAvailability() {
        convoyComboBox.setOnAction(null);
        String selectedType = typeOfConvoyComboBox.getValue();
        if (selectedType != null && !createRunService.getConvoysPoolAvailable().isEmpty()) {
            createRunService.setAvailableConvoysFilteredByType(selectedType, createRunService.getConvoysPoolAvailable());
            convoyComboBox.setItems(FXCollections.observableArrayList(
                    createRunService.getConvoysPoolAvailableFilteredByType()
            ));
            convoyComboBox.setDisable(false);
        } else {
            convoyComboBox.getItems().clear();
            convoyComboBox.setDisable(true);
        }
        convoyComboBox.setOnAction(convoyComboHandler);
    }

    /**
     * Updates the recap labels for staff and convoy selection.
     */
    private void updateRecapLabels() {
        if (checkRecap()) {
            StaffDTO staff = operatorComboBox.getValue();
            Convoy convoy = convoyComboBox.getValue();
            staffNameSurnameRecap.setText(staff != null ? staff.getStaffNameSurname() : "");
            convoyIdRecap.setText(convoy != null ? String.valueOf(convoy.getId()) : "");
            dateRecap.setText(datePicker.getValue() != null ? datePicker.getValue().toString() : "");
            timeRecap.setText(departureTimePicker.getValue() != null ? departureTimePicker.getValue() : "");

            Line selectedLine = lineComboBox.getValue();
            String startStation = startStationComboBox.getValue();
            String time = departureTimePicker.getValue();
            if (selectedLine != null && startStation != null && time != null) {
                int idLine = selectedLine.getIdLine();
                int idStartStation = selectedLine.getFirstStationLocation().equals(startStation) ? selectedLine.getIdFirstStation() : selectedLine.getIdLastStation();
                List<TimeTable.StationArrAndDep> rows = createRunService.getTimeTableForRun(idLine, idStartStation, time);
                routeTableView.setItems(FXCollections.observableArrayList(rows));
                boolean hasZeroDuration = false;
                for (int i = 1; i < rows.size(); i++) {
                    if (rows.get(i).getArriveTime().equals(rows.get(i).getDepartureTime()) && !rows.get(i).getArriveTime().equals("------")) {
                        hasZeroDuration = true;
                        break;
                    }
                }
                if (hasZeroDuration) {
                    showErrorPopup("Attenzione: alcuni tempi di percorrenza tra le stazioni non sono disponibili. Il percorso potrebbe essere incompleto.");
                }
            } else {
                routeTableView.getItems().clear();
                showErrorPopup("Errore nel recupero della tabella del percorso. Assicurati di aver selezionato una linea, una stazione di partenza e un orario di partenza validi.");
            }

            if (convoy != null) {
                carriagesTableView.setItems(FXCollections.observableArrayList(convoy.getCarriages()));
            } else {
                carriagesTableView.getItems().clear();
                showErrorPopup("Errore nel recupero dei vagoni del convoglio. Assicurati di aver selezionato un convoglio valido.");
            }
        } else {
            routeTableView.getItems().clear();
            carriagesTableView.getItems().clear();
        }
    }

    private boolean checkRecap() {
        Line selectedLine = lineComboBox.getValue();
        String direction = startStationComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        String type = typeOfConvoyComboBox.getValue();
        Convoy convoy = convoyComboBox.getValue();
        StaffDTO operator = operatorComboBox.getValue();

        if (selectedLine == null) {
            showErrorPopup("Seleziona una linea.");
            return false;
        }
        if (direction == null) {
            showErrorPopup("Seleziona una direzione.");
            return false;
        }
        if (date == null) {
            showErrorPopup("Seleziona una data di partenza.");
            return false;
        }
        if (time == null) {
            showErrorPopup("Seleziona un orario di partenza.");
            return false;
        }
        if (type == null) {
            showErrorPopup("Seleziona il tipo di convoglio.");
            return false;
        }
        if (convoy == null) {
            showErrorPopup("Seleziona un convoglio disponibile.");
            return false;
        }
        if (operator == null) {
            showErrorPopup("Seleziona un operatore disponibile.");
            return false;
        }
        return true;
    }

    @FXML
    private void recapButton(ActionEvent event) {
        updateRecapLabels();
    }

    @FXML
    private void handleCreateRun(ActionEvent event) {
        Line selectedLine = lineComboBox.getValue();
        String direction = startStationComboBox.getValue();
        int idStartStation = selectedLine.getFirstStationLocation().equals(direction) ? selectedLine.getIdFirstStation() : selectedLine.getIdLastStation();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        Convoy convoy = convoyComboBox.getValue();
        StaffDTO operator = operatorComboBox.getValue();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(250);
        Label loadingLabel = new Label("Creazione corsa in corso...");
        VBox vbox = new VBox(10, loadingLabel, progressBar);
        vbox.setPadding(new javafx.geometry.Insets(20));
        Stage loadingStage = new Stage();
        loadingStage.setTitle("Creazione corsa");
        loadingStage.setScene(new Scene(vbox, 350, 100));
        loadingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        loadingStage.setResizable(false);
        loadingStage.centerOnScreen();
        loadingStage.show();
        calculateTravelTimeAndCreateRun(selectedLine, idStartStation, date, time, convoy, operator, progressBar, loadingStage);
    }

    private void calculateTravelTimeAndCreateRun(Line selectedLine, int idStartStation, LocalDate date, String time, Convoy convoy, StaffDTO operator, ProgressBar progressBar, Stage loadingStage) {
        javafx.concurrent.Task<Run> task = new javafx.concurrent.Task<>() {
            @Override
            protected Run call() {
                updateProgress(0.2, 1.0);
                createRunService.waitForTravelTime(selectedLine);
                updateProgress(0.7, 1.0);
                Run run = createRunService.createRun(selectedLine, idStartStation, date, time, convoy, operator);
                updateProgress(1.0, 1.0);
                return run;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(ignored -> {
            List<Button> buttons = new ArrayList<>();
            Button btnHome = new Button("Torna alla home");
            btnHome.setUserData((Runnable) () -> SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
            Button btnManage = new Button("Torna alla gestione corse");
            btnManage.setUserData((Runnable) () -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml"));
            buttons.add(btnHome);
            buttons.add(btnManage);
            VBox popupContent = PopupManager.buildPopupContent(null, "Corsa creata con successo!", buttons);
            loadingStage.setScene(new Scene(popupContent, 400, 200));
            for (Button btn : buttons) {
                btn.setOnAction(e -> {
                    loadingStage.close();
                    Object data = btn.getUserData();
                    if (data instanceof Runnable runnable) {
                        runnable.run();
                    }
                });
            }
        });
        task.setOnFailed(ignored -> {
            List<Button> buttons = new ArrayList<>();
            Button btnClose = new Button("Chiudi");
            buttons.add(btnClose);
            VBox popupContent = PopupManager.buildPopupContent(null, "Errore durante la creazione della corsa: " + (task.getException() != null ? task.getException().getMessage() : "Errore sconosciuto"), buttons);
            loadingStage.setScene(new Scene(popupContent, 400, 200));
            for (Button btn : buttons) {
                btn.setOnAction(e -> loadingStage.close());
            }
        });
        new Thread(task).start();
    }
}
