package businessLogic.controller;

import businessLogic.service.NotificationObserver;
import businessLogic.service.SupervisorHomeService;
import businessLogic.service.SupervisorHomeService.NotificationRow;
import domain.Notification;
import domain.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Supervisor Home screen.
 * Handles supervisor navigation and dashboard actions.
 */

public class SupervisorHomeController implements NotificationObserver {
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private Label supervisorNameLabel;

    @FXML
    private TableView<NotificationRow> notificationTable;
    @FXML
    private TableColumn<NotificationRow, String> carriageTypeColumn;
    @FXML
    private TableColumn<NotificationRow, String> dateTimeColumn;
    @FXML
    private TableColumn<NotificationRow, String> staffSurnameColumn;
    @FXML
    private TableColumn<NotificationRow, String> typeColumn;
    @FXML
    private TableColumn<NotificationRow, Void> manageColumn;
    @FXML
    private Button gestioneCorseButton;
    @FXML
    private Button gestioneConvogliButton;

    private final SupervisorHomeService supervisorHomeService = new SupervisorHomeService();
    private final ObservableList<NotificationRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and populates the notification table.
     */
    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            supervisorNameLabel.setText(fullName);
        }
        logoutMenuItem.setOnAction(_ -> handleLogout());
        exitMenuItem.setOnAction(_ -> handleExit());

        carriageTypeColumn.setReorderable(false);
        dateTimeColumn.setReorderable(false);
        staffSurnameColumn.setReorderable(false);
        typeColumn.setReorderable(false);
        manageColumn.setReorderable(false);

        carriageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfCarriage"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeOfNotification"));
        staffSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("staffSurname"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfNotification"));

        manageColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button btn = new Button("Gestisci");
            {
                btn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(_ -> {
                    NotificationRow row = getTableView().getItems().get(getIndex());
                    openManageNotificationPopup(row.getNotification());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        supervisorHomeService.addNotificationObserver(this);
        refreshNotificationsTable();
        notificationTable.getItems().addListener((javafx.collections.ListChangeListener<NotificationRow>) _ -> adjustTableHeight());
        adjustTableHeight();

        gestioneCorseButton.setOnAction(_ -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml"));
        gestioneConvogliButton.setOnAction(_ -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageConvoy.fxml"));
    }

    /**
     * Adjusts the height of the notification table based on the number of rows.
     * Improves readability by resizing the table dynamically.
     */
    private void adjustTableHeight() {
        int rowCount = notificationTable.getItems().size();
        double rowHeight = 36; // Altezza aumentata per una migliore leggibilità
        double headerHeight = 36; // Header più alto
        double totalHeight = headerHeight + rowCount * rowHeight;
        notificationTable.setPrefHeight(totalHeight);
    }

    /**
     * Handles the logout action, clearing the user session and returning to the login screen.
     */
    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }

    /**
     * Handles the exit action, closing the application.
     */
    private void handleExit() {
        javafx.application.Platform.exit();
    }

    @Override
    public void onNotificationAdded(domain.Notification notification) {
        if (supervisorHomeService.isNotificationSend(notification)) {
            data.add(supervisorHomeService.toNotificationRow(notification));
            adjustTableHeight();
        }
    }

    /**
     * Ricarica la tabella delle notifiche dal database.
     */
    private void refreshNotificationsTable() {
        data.clear();
        data.addAll(supervisorHomeService.getAllNotificationRows());
        notificationTable.setItems(data);
        adjustTableHeight();
    }

    private void openManageNotificationPopup(Notification notification) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/businessLogic/fxml/ManageNotificationPopup.fxml"));
            javafx.scene.Parent root = loader.load();
            ManageNotificationPopupController controller = loader.getController();
            controller.setNotification(notification);
            controller.loadRunsForCarriage(notification.getIdCarriage());
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestione Notifica Carrozza");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Dopo la chiusura del popup, aggiorna la tabella
            refreshNotificationsTable();
        } catch (Exception e) {
            Logger.getLogger(SupervisorHomeController.class.getName()).log(Level.SEVERE, "Errore nell'apertura del popup di gestione notifica", e);
        }
    }
}
