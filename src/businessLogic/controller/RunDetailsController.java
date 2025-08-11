package businessLogic.controller;

import domain.Carriage;
import domain.DTO.RunDTO;
import domain.DTO.TimeTableDTO;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import businessLogic.controller.UserSession;
import domain.Staff;
import javafx.scene.control.Alert.AlertType;
import businessLogic.service.RunDetailsService;
import domain.Run;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RunDetailsController implements Initializable {
    // Header
    @FXML private Button backButton;
    @FXML private Label supervisorNameLabel;
    @FXML private MenuButton menuButton;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;

    // Sezioni dettagli
    @FXML private VBox generalDetailSection;
    @FXML private VBox convoyDetailSection;
    @FXML private VBox operatorDetailSection;
    @FXML private VBox timeTableDetailSection;

    // General Detail Section
    @FXML private Label firstStation;
    @FXML private Label startAtDateTime;
    @FXML private Label staffNameSurname;
    @FXML private Button eliminationConfirm;
    @FXML private Label eliminationButtonMention;

    // Convoy Detail Section
    @FXML private Label typeOfConvoy;
    @FXML private Label modelOfConvoy;
    @FXML private Label capacityOfConvoy;
    @FXML private Label numberOfCarriage;
    @FXML private Label convoyChangeEditButtonReason;
    @FXML private Button convoyEdit;
    @FXML private Button convoyChange;

    // Operator Detail Section
    @FXML private Label staffName;
    @FXML private Label staffSurname;
    @FXML private Label staffEmail;
    @FXML private Label operatorChangeEditButtonReason;
    @FXML private Button operatorChange;

    // TimeTable Detail Section
    @FXML private Label runDate;
    @FXML private Label lineName;
    @FXML private Label timeTableChangeEditButtonReason;
    @FXML private Button runTimeEdit;
    @FXML private TableView<TimeTableDTO.StationArrAndDepDTO> timeTableView;
    @FXML private TableColumn<TimeTableDTO.StationArrAndDepDTO, String> stationColumn;
    @FXML private TableColumn<TimeTableDTO.StationArrAndDepDTO, String> arriveColumn;
    @FXML private TableColumn<TimeTableDTO.StationArrAndDepDTO, String> departureColumn;

    // Toggle Section Buttons
    @FXML private Button convoyDetailSectionToggle;
    @FXML private Button operatorDetailSectionToggle;
    @FXML private Button timeTableDetailSectionToggle;

    // Altri
    @FXML private Button listOfIdCarriages;

    private final RunDetailsService runDetailsService = new RunDetailsService();
    private static final Logger logger = Logger.getLogger(RunDetailsController.class.getName());


    public static void header(Label supervisorNameLabel, MenuItem logoutMenuItem, MenuItem exitMenuItem) {
        CreateRunController.header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
    }

    private int idLine;
    private int idConvoy;
    private int idStaff;
    private Timestamp timeDeparture;
    private int idFirstStation;

    public void setRunParams(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, int idFirstStation) {
        this.idLine = idLine;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.timeDeparture = timeDeparture;
        this.idFirstStation = idFirstStation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            backButton.setOnAction(e -> {
                switch (staff.getTypeOfStaff()) {
                    case SUPERVISOR -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml");
                    case OPERATOR -> SceneManager.getInstance().switchScene("/businessLogic/fxml/OperatorHome.fxml");
                }
            });
        }
        RunDTO run = null;
        try {
            run = runDetailsService.selectRun(idLine, idConvoy, idStaff, timeDeparture);
        } catch (Exception e) {
            logger.severe("Error loading run details: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR, "Errore nel caricamento dei dettagli della corsa.");
            alert.showAndWait();
        }
        if (run != null) {
            firstStation.setText(run.getFirstStationName());
            startAtDateTime.setText(run.getTimeDeparture().toLocalDateTime().toString());
            staffNameSurname.setText(run.getStaffName() + " " + run.getStaffSurname());
            staffName.setText(run.getStaffName());
            staffSurname.setText(run.getStaffSurname());
            staffEmail.setText(run.getStaffEmail());
            lineName.setText(run.getLineName());

            boolean isEditable = LocalDateTime.now().isBefore(run.getTimeDeparture().toLocalDateTime().minusMinutes(15));
            boolean hasOperatorConflict = runDetailsService.hasOperatorConflicts(idStaff, run.getTimeDeparture());
            operatorChange.setDisable(!isEditable || hasOperatorConflict);
            if (!isEditable) {
                operatorChangeEditButtonReason.setText("Non puoi cambiare l'operatore: il tempo limite per la modifica è scaduto (sono richiesti almeno 15 minuti di anticipo rispetto alla partenza).");
            } else if (hasOperatorConflict) {
                operatorChangeEditButtonReason.setText("Non puoi cambiare l'operatore: l'operatore selezionato ha altre corse programmate dopo questa.");
            } else {
                operatorChangeEditButtonReason.setText("Cambio operatore possibile.");
            }

            eliminationConfirm.setDisable(!isEditable);
            convoyEdit.setDisable(!isEditable);
            operatorChange.setDisable(!isEditable);
            runTimeEdit.setDisable(!isEditable);
            if (!isEditable) {
                eliminationButtonMention.setText("Non puoi eliminare la corsa: il tempo limite per la modifica è scaduto (sono richiesti almeno 15 minuti di anticipo rispetto alla partenza)." );
            } else {
                eliminationButtonMention.setText("Attenzione: l'eliminazione potrebbe generare conflitti con altre corse o prenotazioni.");
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR, "Nessuna corsa selezionata.");
            alert.showAndWait();
            eliminationConfirm.setDisable(true);
            convoyEdit.setDisable(true);
            operatorChange.setDisable(true);
            runTimeEdit.setDisable(true);
            eliminationButtonMention.setText("Nessuna corsa selezionata.");
            return;
        }
        taskInitConvoy(idConvoy);
        String departureTime = timeDeparture.toLocalDateTime().toLocalTime().toString();
        taskInitTimeTable(idLine, idFirstStation, departureTime);

        stationColumn.setCellFactory(tc -> {
            TableCell<TimeTableDTO.StationArrAndDepDTO, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setWrapText(true);
                        setStyle("-fx-label-padding: 4; -fx-text-alignment: left;");
                    }
                }
            };
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            return cell;
        });
        arriveColumn.setCellFactory(tc -> {
            TableCell<TimeTableDTO.StationArrAndDepDTO, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setWrapText(true);
                        setStyle("-fx-label-padding: 4; -fx-text-alignment: left;");
                    }
                }
            };
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            return cell;
        });
        departureColumn.setCellFactory(tc -> {
            TableCell<TimeTableDTO.StationArrAndDepDTO, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setWrapText(true);
                        setStyle("-fx-label-padding: 4; -fx-text-alignment: left;");
                    }
                }
            };
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            return cell;
        });
    }

    private void taskInitConvoy(int idConvoy) {
        Task<domain.Convoy> task = new Task<>() {
            @Override
            protected domain.Convoy call() {
                return runDetailsService.selectConvoy(idConvoy);
            }
        };
        task.setOnSucceeded(e -> {
            domain.Convoy convoy = task.getValue();
            if (convoy != null) {
                typeOfConvoy.setText(convoy.getCarriages().isEmpty() ? "" : convoy.getCarriages().getFirst().getModelType());
                modelOfConvoy.setText(convoy.getCarriages().isEmpty() ? "" : convoy.getCarriages().getFirst().getModel());
                capacityOfConvoy.setText(String.valueOf(convoy.getCarriages().stream().mapToInt(Carriage::getCapacity).sum()));
                numberOfCarriage.setText(String.valueOf(convoy.getCarriages().size()));
            }
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) {
                logger.severe("Error loading convoy details: " + ex.getMessage());
            }
            Alert alert = new Alert(AlertType.ERROR, "Errore nel caricamento dei dettagli del convoglio.");
            alert.showAndWait();
        });
        new Thread(task).start();
    }

    private void taskInitTimeTable(int idLine, int idFirstStation, String departureTime) {
        Task<TimeTableDTO> task = new Task<>() {
            @Override
            protected TimeTableDTO call() {
                return runDetailsService.selectTimeTable(idLine, idFirstStation, departureTime);
            }
        };
        task.setOnSucceeded(e -> {
            TimeTableDTO timeTable = task.getValue();
            if (timeTable != null) {
                timeTableView.getItems().clear();
                timeTableView.setItems(FXCollections.observableArrayList(timeTable.getStationArrAndDepDTOList()));
            }
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) {
                logger.severe("Error loading timetable: " + ex.getMessage());
            }
            Alert alert = new Alert(AlertType.ERROR, "Errore nel caricamento della tabella orari.");
            alert.showAndWait();
        });
        new Thread(task).start();
    }

    // Metodi OnAction
    @FXML
    private void convoyDetailSectionToggle() {
        // TODO: Implementare toggle visualizzazione sezione dettagli convoglio
    }

    @FXML
    private void operatorDetailSectionToggle() {
        // TODO: Implementare toggle visualizzazione sezione dettagli operatore
    }

    @FXML
    private void timeTableDetailSectionToggle() {
        // TODO: Implementare toggle visualizzazione sezione dettagli orario/stazioni
    }

    @FXML
    private void eliminationConfirm() {
        // TODO: Implementare conferma eliminazione corsa
    }

    @FXML
    private void convoyEdit() {
        // TODO: Implementare modifica convoglio
    }

    @FXML
    private void convoyChange() {
        // TODO: Implementare cambio convoglio
    }

    @FXML
    private void operatorChange() {
        // TODO: Implementare cambio operatore
    }

    @FXML
    private void runTimeEdit() {
        // TODO: Implementare modifica orario di partenza
    }

    @FXML
    private void listOfIdCarriages() {
        // TODO: Implementare visualizzazione lista ID carrozze
    }
}
