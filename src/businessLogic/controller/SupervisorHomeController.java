package businessLogic.controller;

/**
 * Controller for the Supervisor Home screen.
 * Handles supervisor navigation and dashboard actions.
 */
import domain.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private TableColumn<NotificationRow, String> carriageIdColumn;
    @FXML
    private TableColumn<NotificationRow, String> convoyIdColumn;
    @FXML
    private TableColumn<NotificationRow, String> stationColumn;
    @FXML
    private TableColumn<NotificationRow, String> typeColumn;
    @FXML
    private TableColumn<NotificationRow, Void> approveColumn;
    @FXML
    private TableColumn<NotificationRow, Void> denyColumn;
    @FXML
    private Button gestioneCorseButton;
    @FXML
    private Button gestioneConvogliButton;

    public class NotificationRow {
        private final String carriageId;
        private final String convoyId;
        private final String station;
        private final String type;

        public NotificationRow(String carriageId, String convoyId, String station, String type) {
            this.carriageId = carriageId;
            this.convoyId = convoyId;
            this.station = station;
            this.type = type;
        }

        public String getCarriageId() {
            return carriageId;
        }

        public String getConvoyId() {
            return convoyId;
        }

        public String getStation() {
            return station;
        }

        public String getType() {
            return type;
        }
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

        carriageIdColumn.setReorderable(false);
        convoyIdColumn.setReorderable(false);
        stationColumn.setReorderable(false);
        typeColumn.setReorderable(false);
        approveColumn.setReorderable(false);
        denyColumn.setReorderable(false);

        carriageIdColumn.setCellValueFactory(new PropertyValueFactory<>("carriageId"));
        convoyIdColumn.setCellValueFactory(new PropertyValueFactory<>("convoyId"));
        stationColumn.setCellValueFactory(new PropertyValueFactory<>("station"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        approveColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Approva");

            {
                btn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(e -> {/* logica futura */});
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        denyColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Nega");

            {
                btn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(e -> {/* logica futura */});
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        ObservableList<NotificationRow> data = FXCollections.observableArrayList(
                new NotificationRow("1", "A12", "Roma Termini", "Manutenzione"),
                new NotificationRow("2", "B34", "Milano Centrale", "Pulizia")
        );
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
