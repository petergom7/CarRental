package com.example.carrental.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** The shell: top bar, clock, and switching between the three pages. */
public class DashboardController {

    private static final String NAV_ON = "-fx-background-color: #111111; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand;";
    private static final String NAV_OFF = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-background-radius: 15; -fx-cursor: hand;";

    // These two-name pairs come from <fx:include fx:id="overview" ...> in dashboard.fxml
    @FXML private Node overview;
    @FXML private Node vehicles;
    @FXML private Node rentals;

    @FXML private Button navOverview;
    @FXML private Button navVehicles;
    @FXML private Button navRentals;
    @FXML private Label clockLabel;

    @FXML
    public void initialize() {
        showPage(overview, navOverview);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mma");
        Timeline clock = new Timeline(
                new KeyFrame(javafx.util.Duration.ZERO,
                        e -> clockLabel.setText("⏰ " + LocalTime.now().format(fmt) + " (GMT + 8)")),
                new KeyFrame(javafx.util.Duration.seconds(15)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML private void handleShowOverview() { showPage(overview, navOverview); }
    @FXML private void handleShowVehicles() { showPage(vehicles, navVehicles); }
    @FXML private void handleShowRentals() { showPage(rentals, navRentals); }

    private void showPage(Node page, Button activeButton) {
        for (Node n : new Node[]{overview, vehicles, rentals}) {
            boolean show = (n == page);
            n.setVisible(show);
            n.setManaged(show);
        }
        for (Button b : new Button[]{navOverview, navVehicles, navRentals}) {
            b.setStyle(b == activeButton ? NAV_ON : NAV_OFF);
        }
    }
}
