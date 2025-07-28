package businessLogic.controller;

import businessLogic.service.ConvoyService;
import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.Convoy;
import domain.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageConvoyController {
    @FXML private ComboBox<String> carriageTypeComboBox;
    @FXML private ComboBox<String> carriageModelComboBox;
    @FXML private Spinner<Integer> carriageCountSpinner;
    @FXML private Button createConvoyButton;
    @FXML private ComboBox<Convoy> deleteConvoyComboBox;
    @FXML private Button deleteConvoyButton;
    @FXML private javafx.scene.control.Label supervisorNameLabel;
    @FXML private javafx.scene.control.MenuItem logoutMenuItem;
    @FXML private javafx.scene.control.MenuItem exitMenuItem;
    @FXML private javafx.scene.control.MenuButton menuButton;

    private final ConvoyService convoyService = new ConvoyService();
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private List<Carriage> allCarriagesFinal = List.of();

    @FXML
    public void initialize() {
        Staff staff = UserSession.getInstance().getStaff();
        if (staff != null) {
            String fullName = staff.getName() + " " + staff.getSurname();
            supervisorNameLabel.setText(fullName);
        }
        supervisorNameLabel.setOnMouseClicked(e -> SceneManager.getInstance().switchScene("/businessLogic/fxml/SupervisorHome.fxml"));
        logoutMenuItem.setOnAction(_ -> UserSession.getInstance().clear());
        exitMenuItem.setOnAction(_ -> javafx.application.Platform.exit());
        updateAvailableCarriagesUI();
        createConvoyButton.setOnAction(e -> handleCreateConvoy());
        try {
            deleteConvoyComboBox.setItems(FXCollections.observableArrayList(facade.selectAllConvoys()));
        } catch (Exception ex) {
            deleteConvoyComboBox.setItems(FXCollections.observableArrayList());
        }
        deleteConvoyComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        deleteConvoyComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Convoy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ("Convoglio " + item.getId()));
            }
        });
        deleteConvoyButton.setOnAction(_ -> handleDeleteConvoy());
    }

    private void updateAvailableCarriagesUI() {
        try {
            allCarriagesFinal = facade.selectAllCarriages().stream()
                .filter(c -> c.getIdConvoy() == null)
                .collect(Collectors.toList());
            List<String> types = allCarriagesFinal.stream()
                .map(Carriage::getModelType)
                .distinct()
                .collect(Collectors.toList());
            carriageTypeComboBox.setItems(FXCollections.observableArrayList(types));
            carriageModelComboBox.setDisable(true);
            if (!types.isEmpty()) {
                carriageTypeComboBox.getSelectionModel().selectFirst();
                String firstType = types.get(0);
                List<String> models = allCarriagesFinal.stream()
                    .filter(c -> c.getModelType().equals(firstType))
                    .map(Carriage::getModel)
                    .distinct()
                    .collect(Collectors.toList());
                String firstModel = models.isEmpty() ? null : models.get(0);
                updateCarriageCountSpinner(firstType, firstModel, allCarriagesFinal);
                carriageModelComboBox.setItems(FXCollections.observableArrayList(models));
                carriageModelComboBox.setDisable(false);
                if (!models.isEmpty()) {
                    carriageModelComboBox.getSelectionModel().selectFirst();
                }
            } else {
                carriageModelComboBox.setItems(FXCollections.observableArrayList());
                updateCarriageCountSpinner(null, null, allCarriagesFinal);
            }
        } catch (Exception e) {
            carriageTypeComboBox.setItems(FXCollections.observableArrayList());
            carriageModelComboBox.setItems(FXCollections.observableArrayList());
            updateCarriageCountSpinner(null, null, List.of());
        }
    }

    private void updateCarriageCountSpinner(String type, String model, List<Carriage> allCarriages) {
        if (type == null || model == null) {
            carriageCountSpinner.setValueFactory(new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            carriageCountSpinner.getValueFactory().setValue(1);
            carriageCountSpinner.setDisable(true);
            return;
        }
        long count = allCarriages.stream().filter(c -> c.getModelType().equals(type) && c.getModel().equals(model)).count();
        int max = (int) Math.max(count, 1);
        carriageCountSpinner.setValueFactory(new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, 1));
        carriageCountSpinner.getValueFactory().setValue(1);
        carriageCountSpinner.setDisable(count == 0);
    }

    private void handleCreateConvoy() {
        String selectedType = carriageTypeComboBox.getValue();
        String selectedModel = carriageModelComboBox.getValue();
        int count = carriageCountSpinner.getValue();
        if (selectedType == null || selectedModel == null || count < 1) return;
        List<Carriage> available = allCarriagesFinal.stream()
            .filter(c -> c.getModelType().equals(selectedType) && c.getModel().equals(selectedModel))
            .limit(count)
            .collect(Collectors.toList());
        if (available.size() < count) return;
        convoyService.createConvoy(available);
        deleteConvoyComboBox.setItems(FXCollections.observableArrayList(convoyService.getAllConvoys()));
        updateAvailableCarriagesUI();
    }

    private void handleDeleteConvoy() {
        Convoy selected = deleteConvoyComboBox.getValue();
        if (selected == null) return;
        try {
            List<Carriage> carriages = facade.selectCarriagesByConvoyId(selected.getId());
            for (Carriage c : carriages) {
                try {
                    facade.updateCarriageConvoy(c.getId(), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            facade.removeConvoy(selected.getId());
            deleteConvoyComboBox.setItems(FXCollections.observableArrayList(facade.selectAllConvoys()));
            updateAvailableCarriagesUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
