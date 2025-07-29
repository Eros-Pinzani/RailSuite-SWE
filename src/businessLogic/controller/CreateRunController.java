package businessLogic.controller;

import businessLogic.service.CreateRunService;
import businessLogic.service.LineService;
import businessLogic.service.ConvoyService;
import businessLogic.service.StaffService;
import domain.Line;
import domain.Convoy;
import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

public class CreateRunController {
    @FXML private ComboBox<Line> lineComboBox;
    @FXML private ComboBox<String> directionComboBox;
    @FXML private Label headStationLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> departureTimePicker;
    @FXML private ComboBox<Convoy> convoyComboBox;
    @FXML private ComboBox<Staff> operatorComboBox;
    @FXML private Button createRunButton;
    @FXML private Label errorLabel;
    @FXML private Label supervisorNameLabel;
    @FXML private MenuButton menuButton;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Button backButton;

    private final CreateRunService createRunService = new CreateRunService();
    private final LineService lineService = new LineService();
    private final ConvoyService convoyService = new ConvoyService();
    private final StaffService staffService = new StaffService();
    private static final Logger logger = Logger.getLogger(CreateRunController.class.getName());

    @FXML
    public void initialize() {
        // Header setup (opzionale: copia da ManageConvoyController se serve)
        // ...
        try {
            lineComboBox.setItems(FXCollections.observableArrayList(lineService.getAllLines()));
            directionComboBox.setItems(FXCollections.observableArrayList("Andata", "Ritorno"));
            datePicker.setDayCellFactory(createRunService.getDateCellFactory());
            departureTimePicker.setItems(FXCollections.observableArrayList(createRunService.getAvailableDepartureTimes()));
        } catch (Exception e) {
            logger.severe("Errore inizializzazione campi: " + e.getMessage());
        }
        lineComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Line item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        lineComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Line item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        lineComboBox.setOnAction(e -> updateHeadStationAndAvailability());
        directionComboBox.setOnAction(e -> updateHeadStationAndAvailability());
        datePicker.setOnAction(e -> updateAvailability());
        departureTimePicker.setOnAction(e -> updateAvailability());
        createRunButton.setOnAction(e -> handleCreateRun());
        backButton.setOnAction(e -> createRunService.goBackToManageRun());
    }

    private void updateHeadStationAndAvailability() {
        Line selectedLine = lineComboBox.getValue();
        String direction = directionComboBox.getValue();
        if (selectedLine != null && direction != null) {
            String headStation = createRunService.getHeadStationName(selectedLine, direction);
            headStationLabel.setText(headStation);
        } else {
            headStationLabel.setText("");
        }
        updateAvailability();
    }

    private void updateAvailability() {
        Line selectedLine = lineComboBox.getValue();
        String direction = directionComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        if (selectedLine != null && direction != null && date != null && time != null) {
            List<Convoy> convoys = createRunService.getAvailableConvoys(selectedLine, direction, date, time);
            convoyComboBox.setItems(FXCollections.observableArrayList(convoys));
            List<Staff> operators = createRunService.getAvailableOperators(selectedLine, direction, date, time);
            operatorComboBox.setItems(FXCollections.observableArrayList(operators));
        } else {
            convoyComboBox.getItems().clear();
            operatorComboBox.getItems().clear();
        }
    }

    private void handleCreateRun() {
        errorLabel.setText("");
        Line selectedLine = lineComboBox.getValue();
        String direction = directionComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = departureTimePicker.getValue();
        Convoy convoy = convoyComboBox.getValue();
        Staff operator = operatorComboBox.getValue();
        if (selectedLine == null || direction == null || date == null || time == null || convoy == null || operator == null) {
            errorLabel.setText("Tutti i campi sono obbligatori.");
            return;
        }
        String result = createRunService.createRun(selectedLine, direction, date, time, convoy, operator);
        if (result == null) {
            createRunService.goBackToManageRun();
        } else {
            errorLabel.setText(result);
        }
    }
}
