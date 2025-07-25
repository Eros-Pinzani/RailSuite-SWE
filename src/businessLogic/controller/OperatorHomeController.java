package businessLogic.controller;

import businessLogic.service.OperatorHomeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import domain.Staff;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeItem;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.Button;


public class OperatorHomeController {
    @FXML
    private Label operatorNameLabel;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private TreeTableView<AssignedConvoyInfo> assignedTrainsTable;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, String> convoyIdColumn;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, String> departureStationColumn;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, String> departureTimeColumn;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, String> arrivalStationColumn;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, String> arrivalTimeColumn;
    @FXML
    private TreeTableColumn<AssignedConvoyInfo, Void> detailsColumn;
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
            List<AssignedConvoyInfo> convoys = service.getAssignedConvoysForOperator(staffId);
            boolean hasConvoys = !convoys.isEmpty();
            assignedTrainsTable.setVisible(hasConvoys);
            noConvoyLabel.setVisible(!hasConvoys);
            if (!hasConvoys) {
                assignedTrainsTable.setRoot(null);
                return;
            }
            TreeItem<AssignedConvoyInfo> root = new TreeItem<>(new AssignedConvoyInfo(0, "", "", "", ""));
            for (AssignedConvoyInfo info : convoys) {
                root.getChildren().add(new TreeItem<>(info));
            }
            assignedTrainsTable.setRoot(root);
            assignedTrainsTable.setShowRoot(false);
            convoyIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getValue().convoyId)));
            departureStationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().departureStation));
            departureTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().departureTime));
            arrivalStationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().arrivalStation));
            arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().arrivalTime));
            detailsColumn.setCellFactory(_ -> new TreeTableCell<>() {
                private final Button btn = new Button("Dettagli");
                {
                    btn.setOnAction(_ -> {
                        AssignedConvoyInfo data = getTableRow() != null ? getTableRow().getItem() : null;
                        if (data != null) {
                            openConvoyDetailsScene(data);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    AssignedConvoyInfo data = getTableRow() != null ? getTableRow().getItem() : null;
                    if (empty || data == null || data.convoyId == 0) {
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

    private void openConvoyDetailsScene(AssignedConvoyInfo info) {
        ConvoyDetailsController.setStaticConvoyInfo(info);
        SceneManager.getInstance().switchScene("/businessLogic/fxml/ConvoyDetails.fxml");
    }
}
