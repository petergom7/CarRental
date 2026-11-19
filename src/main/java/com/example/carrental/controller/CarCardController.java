package com.example.carrental.controller;

import com.example.carrental.model.Vehicle;
import com.example.carrental.util.UiUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CarCardController {
    @FXML private VBox root;
    @FXML private Label nameLabel, ratingLabel, heartLabel, emojiLabel, powerLabel, fuelLabel, priceLabel;
    @FXML private ImageView carImage;

    public void setData(Vehicle v, String priceText, String emoji, boolean selected,
                        Runnable onSelect, Runnable onHeart) {
        nameLabel.setText(v.getName());
        ratingLabel.setText(String.format("⭐%.1f (%d)", v.getRating(), v.getReviewCount()));
        heartLabel.setText(v.isFavorite() ? "❤️" : "🤍");
        powerLabel.setText("🐎 " + v.getHorsepower());
        fuelLabel.setText("⛽ " + v.getFuelEconomy());
        priceLabel.setText(priceText);

        Image pic = UiUtil.loadCarImage(v.getName());
        carImage.setImage(pic);
        carImage.setVisible(pic != null);
        carImage.setManaged(pic != null);
        emojiLabel.setText(emoji);
        emojiLabel.setVisible(pic == null);
        emojiLabel.setManaged(pic == null);

        if (selected) {
            root.setStyle(root.getStyle() + "-fx-border-color: #111111; -fx-border-width: 2; -fx-border-radius: 16;");
        }
        root.setOnMouseClicked(e -> onSelect.run());
        heartLabel.setOnMouseClicked(e -> {
            onHeart.run();
            e.consume();
        });
    }
}