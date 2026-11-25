package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.Vehicle;
import com.example.carrental.service.RentalFactory;
import com.example.carrental.service.RentalService;
import com.example.carrental.service.VehicleFactory;
import com.example.carrental.util.UiUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class OverviewController {

    private static final String PLAN_ON = "-fx-background-color: #111; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 9;";
    private static final String PLAN_OFF = "-fx-background-color: #eee; -fx-text-fill: #333; -fx-background-radius: 10; -fx-padding: 6 9;";

    @FXML private Label subtitleLabel;
    @FXML private Label availableCountLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label activeRentalsLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortBox;
    @FXML private Button dailyBtn;
    @FXML private Button weeklyBtn;
    @FXML private Button monthlyBtn;
    @FXML private FlowPane brandFilters;
    @FXML private FlowPane typeFilters;
    @FXML private FlowPane carGrid;

    @FXML private Label detailPlaceholder;
    @FXML private VBox detailContent;
    @FXML private Label detailEmoji;
    @FXML private ImageView detailImage;
    @FXML private Label detailName;
    @FXML private Label detailPrice;
    @FXML private Label detailPriceUnit;
    @FXML private Label detailSub;
    @FXML private Label toggleLabel1;
    @FXML private VBox specsBox;
    @FXML private Label specBrand;
    @FXML private Label specPower;
    @FXML private Label specFuel;
    @FXML private Label specSeats;
    @FXML private Label specBase;
    @FXML private Label specInsurance;

    private final RentalService service = RentalService.getInstance();
    private final Set<String> selectedBrands = new HashSet<>();
    private final Set<String> selectedTypes = new HashSet<>();
    private String selectedPlan = "Daily";
    private Vehicle selectedVehicle;

    @FXML
    public void initialize() {
        sortBox.getItems().addAll("Name (A-Z)", "Price: Low to High", "Price: High to Low", "Rating: High to Low");
        sortBox.setValue("Name (A-Z)");
        buildTypeFilters();
        stylePlanButtons();

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        sortBox.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        service.addListener(this::refresh);   // refresh whenever another screen changes the data
        refresh();
    }

    private void refresh() {
        rebuildBrandFilters();
        applyFilters();
        availableCountLabel.setText(String.valueOf(service.getAvailableCars().size()));
        avgRatingLabel.setText(String.format("%.1f/5", service.getAverageRating()));
        activeRentalsLabel.setText(String.valueOf(service.getActiveRentalCount()));
    }

    private void rebuildBrandFilters() {
        Set<String> brands = new TreeSet<>();
        for (Vehicle v : service.getAllVehicles()) brands.add(v.getBrand());
        selectedBrands.retainAll(brands);
        brandFilters.getChildren().clear();
        for (String brand : brands) {
            CheckBox cb = new CheckBox(brand);
            cb.setSelected(selectedBrands.contains(brand));
            cb.setOnAction(e -> {
                if (cb.isSelected()) selectedBrands.add(brand); else selectedBrands.remove(brand);
                applyFilters();
            });
            brandFilters.getChildren().add(cb);
        }
    }

    private void buildTypeFilters() {
        for (String type : VehicleFactory.TYPES) {
            CheckBox cb = new CheckBox(type);
            cb.setOnAction(e -> {
                if (cb.isSelected()) selectedTypes.add(type); else selectedTypes.remove(type);
                applyFilters();
            });
            typeFilters.getChildren().add(cb);
        }
    }

    private void applyFilters() {
        List<Vehicle> list = service.search(searchField.getText(), selectedBrands, selectedTypes, true);
        list.sort(currentComparator());

        if (selectedVehicle != null && !selectedVehicle.isAvailable()) selectedVehicle = null;

        carGrid.getChildren().clear();
        for (Vehicle v : list) carGrid.getChildren().add(buildCard(v));
        if (list.isEmpty()) {
            Label empty = new Label("No available cars match your filters.");
            empty.setStyle("-fx-text-fill: #888888;");
            carGrid.getChildren().add(empty);
        }
        subtitleLabel.setText(list.size() + (list.size() == 1 ? " vehicle" : " vehicles") + " available for rent");
        showDetails(selectedVehicle);
    }

    private Comparator<Vehicle> currentComparator() {
        String sort = sortBox.getValue();
        if ("Price: Low to High".equals(sort)) return Comparator.comparingDouble(Vehicle::getDailyRate);
        if ("Price: High to Low".equals(sort)) return Comparator.comparingDouble(Vehicle::getDailyRate).reversed();
        if ("Rating: High to Low".equals(sort)) return Comparator.comparingDouble(Vehicle::getRating).reversed();
        return Comparator.comparing(Vehicle::getName, String.CASE_INSENSITIVE_ORDER);
    }

    @FXML
    private void handleResetFilters() {
        selectedBrands.clear();
        selectedTypes.clear();
        for (javafx.scene.Node n : typeFilters.getChildren()) ((CheckBox) n).setSelected(false);
        searchField.clear();
        sortBox.setValue("Name (A-Z)");
        selectedPlan = "Daily";
        stylePlanButtonsAndRefresh();
    }

    @FXML private void handleDaily() { setPlan("Daily"); }
    @FXML private void handleWeekly() { setPlan("Weekly"); }
    @FXML private void handleMonthly() { setPlan("Monthly"); }

    private void setPlan(String plan) {
        selectedPlan = plan;
        stylePlanButtonsAndRefresh();
    }

    private void stylePlanButtonsAndRefresh() {
        stylePlanButtons();
        refresh();
    }

    private void stylePlanButtons() {
        dailyBtn.setStyle("Daily".equals(selectedPlan) ? PLAN_ON : PLAN_OFF);
        weeklyBtn.setStyle("Weekly".equals(selectedPlan) ? PLAN_ON : PLAN_OFF);
        monthlyBtn.setStyle("Monthly".equals(selectedPlan) ? PLAN_ON : PLAN_OFF);
    }

    private String unitFor(String plan) {
        switch (plan) {
            case "Weekly": return "week";
            case "Monthly": return "month";
            default: return "day";
        }
    }

    private String emojiFor(Vehicle v) {
        switch (v.getType()) {
            case "Van": return "🚐";
            case "Pickup": return "🛻";
            default: return "🚗";
        }
    }

    private VBox buildCard(Vehicle v) {
        String base = "-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 15; -fx-cursor: hand;";
        String selected = "-fx-border-color: #111111; -fx-border-width: 2; -fx-border-radius: 16;";

        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle(v == selectedVehicle ? base + selected : base);

        Label name = new Label(v.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label rating = new Label(String.format("⭐ %.1f (%d)", v.getRating(), v.getReviewCount()));
        rating.setStyle("-fx-font-size: 11px; -fx-text-fill: #e6a800;");

        Label heart = new Label(v.isFavorite() ? "❤" : "♡");
        heart.setStyle("-fx-font-size: 16px; -fx-cursor: hand;");
        heart.setOnMouseClicked(e -> {
            service.setFavorite(v, !v.isFavorite());
            e.consume();
        });

        HBox top = new HBox(new VBox(name, rating), spacer(), heart);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane image = new StackPane();
        image.setPrefHeight(100);
        image.setStyle("-fx-background-color: #f5f2eb; -fx-background-radius: 12;");
        Image pic = UiUtil.loadCarImage(v.getName());
        if (pic != null) {
            ImageView iv = new ImageView(pic);
            iv.setFitHeight(100);
            iv.setFitWidth(260);
            iv.setPreserveRatio(true);
            image.getChildren().add(iv);
        } else {
            Label emoji = new Label(emojiFor(v));
            emoji.setStyle("-fx-font-size: 48px;");
            image.getChildren().add(emoji);
        }

        Label power = new Label("🐎 " + v.getHorsepower());
        power.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        Label fuel = new Label("⛽ " + v.getFuelEconomy());
        fuel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        Label price = new Label(UiUtil.peso(service.quote(v, selectedPlan, 1)) + " /" + unitFor(selectedPlan));
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        HBox bottom = new HBox(15, power, fuel, spacer(), price);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(top, image, bottom);
        card.setOnMouseClicked(e -> {
            selectedVehicle = v;
            applyFilters();
        });
        return card;
    }
    private Pane spacer() {
        Pane p = new Pane();
        HBox.setHgrow(p, javafx.scene.layout.Priority.ALWAYS);
        return p;
    }


    private void showDetails(Vehicle v) {
        boolean has = v != null;
        detailContent.setVisible(has);
        detailContent.setManaged(has);
        detailPlaceholder.setVisible(!has);
        detailPlaceholder.setManaged(!has);
        if (!has) return;

        Image pic = UiUtil.loadCarImage(v.getName());
        detailImage.setImage(pic);
        detailImage.setVisible(pic != null);
        detailImage.setManaged(pic != null);
        detailEmoji.setVisible(pic == null);
        detailEmoji.setManaged(pic == null);

        detailEmoji.setText(emojiFor(v));
        detailName.setText(v.getName());
        detailPrice.setText(UiUtil.peso(service.quote(v, selectedPlan, 1)));
        detailPriceUnit.setText("/ " + unitFor(selectedPlan));
        detailSub.setText(v.getType() + " • " + v.getTransmission());

        specBrand.setText("Brand: " + v.getBrand());
        specPower.setText("Power: " + v.getHorsepower());
        specFuel.setText("Fuel economy: " + v.getFuelEconomy());
        String seats = "Seats: " + v.getSeats();
        if (v instanceof Car) seats += "  •  Doors: " + ((Car) v).getDoors();   // downcast: only cars have doors
        specSeats.setText(seats);
        specBase.setText("Base rate: " + UiUtil.peso(v.getDailyRate()) + " / day");
        specInsurance.setText("Insurance: " + UiUtil.peso(v.getInsuranceFeePerDay()) + " / day");
    }

    @FXML
    private void handleClearSelection() {
        selectedVehicle = null;
        applyFilters();
    }

    @FXML
    private void handleToggleSpecs() {
        boolean show = !specsBox.isVisible();
        specsBox.setVisible(show);
        specsBox.setManaged(show);
        toggleLabel1.setText(show ? "▲ Hide Specifications" : "▼ Show Specifications");
    }

    @FXML
    private void handleRentNow() {
        if (selectedVehicle == null) {
            UiUtil.showError("Please select a car first.");
            return;
        }
        showRentDialog(selectedVehicle);
    }

    private void showRentDialog(Vehicle v) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Rent " + v.getName());
        dialog.setHeaderText("Enter the rental details");
        ButtonType confirmType = new ButtonType("Confirm Rental", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        TextField contactField = new TextField();
        contactField.setPromptText("09XXXXXXXXX");
        DatePicker startPicker = new DatePicker(LocalDate.now());
        ComboBox<String> planBox = new ComboBox<>();
        planBox.getItems().addAll(RentalFactory.PLANS);
        planBox.setValue(selectedPlan);
        TextField durationField = new TextField("1");
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.addRow(0, new Label("Customer name"), nameField);
        grid.addRow(1, new Label("Contact number"), contactField);
        grid.addRow(2, new Label("Start date"), startPicker);
        grid.addRow(3, new Label("Rental plan"), planBox);
        grid.addRow(4, new Label("Duration (days / weeks / months)"), durationField);
        grid.addRow(5, new Label("Estimated total"), totalLabel);
        dialog.getDialogPane().setContent(grid);

        Runnable updateTotal = () -> {
            try {
                int d = UiUtil.parseInt(durationField.getText(), "Duration");
                totalLabel.setText(UiUtil.peso(service.quote(v, planBox.getValue(), d)));
            } catch (RuntimeException ex) {
                totalLabel.setText("—");
            }
        };
        planBox.valueProperty().addListener((obs, o, n) -> updateTotal.run());
        durationField.textProperty().addListener((obs, o, n) -> updateTotal.run());
        updateTotal.run();

        Rental[] created = new Rental[1];
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmType);
        confirmButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                int duration = UiUtil.parseInt(durationField.getText(), "Duration");
                created[0] = service.createRental(v, planBox.getValue(), nameField.getText(),
                        contactField.getText(), startPicker.getValue(), duration);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                UiUtil.showError(ex.getMessage());
                event.consume();   // keeps the dialog open so the user can fix the input
            }
        });

        dialog.showAndWait();

        if (created[0] != null) {
            Rental r = created[0];
            UiUtil.showInfo("Rental confirmed",
                    "Rental #" + r.getId() + " for " + r.getCustomerName() + "\n"
                            + r.getVehicle().getName() + " • " + r.getRentalType() + " • " + r.getDurationLabel() + "\n"
                            + "Return by: " + r.getEndDate() + "\n"
                            + "Total: " + UiUtil.peso(r.computeTotal()));
        }
    }
}
