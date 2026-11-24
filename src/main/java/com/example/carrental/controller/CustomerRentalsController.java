package com.example.carrental.controller;

import com.example.carrental.model.Rental;
import com.example.carrental.service.RentalService;
import com.example.carrental.util.UiUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class CustomerRentalsController {

    @FXML private Label summaryLabel;
    @FXML private TableView<Rental> rentedTable;
    @FXML private TableColumn<Rental, Number> colId;
    @FXML private TableColumn<Rental, String> colVehicle;
    @FXML private TableColumn<Rental, String> colPlan;
    @FXML private TableColumn<Rental, String> colStart;
    @FXML private TableColumn<Rental, String> colEnd;
    @FXML private TableColumn<Rental, String> colDuration;
    @FXML private TableColumn<Rental, String> colTotal;
    @FXML private TableColumn<Rental, String> colStatus;

    private final RentalService service = RentalService.getInstance();
    private String customerUsername = "";

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colVehicle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicle().getName()));
        colPlan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRentalType()));
        colStart.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));
        colEnd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate().toString()));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDurationLabel()));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(UiUtil.peso(c.getValue().computeTotal())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        service.addListener(this::refresh);
        refresh();
    }

    public void setCustomerUsername(String username) {
        this.customerUsername = username == null ? "" : username.trim();
        refresh();
    }

    private void refresh() {
        List<Rental> all = service.getRentals();
        List<Rental> matched = new ArrayList<>();

        if (!customerUsername.isEmpty()) {
            for (Rental r : all) {
                if (r.getCustomerName().equalsIgnoreCase(customerUsername)) {
                    matched.add(r);
                }
            }
        }

        List<Rental> rows = matched.isEmpty() ? all : matched;
        rentedTable.setItems(FXCollections.observableArrayList(rows));
        summaryLabel.setText("Total rented records: " + rows.size());
    }
}