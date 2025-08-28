package businessLogic.controller;

/**
 * Controller for the Supervisor Home screen.
 * Handles supervisor navigation and dashboard actions.
 */
import businessLogic.service.NotificationService;
import dao.NotificationDao;
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
import java.sql.Timestamp;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class SupervisorHomeController {
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

    public static class NotificationRow {
        private final SimpleStringProperty typeOfCarriage;
        private final SimpleStringProperty dateTimeOfNotification;
        private final SimpleStringProperty staffSurname;
        private final SimpleStringProperty typeOfNotification;

        public NotificationRow(String typeOfCarriage, String dateTimeOfNotification, String staffSurname, String typeOfNotification) {
            this.typeOfCarriage = new SimpleStringProperty(typeOfCarriage);
            this.dateTimeOfNotification = new SimpleStringProperty(dateTimeOfNotification);
            this.staffSurname = new SimpleStringProperty(staffSurname);
            this.typeOfNotification = new SimpleStringProperty(typeOfNotification);
        }
        public String getTypeOfCarriage() { return typeOfCarriage.get(); }
        public String getDateTimeOfNotification() { return dateTimeOfNotification.get(); }
        public String getStaffSurname() { return staffSurname.get(); }
        public String getTypeOfNotification() { return typeOfNotification.get(); }
    }

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

        manageColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Gestisci");
            {
                btn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(e -> {/* logica futura per gestire la notifica */});
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        ObservableList<NotificationRow> data = FXCollections.observableArrayList();
        NotificationService notificationService = new NotificationService();
        List<Notification> notifications = notificationService.getAllNotifications();
        for (Notification n : notifications) {
            data.add(new NotificationRow(
                n.getTypeOfCarriage(),
                n.getDateTimeOfNotification().toString(),
                n.getStaffSurname(),
                n.getTypeOfNotification()
            ));
        }
        notificationTable.setItems(data);
        notificationTable.getItems().addListener((javafx.collections.ListChangeListener<NotificationRow>) c -> adjustTableHeight());
        adjustTableHeight();

        gestioneCorseButton.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml"));
        gestioneConvogliButton.setOnAction(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageConvoy.fxml"));
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
}
