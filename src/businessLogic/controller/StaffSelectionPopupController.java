package businessLogic.controller;

import domain.Staff;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

public class StaffSelectionPopupController {
    @FXML private TextField filterField;
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> nameColumn;
    @FXML private TableColumn<Staff, String> surnameColumn;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private FilteredList<Staff> filteredData;
    private Callback<Staff, Void> confirmCallback;

    public void setStaffList(ObservableList<Staff> staffList) {
        filteredData = new FilteredList<>(staffList, s -> true);
        staffTable.setItems(filteredData);
    }

    public void setConfirmCallback(Callback<Staff, Void> callback) {
        this.confirmCallback = callback;
    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        surnameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSurname()));

        filterField.textProperty().addListener((observable, oldVal, newVal) -> {
            filteredData.setPredicate(s -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return s.getName().toLowerCase().contains(lower)
                    || s.getSurname().toLowerCase().contains(lower);
            });
        });
    }

    @FXML
    private void onConfirm() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmCallback != null) {
            confirmCallback.call(selected);
        }
    }

    @FXML
    private void onCancel() {
        staffTable.getScene().getWindow().hide();
    }
}
