module com.example.carrental {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.carrental to javafx.fxml;
    opens com.example.carrental.controller to javafx.fxml;

    exports com.example.carrental;
    exports com.example.carrental.model;
    exports com.example.carrental.service;
}