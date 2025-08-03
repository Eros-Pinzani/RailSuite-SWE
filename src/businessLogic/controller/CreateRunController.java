package businessLogic.controller;

import businessLogic.service.CreateRunService;
import domain.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import domain.DTO.StaffDTO;

/**
 * Controller for the Create Run view.
 * Handles UI logic and interactions for creating a new run.
 */
public class CreateRunController {
    @FXML
    private ComboBox<LineRaw> lineComboBox;
    @FXML
    private ComboBox<String> directionComboBox;
    private EventHandler<ActionEvent> directionComboHandler;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> departureTimePicker;
    private Duration travelTime;
    @FXML
    private ComboBox<String> typeOfConvoyComboBox;
    private EventHandler<ActionEvent> typeOfConvoyComboHandler;
    @FXML
    private ComboBox<Convoy> convoyComboBox;
    private EventHandler<ActionEvent> convoyComboHandler;
    @FXML
    private ComboBox<StaffDTO> operatorComboBox;
    @FXML
    private Label headStationLabel;
    @FXML
    private Button createRunButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label supervisorNameLabel;
    @FXML
    private MenuButton menuButton;
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
    private TableView<?> percorsoTableView; // Da tipizzare se hai una classe per il percorso
    @FXML
    private TableColumn<?, ?> stazioniColumn;
    @FXML
    private TableColumn<?, ?> arrivoColumn;
    @FXML
    private TableColumn<?, ?> partenzaColumn;
    @FXML
    private TableView<?> carriagesTableView; // Da tipizzare se hai una classe per le carrozze
    @FXML
    private TableColumn<?, ?> carriageIdColumn;
    @FXML
    private TableColumn<?, ?> carriageModelColumn;
    @FXML
    private TableColumn<?, ?> carriageCapacityColumn;

    private final CreateRunService createRunService = new CreateRunService();
    private static final Logger logger = Logger.getLogger(CreateRunController.class.getName());

    /**
     * Sets up the header and menu actions for the supervisor.
     * @param supervisorNameLabel the label for supervisor name
     * @param logoutMenuItem the logout menu item
     * @param exitMenuItem the exit menu item
     */
    public static void header(Label supervisorNameLabel, MenuItem logoutMenuItem, MenuItem exitMenuItem) {
        if (businessLogic.controller.UserSession.getInstance().getStaff() != null) {
            supervisorNameLabel.setText(businessLogic.controller.UserSession.getInstance().getStaff().getName() + " " + businessLogic.controller.UserSession.getInstance().getStaff().getSurname());
        }
        logoutMenuItem.setOnAction(e -> businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml"));
        exitMenuItem.setOnAction(e -> System.exit(0));
    }

    /**
     * Initializes the UI components and sets up event handlers.
     */
    @FXML
    public void initialize() {
        header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        try {
            lineComboBox.setItems(FXCollections.observableArrayList(createRunService.getAllLines()));
            directionComboBox.setDisable(true);
            datePicker.setDayCellFactory(createRunService.getDateCellFactory());
            departureTimePicker.setItems(FXCollections.observableArrayList(createRunService.getAvailableDepartureTimes()));
            convoyComboBox.setCellFactory(lv -> new ListCell<Convoy>() {
                @Override
                protected void updateItem(Convoy item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        int capacity = item.getCarriages().stream().mapToInt(Carriage::getCapacity).sum();
                        setText(item.getCarriages().getFirst().getModelType() + " - " + capacity);
                    }
                }
            });
            convoyComboBox.setButtonCell(convoyComboBox.getCellFactory().call(null));
        } catch (Exception e) {
            logger.severe("Error initializing fields: " + e.getMessage());
        }
        lineComboBox.setOnAction(e -> onLineSelected());
        directionComboHandler = e -> updatePoolsAvailability();
        directionComboBox.setOnAction(directionComboHandler);
        datePicker.setOnAction(e -> updatePoolsAvailability());
        departureTimePicker.setOnAction(e -> updatePoolsAvailability());

        // Second set of filters
        typeOfConvoyComboHandler = e -> updateConvoyAvailability();
        typeOfConvoyComboBox.setOnAction(typeOfConvoyComboHandler);
        convoyComboHandler = e -> updateRecapLabels();
        convoyComboBox.setOnAction(convoyComboHandler);
        typeOfConvoyComboBox.setOnAction(typeOfConvoyComboHandler);
        operatorComboBox.setOnAction(e -> updateRecapLabels());

        typeOfConvoyComboBox.setDisable(true);
        convoyComboBox.setDisable(true);
        operatorComboBox.setDisable(true);
    }

    private void onLineSelected() {
        directionComboBox.setOnAction(null);
        LineRaw selectedLine = lineComboBox.getValue();
        if (selectedLine != null) {
            directionComboBox.setDisable(false);
            directionComboBox.setItems(FXCollections.observableArrayList(selectedLine.getFirstStationLocation(), selectedLine.getLastStationLocation()));
            directionComboBox.getSelectionModel().selectFirst();
            calculateTravelTimeAsync(selectedLine);
        } else {
            directionComboBox.setDisable(true);
            headStationLabel.setText("");
            convoyComboBox.getItems().clear();
            operatorComboBox.getItems().clear();
            travelTime = null;
        }
        directionComboBox.setOnAction(directionComboHandler);
    }

    /**
     * Calculates travel time asynchronously for the selected line.
     * @param selectedLine the selected line
     */
    private void calculateTravelTimeAsync(LineRaw selectedLine) {
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
                travelTime = getValue();
            }
        };
        new Thread(task).start();
    }

    /**
     * Updates the available pools and operators based on selected filters.
     */
    private void updatePoolsAvailability() {
        LineRaw selectedLine = lineComboBox.getValue();
        String direction = directionComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        int idStation;
        if (selectedLine != null && direction != null && date != null && time != null) {
            if (selectedLine.getFirstStationLocation().equals(direction)) idStation = selectedLine.getIdFirstStation();
            else if (selectedLine.getLastStationLocation().equals(direction))
                idStation = selectedLine.getIdLastStation();
            else {
                errorLabel.setText("Direzione non valida per la linea selezionata.");
                return;
            }
            createRunService.setConvoyPools(idStation, time, date, selectedLine.getIdLine());
            typeOfConvoyComboBox.setItems(FXCollections.observableArrayList(
                    createRunService.getConvoyTypes(createRunService.getConvoysPoolAvailable())));
            List<StaffDTO> operators = createRunService.getStaffPools(idStation, date, time);
            operatorComboBox.setItems(FXCollections.observableArrayList(operators));
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
        //TODO: implementare recap
        StaffDTO staff = operatorComboBox.getValue();
        Convoy convoy = convoyComboBox.getValue();
        staffNameSurnameRecap.setText(staff != null ? staff.getStaffNameSurname() : "");
        convoyIdRecap.setText(convoy != null ? String.valueOf(convoy.getId()) : "");
    }
}
