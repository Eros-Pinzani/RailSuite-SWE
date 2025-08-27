package businessLogic.controller;

import domain.Carriage;
import domain.Convoy;
import domain.DTO.ConvoyTableDTO;
import domain.DTO.RunDTO;
import domain.TimeTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import domain.Staff;
import javafx.scene.control.Alert.AlertType;
import businessLogic.service.RunDetailsService;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RunDetailsController implements Initializable {
    // Header
    @FXML
    private Button backButton;
    @FXML
    private Label supervisorNameLabel;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;

    // Sezioni dettagli
    @FXML
    private VBox generalDetailSection;
    @FXML
    private VBox convoyDetailSection;
    @FXML
    private VBox operatorDetailSection;
    @FXML
    private VBox timeTableDetailSection;

    // General Detail Section
    @FXML
    private Label firstStation;
    @FXML
    private Label startAtDateTime;
    @FXML
    private Label staffNameSurname;
    @FXML
    private Button eliminationConfirm;
    @FXML
    private Label eliminationButtonMention;

    // Convoy Detail Section
    @FXML
    private Label typeOfConvoy;
    @FXML
    private Label modelOfConvoy;
    @FXML
    private Label capacityOfConvoy;
    @FXML
    private Label numberOfCarriage;
    @FXML
    private Label convoyChangeEditButtonReason;
    @FXML
    private Button convoyEdit;
    @FXML
    private Button convoyChange;

    // Operator Detail Section
    @FXML
    private Label staffName;
    @FXML
    private Label staffSurname;
    @FXML
    private Label staffEmail;
    @FXML
    private Label operatorChangeEditButtonReason;
    @FXML
    private Button operatorChange;

    // TimeTable Detail Section
    @FXML
    private Label runDate;
    @FXML
    private Label lineName;
    @FXML
    private TableView<TimeTable.StationArrAndDep> timeTableView;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> stationColumn;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> arriveColumn;
    @FXML
    private TableColumn<TimeTable.StationArrAndDep, String> departureColumn;

    // Toggle Section Buttons
    @FXML
    private Button convoyDetailSectionToggle;
    @FXML
    private Button operatorDetailSectionToggle;
    @FXML
    private Button timeTableDetailSectionToggle;

    // Altri
    @FXML
    private Button listOfIdCarriages;
    private final ConvoyEditPopupController convoyEditPopupController = new ConvoyEditPopupController();

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

    private String convoyEditReason = "";
    private String convoyChangeReason = "";

    public void setRunParams(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, int idFirstStation) {
        this.idLine = idLine;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.timeDeparture = timeDeparture;
        this.idFirstStation = idFirstStation;
        loadRunDetails();
    }

    private void loadRunDetails() {
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
            if (!isEditable) {
                eliminationButtonMention.setText("Non puoi eliminare la corsa: il tempo limite per la modifica è scaduto (sono richiesti almeno 15 minuti di anticipo rispetto alla partenza).");
            } else {
                eliminationButtonMention.setText("Attenzione: l'eliminazione potrebbe generare conflitti con altre corse o prenotazioni.");
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR, "Nessuna corsa selezionata.");
            alert.showAndWait();
            eliminationConfirm.setDisable(true);
            convoyEdit.setDisable(true);
            operatorChange.setDisable(true);
            eliminationButtonMention.setText("Nessuna corsa selezionata.");
            return;
        }
        taskInitConvoy(idConvoy);
        String departureTime = timeDeparture.toLocalDateTime().toLocalTime().toString();
        taskInitTimeTable(idLine, idFirstStation, departureTime);
    }

    private void hideSections() {
        convoyDetailSection.setVisible(false);
        convoyDetailSection.setManaged(false);
        operatorDetailSection.setVisible(false);
        operatorDetailSection.setManaged(false);
        timeTableDetailSection.setVisible(false);
        timeTableDetailSection.setManaged(false);
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
        hideSections();
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
        Task<TimeTable> task = new Task<>() {
            @Override
            protected TimeTable call() {
                return runDetailsService.selectTimeTable(idLine, idFirstStation, departureTime);
            }
        };
        task.setOnSucceeded(e -> {
            stationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStationName()));
            arriveColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArriveTime()));
            departureColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartureTime()));

            TimeTable timeTable = task.getValue();
            if (timeTable != null) {
                timeTableView.getItems().clear();
                timeTableView.setItems(FXCollections.observableArrayList(timeTable.getStationArrAndDepList()));
                timeTableDetailSection.setVisible(false);
                timeTableDetailSection.setManaged(false);
            } else {
                logger.warning("Tabella orari non disponibile");
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
        boolean show = !convoyDetailSection.isVisible();
        hideSections();
        convoyDetailSection.setVisible(show);
        convoyDetailSection.setManaged(show);
    }

    @FXML
    private void operatorDetailSectionToggle() {
        boolean show = !operatorDetailSection.isVisible();
        hideSections();
        operatorDetailSection.setVisible(show);
        operatorDetailSection.setManaged(show);
    }

    @FXML
    private void timeTableDetailSectionToggle() {
        boolean show = !timeTableDetailSection.isVisible();
        hideSections();
        timeTableDetailSection.setVisible(show);
        timeTableDetailSection.setManaged(show);
    }

    @FXML
    private void eliminationConfirm() {
        try {
            boolean hasConflicts = runDetailsService.hasRunConflict();
            if (hasConflicts) {
                businessLogic.controller.PopupManager.openPopup(
                        "Eliminazione corsa",
                        "Impossibile eliminare la corsa",
                        null,
                        "Questa corsa non può essere eliminata a causa di conflitti con altre corse.",
                        null
                );
                eliminationConfirm.setDisable(true);
                eliminationButtonMention.setText("Non puoi eliminare la corsa: ci sono conflitti con altre corse" +
                        ".");
                return;
            }
            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("Conferma eliminazione corsa");
            confirmAlert.setHeaderText("Sei sicuro di voler eliminare questa corsa?");
            RunDTO run = runDetailsService.selectRun();
            confirmAlert.setContentText(
                    "Linea: " + run.getLineName() + "\n" +
                            "Convoglio: " + run.getIdConvoy() + "\n" +
                            "Personale: " + run.getStaffName() + " " + run.getStaffSurname() + "\n" +
                            "Partenza: " + run.getTimeDeparture()
            );
            confirmAlert.getDialogPane().setPrefSize(500, 300);
            ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) {
                return;
            }

            boolean deleted = runDetailsService.deleteRun();
            String header = deleted ? "Corsa cancellata con successo" : "Corsa non cancellata";
            String message = deleted ? "La corsa è stata rimossa dal sistema." : "Impossibile cancellare la corsa. Potrebbe essere già stata eliminata o non esistere.";
            businessLogic.controller.PopupManager.openPopup(
                    "Eliminazione corsa",
                    header,
                    null,
                    message,
                    null
            );
            if (deleted) {
                SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml");
            }
        } catch (Exception e) {
            logger.severe("Error deleting run: " + e.getMessage());
            businessLogic.controller.PopupManager.openPopup(
                    "Cancellazione corsa",
                    "Error deleting run",
                    null,
                    "An error occurred while deleting the run.\n Please contact support.",
                    null
            );
        }
    }

    @FXML
    private void convoyEdit() {
        try {
            Convoy convoy = runDetailsService.selectConvoy(idConvoy);
            if (convoy == null || convoy.getCarriages().isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Nessun convoglio associato a questa corsa.");
                alert.showAndWait();
                convoyEditReason = "Nessun convoglio associato a questa corsa.";
                updateConvoyReasonLabel();
                convoyEdit.setDisable(true);
                return;
            }
            if (!runDetailsService.hasConvoyConflict()) {
                Alert alert = new Alert(AlertType.ERROR, "Impossibile modificare il convoglio: Il convoglio è in un'altra corsa.");
                alert.showAndWait();
                convoyEdit.setDisable(true);
                convoyEditReason = "Impossibile modificare il convoglio: Il convoglio è in un'altra corsa.";
                updateConvoyReasonLabel();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/businessLogic/fxml/convoyEditPopup.fxml"));
            Parent root = loader.load();
            ConvoyEditPopupController controller = loader.getController();
            controller.setConvoy(convoy);
            Stage popupStage = new Stage();
            popupStage.setTitle("Modifica convoglio");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
            taskInitConvoy(idConvoy);
        } catch (Exception e) {
            logger.severe("Error loading convoy edit: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR, "Errore durante il caricamento della modifica del convoglio.");
            alert.showAndWait();
        }
    }

    @FXML
    private void convoyChange() {
        try {
            List<ConvoyTableDTO> convoysAvailable = runDetailsService.checkAvailabilityOfConvoy();
            if (convoysAvailable.isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Nessun convoglio disponibile per il cambio.");
                alert.showAndWait();
                convoyChange.setDisable(true);
                convoyChangeReason = "Non è possibilie cambiare convoglio: nessun convoglio disponibile alla stazione di partenza.";
                updateConvoyReasonLabel();
                convoyChange.setDisable(true);
                return;
            }

            ObservableList<ConvoyTableDTO> convoysObservable = FXCollections.observableArrayList(runDetailsService.getFutureRunsOfCurrentConvoy(idConvoy, timeDeparture));
            businessLogic.controller.PopupManager.showChangeConvoyPopup(
                    convoysObservable,
                    "Cambia convoglio",
                    "Cambia convoglio associato alla corsa e alle corse future ad esso assiciato",
                    selectedConvoy -> {
                        if (selectedConvoy != null && selectedConvoy.getIdConvoy() != idConvoy) {
                            int newIdConvoy = selectedConvoy.getIdConvoy();
                            runDetailsService.replaceFutureRunsConvoy(idConvoy, newIdConvoy);
                        }
                        return null;
                    }
            );

        } catch (Exception e) {
            logger.severe("Error loading convoy edit: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR, "Errore durante il caricamento della modifica del convoglio.");
            alert.showAndWait();
            convoyChangeReason = "Errore durante la modifica del convoglio.";
            updateConvoyReasonLabel();
        }
    }

    @FXML
    private void operatorChange() {
        try {
            List<Staff> operatorsAvailable = runDetailsService.checkAvailabilityOfOperator();
            if (operatorsAvailable.isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Nessun operatore disponibile per il cambio.");
                alert.showAndWait();
                operatorChange.setDisable(true);
                operatorChangeEditButtonReason.setText("Non è possibile cambiare operatore: nessun operatore disponibile.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/businessLogic/fxml/StaffSelectionPopup.fxml"));
            Parent root = loader.load();
            StaffSelectionPopupController popupController = loader.getController();
            ObservableList<Staff> operatorsObservable = FXCollections.observableArrayList(operatorsAvailable);
            popupController.setStaffList(operatorsObservable);
            popupController.setConfirmCallback(selectedStaff -> {
                if (selectedStaff != null) {
                    try {
                        runDetailsService.changeOperator(selectedStaff);
                        staffName.setText(selectedStaff.getName());
                        staffSurname.setText(selectedStaff.getSurname());
                        staffEmail.setText(selectedStaff.getEmail());
                        staffNameSurname.setText(selectedStaff.getName() + " " + selectedStaff.getSurname());
                        idStaff = selectedStaff.getIdStaff();
                    } catch (Exception e) {
                        logger.severe("Error changing operator: " + e.getMessage());
                        Alert alert = new Alert(AlertType.ERROR, "Errore durante il cambio dell'operatore.");
                        alert.showAndWait();
                        operatorChangeEditButtonReason.setText("Errore durante la modifica dell'operatore.");
                    }
                }
                ((Stage) root.getScene().getWindow()).close();
                return null;
            });
            Stage stage = new Stage();
            stage.setTitle("Seleziona operatore disponibile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            logger.severe("Error loading staff edit: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR, "Errore durante il caricamento della modifica dell'operatore.");
            alert.showAndWait();
            operatorChangeEditButtonReason.setText("Errore durante la modifica dell'operatore.");
        }
    }

    @FXML
    private void listOfIdCarriages() {
        try {
            Convoy convoy = runDetailsService.selectConvoy(idConvoy);
            if (convoy == null || convoy.getCarriages().isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Nessuna carrozza associata a questo convoglio.");
                alert.showAndWait();
                return;
            }
            VBox carriageListBox = new VBox(10);
            carriageListBox.setStyle("-fx-padding: 10;");
            for (Carriage carriage : convoy.getCarriages()) {
                HBox row = new HBox(10);
                row.getChildren().addAll(
                        new Label("ID: " + carriage.getId()),
                        new Label("Modello: " + carriage.getModel()),
                        new Label("Tipo: " + carriage.getModelType()),
                        new Label("Capcità: " + carriage.getCapacity())
                );
                carriageListBox.getChildren().add(row);
            }
            businessLogic.controller.PopupManager.openPopup(
                    "Lista carrozze",
                    "Carrozze associate al convoglio: " + convoy.getId(),
                    carriageListBox,
                    null,
                    null
            );
        } catch (Exception e) {
            logger.severe("Error loading carriages for convoy: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR, "Errore durante il caricamento di carrozze di un convoglio.");
            alert.showAndWait();
        }
    }

    private void updateConvoyReasonLabel() {
        StringBuilder sb = new StringBuilder();
        if (!convoyEditReason.isEmpty()) {
            sb.append(convoyEditReason);
        }
        if (!convoyChangeReason.isEmpty()) {
            if (!sb.isEmpty()) sb.append("\n");
            sb.append(convoyChangeReason);
        }
        convoyChangeEditButtonReason.setText(sb.toString());
    }
}
