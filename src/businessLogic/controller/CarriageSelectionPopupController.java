package businessLogic.controller;

import domain.Carriage;
import domain.Convoy;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import businessLogic.service.CarriageSelectionPopupService;
import javafx.stage.Window;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CarriageSelectionPopupController implements Initializable {
    @FXML
    private TableView<Carriage> carriageTable;
    @FXML
    private TableColumn<Carriage, Integer> idColumn;
    @FXML
    private TableColumn<Carriage, String> modelColumn;
    @FXML
    private TableColumn<Carriage, String> typeColumn;
    @FXML
    private TableColumn<Carriage, Integer> capacityColumn;
    @FXML
    private Button confirmButton;

    private Convoy convoy;

    private  CarriageSelectionPopupService service;
    private final ObservableList<Carriage> selectedCarriages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModelType()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        carriageTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setConvoy(Convoy convoy) {
        this.convoy = convoy;
        this.service = new CarriageSelectionPopupService(convoy);
        setAvailableCarriages();
    }

    private void setAvailableCarriages() {
        carriageTable.setItems(FXCollections.observableArrayList(service.getCarriagesFromStation()));
    }

    public List<Carriage> getSelectedCarriages() {
        return carriageTable.getSelectionModel().getSelectedItems();
    }

    @FXML
    private void confirmSelection() {
        selectedCarriages.setAll(getSelectedCarriages());
        Window window = confirmButton.getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.close();
        } else {
            window.hide();
        }
    }
}
