package businessLogic.controller;

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
    @FXML
    private Label operatorNameLabel;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private TableView<OperatorHomeService.AssignedConvoyInfo> assignedTrainsTable;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> convoyIdColumn;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> departureStationColumn;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> departureTimeColumn;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> arrivalStationColumn;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, String> arrivalTimeColumn;
    @FXML
    private TableColumn<OperatorHomeService.AssignedConvoyInfo, Void> detailsColumn;
    @FXML
    private Label noConvoyLabel;

    private static final Logger logger = Logger.getLogger(OperatorHomeController.class.getName());

    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            operatorNameLabel.setText(fullName);
            populateAssignedTrainsTable(staff.getIdStaff());
        }
        logoutMenuItem.setOnAction(_ -> handleLogout());
        exitMenuItem.setOnAction(_ -> handleExit());
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        SceneManager.getInstance().switchScene("/businessLogic/fxml/LogIn.fxml");
    }

    private void handleExit() {
        Platform.exit();
    }

    private void populateAssignedTrainsTable(int staffId) {
        OperatorHomeService service = new OperatorHomeService();
        try {
            List<OperatorHomeService.AssignedConvoyInfo> convoys = service.getAssignedConvoysForOperator(staffId);
            boolean hasConvoys = !convoys.isEmpty();
            assignedTrainsTable.setVisible(hasConvoys);
            noConvoyLabel.setVisible(!hasConvoys);
            if (!hasConvoys) {
                assignedTrainsTable.setItems(FXCollections.observableArrayList());
                return;
            }
            assignedTrainsTable.setItems(FXCollections.observableArrayList(convoys));
            convoyIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().convoyId)));
            departureStationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().departureStation));
            departureTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().departureTime));
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
                    businessLogic.service.OperatorHomeService.AssignedConvoyInfo data = null;
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
        } catch (Exception e) {
            logger.severe("Error while populating the assigned convoys table: " + e.getMessage());
            assignedTrainsTable.setVisible(false);
            noConvoyLabel.setVisible(true);
        }
    }

    private void openConvoyDetailsScene(OperatorHomeService.AssignedConvoyInfo info) {
        ConvoyDetailsController.setStaticConvoyInfo(info);
        SceneManager.getInstance().switchScene("/businessLogic/fxml/ConvoyDetails.fxml");
    }
}
