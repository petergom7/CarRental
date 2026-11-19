package com.example.carrental.controller;

import com.example.carrental.model.Vehicle;
import com.example.carrental.service.RentalService;
import com.example.carrental.service.VehicleFactory;
import com.example.carrental.util.UiUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

/** CRUD screen for vehicles. All real work is delegated to RentalService. */
public class VehicleController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> colName;
    @FXML private TableColumn<Vehicle, String> colBrand;
    @FXML private TableColumn<Vehicle, String> colType;
    @FXML private TableColumn<Vehicle, String> colTransmission;
    @FXML private TableColumn<Vehicle, String> colRate;
    @FXML private TableColumn<Vehicle, String> colStatus;

    @FXML private TextField nameField;
    @FXML private TextField brandField;
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> transmissionBox;
    @FXML private TextField rateField;
    @FXML private TextField hpField;
    @FXML private TextField fuelField;

    private final RentalService service = RentalService.getInstance();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colBrand.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBrand()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colTransmission.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransmission()));
        colRate.setCellValueFactory(c -> new SimpleStringProperty(UiUtil.peso(c.getValue().getDailyRate())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAvailable() ? "Available" : "Rented"));

        typeBox.getItems().addAll(VehicleFactory.TYPES);
        transmissionBox.getItems().addAll("Automatic", "Manual");

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) fillForm(newV);
        });

        service.addListener(this::refresh);
        refresh();
    }

    private void refresh() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        vehicleTable.setItems(FXCollections.observableArrayList(service.getAllVehicles()));
        if (selected != null && vehicleTable.getItems().contains(selected)) {
            vehicleTable.getSelectionModel().select(selected);
        }
    }

    private void fillForm(Vehicle v) {
        nameField.setText(v.getName());
        brandField.setText(v.getBrand());
        typeBox.setValue(v.getType());
        typeBox.setDisable(true);
        transmissionBox.setValue(v.getTransmission());
        rateField.setText(BigDecimal.valueOf(v.getDailyRate()).stripTrailingZeros().toPlainString());
        hpField.setText(v.getHorsepower());
        fuelField.setText(v.getFuelEconomy());
    }

    @FXML
    private void handleClear() {
        vehicleTable.getSelectionModel().clearSelection();
        nameField.clear();
        brandField.clear();
        typeBox.setValue(null);
        typeBox.setDisable(false);
        transmissionBox.setValue(null);
        rateField.clear();
        hpField.clear();
        fuelField.clear();
    }

    @FXML
    private void handleAdd() {
        try {
            Vehicle v = VehicleFactory.create(typeBox.getValue(), nameField.getText(), brandField.getText(),
                    transmissionBox.getValue(), UiUtil.parseDouble(rateField.getText(), "Daily rate"),
                    0.0, 0, hpField.getText(), fuelField.getText());
            service.addVehicle(v);
            handleClear();
            UiUtil.showInfo("Vehicle added", v.getName() + " is now available for rent.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.showError(ex.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.showError("Click a vehicle in the table first.");
            return;
        }
        try {
            service.updateVehicle(selected, nameField.getText(), brandField.getText(), transmissionBox.getValue(),
                    UiUtil.parseDouble(rateField.getText(), "Daily rate"), hpField.getText(), fuelField.getText());
            UiUtil.showInfo("Vehicle updated", "Changes saved.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.showError(ex.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.showError("Click a vehicle in the table first.");
            return;
        }
        if (!UiUtil.confirm("Delete vehicle", "Delete " + selected.getName() + "? Its returned-rental history will be removed too.")) {
            return;
        }
        try {
            service.deleteVehicle(selected);
            handleClear();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.showError(ex.getMessage());
        }
    }
}
