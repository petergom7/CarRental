package com.example.carrental.controller;

import com.example.carrental.Main;
import com.example.carrental.util.UiUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SignInController {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private static final String CUSTOMER_USER = "customer";
    private static final String CUSTOMER_PASS = "customer123";

    private static final String BASE_PATH = "/com/example/carrental/";

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton adminRadio;
    @FXML private ToggleGroup userTypeGroup;

    @FXML
    private void handleSignIn() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty()) {
            UiUtil.showError("Please enter a username.");
            return;
        }
        if (password.isEmpty()) {
            UiUtil.showError("Please enter a password.");
            return;
        }
        if (userTypeGroup.getSelectedToggle() == null) {
            UiUtil.showError("Please choose a user type (Customer or Admin).");
            return;
        }

        boolean isAdmin = adminRadio.isSelected();

        if (isAdmin) {
            if (!username.equals(ADMIN_USER) || !password.equals(ADMIN_PASS)) {
                UiUtil.showError("Invalid Admin username or password.");
                return;
            }
        } else {
            if (!username.equals(CUSTOMER_USER) || !password.equals(CUSTOMER_PASS)) {
                UiUtil.showError("Invalid Customer username or password.");
                return;
            }
        }

        String fxmlFile = isAdmin ? "dashboard.fxml" : "customer_dashboard.fxml";
        String title = isAdmin ? "CarViaRental Admin Dashboard" : "CarViaRental Customer Dashboard";

        URL fxmlUrl = Main.class.getResource(BASE_PATH + fxmlFile);
        if (fxmlUrl == null) {
            UiUtil.showError("Could not find " + BASE_PATH + fxmlFile
                    + ". Make sure it is placed in resources/com/example/carrental and rebuild the project.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 1280, 768);

            if (!isAdmin) {
                CustomerDashboardController ctrl = loader.getController();
                ctrl.setCustomerUsername(username);
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setResizable(true);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(1100);
            stage.setMinHeight(650);
            stage.setWidth(1280);
            stage.setHeight(768);
            stage.setScene(scene);
            stage.setWidth(1296);
            stage.setHeight(808);
            stage.centerOnScreen();
        } catch (IOException e) {
            UiUtil.showError("Unable to open dashboard: " + e.getMessage());
        }
    }
}