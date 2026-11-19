package com.example.carrental.controller;

import com.example.carrental.model.Rental;
import com.example.carrental.service.RentalService;
import com.example.carrental.util.UiUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Read / update / return / delete rentals. */
public class RentalController {

    @FXML private Label summaryLabel;
    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, Number> colId;
    @FXML private TableColumn<Rental, String> colCustomer;
    @FXML private TableColumn<Rental, String> colContact;
    @FXML private TableColumn<Rental, String> colVehicle;
    @FXML private TableColumn<Rental, String> colPlan;
    @FXML private TableColumn<Rental, String> colStart;
    @FXML private TableColumn<Rental, String> colEnd;
    @FXML private TableColumn<Rental, String> colDuration;
    @FXML private TableColumn<Rental, String> colTotal;
    @FXML private TableColumn<Rental, String> colStatus;

    private final RentalService service = RentalService.getInstance();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));
        colContact.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContact()));
        colVehicle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicle().getName()));
        colPlan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRentalType()));
        colStart.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));
        colEnd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate().toString()));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDurationLabel()));
        // computeTotal() is overridden in Daily/Weekly/MonthlyRental, so each row uses its own formula
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(UiUtil.peso(c.getValue().computeTotal())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        service.addListener(this::refresh);
        refresh();
    }

    private void refresh() {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        rentalTable.setItems(FXCollections.observableArrayList(service.getRentals()));
        if (selected != null && rentalTable.getItems().contains(selected)) {
            rentalTable.getSelectionModel().select(selected);
        }
        int total = rentalTable.getItems().size();
        int active = service.getActiveRentalCount();
        summaryLabel.setText("Active: " + active + "  •  Returned: " + (total - active)
                + "  •  Total booked: " + UiUtil.peso(service.getTotalBooked()));
    }

    private Rental selectedOrWarn() {
        Rental r = rentalTable.getSelectionModel().getSelectedItem();
        if (r == null) UiUtil.showError("Click a rental in the table first.");
        return r;
    }

    @FXML
    private void handleReturn() {
        Rental r = selectedOrWarn();
        if (r == null) return;
        try {
            service.returnRental(r);
            UiUtil.showInfo("Car returned", r.getVehicle().getName() + " is available again.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.showError(ex.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Rental r = selectedOrWarn();
        if (r == null) return;
        String extra = r.isActive() ? " The car will become available again." : "";
        if (!UiUtil.confirm("Delete rental", "Delete rental #" + r.getId() + "?" + extra)) return;
        try {
            service.deleteRental(r);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.showError(ex.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Rental r = selectedOrWarn();
        if (r == null) return;
        if (r.isReturned()) {
            UiUtil.showError("Returned rentals can't be edited.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit rental #" + r.getId());
        dialog.setHeaderText(r.getVehicle().getName() + " • " + r.getRentalType() + " plan");
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField nameField = new TextField(r.getCustomerName());
        TextField contactField = new TextField(r.getContact());
        TextField durationField = new TextField(String.valueOf(r.getDuration()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.addRow(0, new Label("Customer name"), nameField);
        grid.addRow(1, new Label("Contact number"), contactField);
        grid.addRow(2, new Label("Duration (" + r.getRentalType().toLowerCase() + " units)"), durationField);
        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                service.updateRental(r, nameField.getText(), contactField.getText(),
                        UiUtil.parseInt(durationField.getText(), "Duration"));
            } catch (IllegalArgumentException | IllegalStateException ex) {
                UiUtil.showError(ex.getMessage());
                event.consume();
            }
        });
        dialog.showAndWait();
    }
}
