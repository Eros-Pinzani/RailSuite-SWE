package businessLogic.controller;

/**
 * Controller for the Operator Home screen.
 * Handles the display of assigned convoys and navigation for the operator user.
 */
import businessLogic.service.OperatorHomeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import domain.Staff;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;

public class OperatorHomeController {
    @FXML private Label operatorNameLabel;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private TableView<OperatorHomeService.AssignedConvoyInfo> assignedTrainsTable;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> convoyIdColumn;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> departureStationColumn;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> departureTimeColumn;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> arrivalStationColumn;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> arrivalTimeColumn;
    @FXML private TableColumn<OperatorHomeService.AssignedConvoyInfo, Void> detailsColumn;
    @FXML private Label noConvoyLabel;

    private static final Logger logger = Logger.getLogger(OperatorHomeController.class.getName());

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up UI bindings, event handlers, and loads assigned convoys for the operator.
     */
    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            operatorNameLabel.setText(fullName);
            setupTableColumns();
            populateAssignedTrainsTable(staff.getIdStaff());
        }
        logoutMenuItem.setOnAction(_ -> handleLogout());
        exitMenuItem.setOnAction(_ -> handleExit());
    }

    /**
     * Configures the columns of the assigned trains table, including the details button.
     */
    private void setupTableColumns() {
        convoyIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().convoyId)));
        departureStationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().departureStation));
        departureTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().timeDeparture != null ? data.getValue().timeDeparture.toString() : ""));
        arrivalStationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().arrivalStation));
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().arrivalTime));
        detailsColumn.setCellFactory(_ -> new javafx.scene.control.TableCell<>() {
            private final Button btn = new Button("Dettagli");
            {
                btn.setOnAction(_ -> {
                    OperatorHomeService.AssignedConvoyInfo data = getTableView().getItems().get(getIndex());
                    if (data != null) {
                        openConvoyDetailsScene(data);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                OperatorHomeService.AssignedConvoyInfo data = null;
                if (!empty && getIndex() < getTableView().getItems().size()) {
                    data = getTableView().getItems().get(getIndex());
                }
                if (empty || data == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
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
        Platform.exit();
    }

    /**
     * Populates the assigned trains table with convoys assigned to the operator.
     * Shows or hides the table and no-convoy label based on results.
     * @param staffId The ID of the operator staff.
     */
    private void populateAssignedTrainsTable(int staffId) {
        OperatorHomeService service = new OperatorHomeService(new businessLogic.RailSuiteFacade());
        try {
            List<OperatorHomeService.AssignedConvoyInfo> convoys = service.getAssignedConvoysForOperator(staffId);
            boolean hasConvoys = !convoys.isEmpty();
            assignedTrainsTable.setVisible(hasConvoys);
            noConvoyLabel.setVisible(!hasConvoys);
            assignedTrainsTable.setItems(FXCollections.observableArrayList(convoys));
        } catch (Exception e) {
            logger.warning("Errore nel recupero dei convogli assegnati: " + e.getMessage());
            assignedTrainsTable.setVisible(false);
            noConvoyLabel.setVisible(true);
        }
    }

    /**
     * Opens the Convoy Details screen for the selected assigned convoy.
     * @param info The AssignedConvoyInfo object containing convoy details.
     */
    private void openConvoyDetailsScene(OperatorHomeService.AssignedConvoyInfo info) {
        businessLogic.controller.ConvoyDetailsController.setStaticConvoyInfo(info);
        SceneManager.getInstance().switchScene("/businessLogic/fxml/ConvoyDetails.fxml");
    }

    private void openRunDetailsScene(OperatorHomeService.AssignedConvoyInfo info) {
        // Apri la schermata dettagli corsa con i parametri corretti
        SceneManager.getInstance().openRunDetailsScene(
            info.idLine,
            info.convoyId,
            info.idStaff,
            info.timeDeparture,
            info.idFirstStation
        );
    }
}
