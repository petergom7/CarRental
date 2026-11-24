package com.example.carrental.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class CustomerDashboardController {

    private static final String NAV_ON = "-fx-background-color: #111111; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand;";
    private static final String NAV_OFF = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-background-radius: 15; -fx-cursor: hand;";

    @FXML private Node overview;
    @FXML private Node rentedCars;
    @FXML private CustomerRentalsController rentedCarsController;

    @FXML private Button navOverview;
    @FXML private Button navRentedCars;

    @FXML
    public void initialize() {
        showPage(overview, navOverview);
    }

    public void setCustomerUsername(String username) {
        if (rentedCarsController != null) {
            rentedCarsController.setCustomerUsername(username);
        }
    }

    @FXML
    private void handleShowOverview() {
        showPage(overview, navOverview);
    }

    @FXML
    private void handleShowRentedCars() {
        showPage(rentedCars, navRentedCars);
    }

    private void showPage(Node page, Button activeButton) {
        for (Node n : new Node[]{overview, rentedCars}) {
            boolean show = (n == page);
            n.setVisible(show);
            n.setManaged(show);
        }
        for (Button b : new Button[]{navOverview, navRentedCars}) {
            b.setStyle(b == activeButton ? NAV_ON : NAV_OFF);
        }
    }
}