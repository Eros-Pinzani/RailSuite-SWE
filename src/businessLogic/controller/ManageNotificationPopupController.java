package businessLogic.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import domain.Run;
import dao.ConvoyDao;
import dao.RunDao;
import domain.Convoy;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;
import domain.Notification;
import dao.NotificationDao;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageNotificationPopupController {
    @FXML private TableView<Run> runTable;
    @FXML private TableColumn<Run, Integer> convoyIdColumn;
    @FXML private TableColumn<Run, String> dateColumn;
    @FXML private TableColumn<Run, String> staffSurnameColumn;
    @FXML private Label noRunsLabel;

    private Notification notification;

    public void initialize() {
        convoyIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getIdConvoy()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimeDeparture().toString()));
        staffSurnameColumn.setCellValueFactory(cellData -> {
            String nameSurname = cellData.getValue().getStaffNameSurname();
            String surname = nameSurname.contains(" ") ? nameSurname.substring(nameSurname.lastIndexOf(' ') + 1) : nameSurname;
            return new javafx.beans.property.SimpleStringProperty(surname);
        });
    }

    public void loadRunsForCarriage(int idCarriage) {
        try {
            List<Convoy> allConvoys = ConvoyDao.of().selectAllConvoys();
            List<Integer> convoyIds = allConvoys.stream()
                .filter(convoy -> convoy.getCarriages().stream().anyMatch(c -> c.getId() == idCarriage))
                .map(Convoy::getId)
                .toList();
            if (convoyIds.isEmpty()) {
                runTable.setItems(FXCollections.observableArrayList());
                runTable.setVisible(false);
                noRunsLabel.setVisible(true);
                return;
            }
            List<Run> filteredRuns = new java.util.ArrayList<>();
            for (Integer convoyId : convoyIds) {
                filteredRuns.addAll(RunDao.of().selectRunsByConvoy(convoyId));
            }
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            List<Run> futureRuns = filteredRuns.stream()
                .filter(run -> run.getTimeDeparture().toLocalDateTime().isAfter(now) || run.getTimeDeparture().toLocalDateTime().isEqual(now))
                .collect(Collectors.toList());
            if (futureRuns.isEmpty()) {
                runTable.setItems(FXCollections.observableArrayList());
                runTable.setVisible(false);
                noRunsLabel.setVisible(true);
            } else {
                runTable.setItems(FXCollections.observableArrayList(futureRuns));
                runTable.setVisible(true);
                noRunsLabel.setVisible(false);
            }
        } catch (SQLException e) {
            Logger.getLogger(ManageNotificationPopupController.class.getName()).log(Level.SEVERE, "Errore nel caricamento delle corse per la carrozza", e);
            runTable.setItems(FXCollections.observableArrayList());
            runTable.setVisible(false);
            noRunsLabel.setVisible(true);
        }
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @FXML
    private void handleApprove() {
        try {
            // Recupera la Run associata alla notifica tramite idConvoy, idStaff e data compatibile
            Run run = null;
            try {
                List<Run> runs = dao.RunDao.of().selectRunsByConvoy(notification.getIdConvoy());
                for (Run r : runs) {
                    boolean staffMatch = r.getIdStaff() == notification.getIdStaff();
                    if (staffMatch) {
                        run = r;
                        break;
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ManageNotificationPopupController.class.getName()).log(Level.SEVERE, "Errore nel recupero delle corse per il convoglio", e);
            }
            if (run != null) {
                // Controllo se la carrozza è impegnata in una corsa in corso
                List<Run> allRuns = dao.RunDao.of().selectRunsByConvoy(notification.getIdConvoy());
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                boolean inCorso = false;
                for (Run r : allRuns) {
                    if (r.getTimeDeparture() != null && r.getTimeArrival() != null &&
                        !now.before(r.getTimeDeparture()) && !now.after(r.getTimeArrival())) {
                        inCorso = true;
                        break;
                    }
                }
                if (!inCorso) {
                    new businessLogic.service.ManageRunService().completeRun(run);
                }
            }
            // Sposta la notifica nello storico con stato APPROVATA
            dao.NotificationDao.of().moveNotificationToHistory(
                notification.getIdCarriage(),
                notification.getIdConvoy(),
                notification.getDateTimeOfNotification(),
                notification.getTypeOfNotification(),
                notification.getIdStaff(),
                notification.getStaffName(),
                notification.getStaffSurname(),
                "APPROVATA"
            );
            // Elimina la notifica dalla tabella principale
            dao.NotificationDao.of().deleteNotification(
                notification.getIdCarriage(),
                notification.getIdConvoy(),
                notification.getDateTimeOfNotification()
            );
        } catch (Exception e) {
            Logger.getLogger(ManageNotificationPopupController.class.getName()).log(Level.SEVERE, "Errore nell'approvazione della notifica", e);
        }
        close();
    }

    @FXML
    private void handleDeny() {
        try {
            // Sposta la notifica nello storico con stato NEGATA
            NotificationDao.of().moveNotificationToHistory(
                notification.getIdCarriage(),
                notification.getIdConvoy(),
                notification.getDateTimeOfNotification(),
                notification.getTypeOfNotification(),
                notification.getIdStaff(),
                notification.getStaffName(),
                notification.getStaffSurname(),
                "NEGATA"
            );
            // Elimina la notifica dalla tabella principale
            NotificationDao.of().deleteNotification(
                notification.getIdCarriage(),
                notification.getIdConvoy(),
                notification.getDateTimeOfNotification()
            );
        } catch (Exception e) {
            Logger.getLogger(ManageNotificationPopupController.class.getName()).log(Level.SEVERE, "Errore nella negazione della notifica", e);
        }
        close();
    }

    private void close() {
        Stage stage = (Stage) runTable.getScene().getWindow();
        stage.close();
    }
}
