package com.example.carrental.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Locale;

public final class UiUtil {
    private UiUtil() {}

    public static Image loadCarImage(String carName) {
        String slug = carName.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        for (String ext : new String[]{"png", "jpg", "jpeg"}) {
            InputStream in = UiUtil.class.getResourceAsStream("/com/example/carrental/images/" + slug + "." + ext);
            if (in != null) return new Image(in);
        }
        return null;
    }

    public static String peso(double amount) {
        if (amount == Math.rint(amount)) return String.format(Locale.US, "₱%,.0f", amount);
        return String.format(Locale.US, "₱%,.2f", amount);
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Please check your input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    public static int parseInt(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(field + " must be a whole number.");
        }
    }

    public static double parseDouble(String text, String field) {
        try {
            return Double.parseDouble(text.trim().replace(",", ""));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(field + " must be a number.");
        }
    }
}