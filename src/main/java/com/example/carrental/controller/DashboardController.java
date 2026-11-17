package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.service.RentalService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class DashboardController {

    @FXML
    private Label selectedCarNameLabel;

    private RentalService rentalService = new RentalService();

    @FXML
    public void initialize() {
        List<Car> cars = rentalService.getAvailableCars();
    }

    @FXML
    private void handleRentNow() {
        System.out.println("Rent Now button clicked!");
    }
}