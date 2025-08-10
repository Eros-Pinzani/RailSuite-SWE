package businessLogic.controller;

import domain.DTO.RunDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import businessLogic.controller.UserSession;
import domain.Staff;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import businessLogic.service.RunDetailsService;
import domain.Run;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

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

    // Toggle Section Buttons
    @FXML private Button convoyDetailSectionToggle;
    @FXML private Button operatorDetailSectionToggle;
    @FXML private Button timeTableDetailSectionToggle;

    // Altri
    @FXML private Button listOfIdCarriages;

    private final RunDetailsService runDetailsService = new RunDetailsService();


    public static void header(Label supervisorNameLabel, MenuItem logoutMenuItem, MenuItem exitMenuItem) {
        CreateRunController.header(supervisorNameLabel, logoutMenuItem, exitMenuItem);
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
        RunDTO run = runDetailsService(); //TODO trasporta i dati: idLine, idConvoy, idStaff
        if (run != null) {
            firstStation.setText(run.getFirstStationName());
            startAtDateTime.setText(run.getTimeDeparture().toLocalDateTime().toString());
            staffNameSurname.setText(run.getStaffName() + " " + run.getStaffSurname());
            staffName.setText(run.getStaffName());
            staffSurname.setText(run.getStaffSurname());
            staffEmail.setText(run.getStaffEmail());
            lineName.setText(run.getLineName());

        }

        // Carica i dati della corsa (Run) tramite il service
        run = runDetailsService.getSelectedRun(); // Supponiamo che il service abbia questo metodo
        if (run != null) {
            firstStation.setText(run.getStartStation().getLocation());
            startAtDateTime.setText(run.getDepartureTime().toString());
            staffNameSurname.setText(run.getOperator().getName() + " " + run.getOperator().getSurname());
            typeOfConvoy.setText(run.getConvoy().getType());
            modelOfConvoy.setText(run.getConvoy().getModel());
            capacityOfConvoy.setText(String.valueOf(run.getConvoy().getCapacity()));
            numberOfCarriage.setText(String.valueOf(run.getConvoy().getNumberOfCarriage()));
            staffName.setText(run.getOperator().getName());
            staffSurname.setText(run.getOperator().getSurname());
            staffEmail.setText(run.getOperator().getEmail());
            runDate.setText(run.getDepartureTime().toLocalDateTime().toString());
            lineName.setText(run.getLine().getName());

            // Abilita/disabilita i pulsanti di modifica in base all'orario di partenza
            boolean isEditable = LocalDateTime.now().isBefore(run.getDepartureTime().toLocalDateTime());
            eliminationConfirm.setDisable(!isEditable);
            convoyEdit.setDisable(!isEditable);
            operatorChange.setDisable(!isEditable);
            runTimeEdit.setDisable(!isEditable);

        } else {
            // Gestione errore: nessuna corsa selezionata
            Alert alert = new Alert(AlertType.ERROR, "Nessuna corsa selezionata.");
            alert.showAndWait();
            eliminationConfirm.setDisable(true);
            convoyEdit.setDisable(true);
            operatorChange.setDisable(true);
            runTimeEdit.setDisable(true);
        }
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
