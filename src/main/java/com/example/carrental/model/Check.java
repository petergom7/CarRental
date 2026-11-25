package com.example.carrental.model;

final class Check {
    private Check() {}

    static String text(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        String t = value.trim();
        if (t.indexOf('|') >= 0) {
            throw new IllegalArgumentException(label + " must not contain the | character.");
        }
        return t;
    }

    static String optionalText(String value, String fallback, String label) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return text(value, label);
    }
}
