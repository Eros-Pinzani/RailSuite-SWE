package businessLogic.controller;

import businessLogic.service.ConvoyDetailsService;
import businessLogic.service.ConvoyDetailsService.StationRow;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import businessLogic.service.NotificationService;
import java.sql.Timestamp;

import domain.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


/**
 * Controller for the Convoy Details screen.
 * Handles the display of convoy information, carriages, stations,
 * and user actions related to convoy details.
 */
public class ConvoyDetailsController {
    @FXML private TableView<domain.Carriage> carriageTable;
    @FXML private TableColumn<domain.Carriage, Integer> carriageIdColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageModelColumn;
    @FXML private TableColumn<domain.Carriage, String> carriageTypeColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageYearColumn;
    @FXML private TableColumn<domain.Carriage, Integer> carriageCapacityColumn;
    @FXML private TableColumn<domain.Carriage, Void> notifyColumn;
    @FXML private Label convoyIdLabel;
    @FXML private Label lineNameLabel;
    @FXML private Label departureStationLabel;
    @FXML private Label departureTimeLabel;
    @FXML private Label arrivalStationLabel;
    @FXML private Label arrivalTimeLabel;
    @FXML private Label staffNameLabel;
    @FXML private TableView<StationRow> stationTable;
    @FXML private TableColumn<StationRow, String> stationNameColumn;
    @FXML private TableColumn<StationRow, String> arrivalTimeColumn;
    @FXML private TableColumn<StationRow, String> departureTimeColumn;
    @FXML private Label operatorNameLabel;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private Button toggleCarriageTableButton;
    @FXML private Button toggleStationTableButton;

    private final ConvoyDetailsService convoyDetailsService = new ConvoyDetailsService();
    private AssignedConvoyInfo convoyInfo;
    private static AssignedConvoyInfo staticConvoyInfo;
    private final NotificationService notificationService;

    // Mappa: idCarrozza -> Set dei tipi di segnalazione già fatte (per TUTTI gli operatori)
    private final java.util.Map<Integer, java.util.Set<String>> notifiedTypesByCarriage = new java.util.HashMap<>();
    // Mappa: idCarrozza -> Set dei tipi di segnalazione gi�� fatte (SOLO per l'operatore corrente)
    private final java.util.Map<Integer, java.util.Set<String>> notifiedTypesByCarriagePerStaff = new java.util.HashMap<>();

    /**
     * Sets the static convoy info to be used when initializing the details view.
     * @param info The AssignedConvoyInfo object containing convoy details.
     */
    public static void setStaticConvoyInfo(AssignedConvoyInfo info) {
        staticConvoyInfo = info;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads convoy details if available.
     */
    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null && operatorNameLabel != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            operatorNameLabel.setText(fullName);
        }
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(_ -> handleLogout());
        }
        if (exitMenuItem != null) {
            exitMenuItem.setOnAction(_ -> handleExit());
        }

        carriageIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        carriageModelColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        carriageTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModelType()));
        carriageYearColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getYearProduced()).asObject());
        carriageCapacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()).asObject());

        if (stationNameColumn != null) {
            stationNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().stationName));
        }
        if (arrivalTimeColumn != null) {
            arrivalTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().arrivalTime));
        }
        if (departureTimeColumn != null) {
            departureTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().departureTime));
        }

        if (notifyColumn != null) {
            notifyColumn.setCellFactory(col -> new TableCell<domain.Carriage, Void>() {
                private final Button btnTechnicalIssue = new Button("Guasto tecnico");
                private final Button btnCleaning = new Button("Pulizia");
                private final HBox box = new HBox(10, btnTechnicalIssue, btnCleaning);
                {
                    btnTechnicalIssue.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");
                    btnCleaning.setStyle("-fx-background-color: #64b5f6; -fx-text-fill: white;");
                    btnTechnicalIssue.setFocusTraversable(false);
                    btnCleaning.setFocusTraversable(false);
                    btnTechnicalIssue.setOnAction(e -> {
                        domain.Carriage carriage = getTableView().getItems().get(getIndex());
                        handleTechnicalIssue(carriage);
                        getTableView().refresh();
                    });
                    btnCleaning.setOnAction(e -> {
                        domain.Carriage carriage = getTableView().getItems().get(getIndex());
                        handleCleaning(carriage);
                        getTableView().refresh();
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        domain.Carriage carriage = getTableView().getItems().get(getIndex());
                        boolean technicalNotified = false;
                        boolean cleaningNotified = false;
                        var types = notifiedTypesByCarriagePerStaff.get(carriage.getId());
                        if (types != null) {
                            technicalNotified = types.contains("MAINTENANCE");
                            cleaningNotified = types.contains("CLEANING");
                        }
                        boolean inRun = false;
                        if (convoyInfo != null && convoyInfo.timeDeparture != null && convoyInfo.arrivalTime != null) {
                            try {
                                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                                java.time.LocalDateTime dep = convoyInfo.timeDeparture.toLocalDateTime();
                                java.time.LocalDateTime arr;
                                try {
                                    arr = java.time.LocalDateTime.parse(convoyInfo.arrivalTime.replace(' ', 'T'));
                                } catch (Exception e) {
                                    arr = dep.plusHours(1);
                                }
                                inRun = (now.isEqual(dep) || now.isAfter(dep)) && (now.isBefore(arr) || now.isEqual(arr));
                            } catch (Exception e) {
                                inRun = false;
                            }
                        }
                        btnTechnicalIssue.setDisable(!inRun || technicalNotified);
                        btnCleaning.setDisable(!inRun || cleaningNotified);
                        if (!inRun || technicalNotified) {
                            btnTechnicalIssue.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #888888;");
                        } else {
                            btnTechnicalIssue.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");
                        }
                        if (!inRun || cleaningNotified) {
                            btnCleaning.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #888888;");
                        } else {
                            btnCleaning.setStyle("-fx-background-color: #64b5f6; -fx-text-fill: white;");
                        }
                        setGraphic(box);
                    }
                }
            });
        }

        if (toggleCarriageTableButton != null && carriageTable != null) {
            toggleCarriageTableButton.setOnAction(e -> {
                boolean isVisible = carriageTable.isVisible();
                carriageTable.setVisible(!isVisible);
                carriageTable.setManaged(!isVisible);
                toggleCarriageTableButton.setText(isVisible ? "Mostra tabella convoglio" : "Nascondi tabella convoglio");
            });
        }
        if (toggleStationTableButton != null && stationTable != null) {
            toggleStationTableButton.setOnAction(e -> {
                boolean isVisible = stationTable.isVisible();
                stationTable.setVisible(!isVisible);
                stationTable.setManaged(!isVisible);
                toggleStationTableButton.setText(isVisible ? "Mostra dettagli corsa" : "Nascondi dettagli corsa");
            });
        }

        if (staticConvoyInfo != null) {
            setConvoyInfo(staticConvoyInfo);
            staticConvoyInfo = null;
        }

        if (operatorNameLabel != null) {
            operatorNameLabel.setCursor(Cursor.HAND);
            operatorNameLabel.setOnMouseClicked(event -> {
                SceneManager.getInstance().switchScene("/businessLogic/fxml/OperatorHome.fxml");
            });
        }
    }

    /**
     * Sets the convoy information to be displayed and populates the details.
     * @param info The AssignedConvoyInfo object containing convoy details.
     */
    public void setConvoyInfo(AssignedConvoyInfo info) {
        this.convoyInfo = info;
        populateDetails();
    }

    /**
     * Populates the UI fields with the details of the selected convoy.
     * Fetches data from the service and updates the labels and tables.
     */
    private void populateDetails() {
        ConvoyDetailsService.ConvoyDetailsDTO dto = convoyDetailsService.getConvoyDetailsDTO(convoyInfo);
        if (dto == null) {
            if (convoyIdLabel != null) convoyIdLabel.setText("Errore");
            return;
        }
        if (convoyIdLabel != null) convoyIdLabel.setText(dto.convoyId);
        if (lineNameLabel != null) lineNameLabel.setText(dto.lineName);
        if (staffNameLabel != null) staffNameLabel.setText(dto.staffName);
        if (departureStationLabel != null) departureStationLabel.setText(dto.departureStation);
        if (departureTimeLabel != null) departureTimeLabel.setText(dto.departureTime);
        if (arrivalStationLabel != null) arrivalStationLabel.setText(dto.arrivalStation);
        if (arrivalTimeLabel != null) arrivalTimeLabel.setText(dto.arrivalTime);
        if (carriageTable != null) carriageTable.setItems(FXCollections.observableArrayList(dto.carriages));
        if (stationTable != null) stationTable.setItems(FXCollections.observableArrayList(dto.stationRows));

        // Recupera le notifiche dal NotificationService e aggiorna la mappa notifiedTypesByCarriage
        try {
            var notifications = notificationService.getNotificationsByConvoyId(Integer.parseInt(dto.convoyId));
            notifiedTypesByCarriage.clear();
            if (notifications != null) {
                for (var n : notifications) {
                    String type = n.getTypeOfNotification();
                    if (type == null) continue;
                    switch (type) {
                        case "MAINTENANCE":
                        case "CLEANING":
                            notifiedTypesByCarriage.computeIfAbsent(n.getIdCarriage(), k -> new java.util.HashSet<>()).add(type);
                            break;
                        default:
                            break;
                    }
                }
            }
            // Popola la mappa delle notifiche per l'operatore corrente
            Staff staff = UserSession.getInstance().getStaff();
            notifiedTypesByCarriagePerStaff.clear();
            if (staff != null) {
                var staffNotifications = notificationService.getAllNotificationsForConvoyAndStaff(Integer.parseInt(dto.convoyId), staff.getIdStaff());
                if (staffNotifications != null) {
                    for (var n : staffNotifications) {
                        String type = n.getTypeOfNotification();
                        if (type == null) continue;
                        switch (type) {
                            case "MAINTENANCE":
                            case "CLEANING":
                                notifiedTypesByCarriagePerStaff.computeIfAbsent(n.getIdCarriage(), k -> new java.util.HashSet<>()).add(type);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (carriageTable != null) carriageTable.refresh();
        } catch (Exception e) {
            // In caso di errore, ignora e lascia la mappa vuota
        }
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

    /**
     * Gestisce la segnalazione di un guasto tecnico per una carrozza.
     * @param carriage la carrozza selezionata
     */
    private void handleTechnicalIssue(domain.Carriage carriage) {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff == null || convoyInfo == null || carriage == null) return;
        notificationService.addNotification(
            carriage.getId(),
            carriage.getModelType(),
            convoyInfo.convoyId,
            "MAINTENANCE",
            new Timestamp(System.currentTimeMillis()),
            staff.getIdStaff(),
            staff.getName(),
            staff.getSurname(),
            "INVIATA" // status
        );
        // Aggiorna subito la mappa per disabilitare il pulsante
        notifiedTypesByCarriagePerStaff.computeIfAbsent(carriage.getId(), k -> new java.util.HashSet<>()).add("MAINTENANCE");
        if (carriageTable != null) carriageTable.refresh();
    }

    /**
     * Gestisce la segnalazione di necessità di pulizie per una carrozza.
     * @param carriage la carrozza selezionata
     */
    private void handleCleaning(domain.Carriage carriage) {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff == null || convoyInfo == null || carriage == null) return;
        notificationService.addNotification(
            carriage.getId(),
            carriage.getModelType(),
            convoyInfo.convoyId,
            "CLEANING",
            new Timestamp(System.currentTimeMillis()),
            staff.getIdStaff(),
            staff.getName(),
            staff.getSurname(),
            "INVIATA" // status
        );
        // Aggiorna subito la mappa per disabilitare il pulsante
        notifiedTypesByCarriagePerStaff.computeIfAbsent(carriage.getId(), k -> new java.util.HashSet<>()).add("CLEANING");
        if (carriageTable != null) carriageTable.refresh();
    }

    public ConvoyDetailsController() {
        this.notificationService = convoyDetailsService.getNotificationService();
    }
}
