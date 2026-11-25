package com.example.carrental.service;

import com.example.carrental.model.*;

import java.time.LocalDate;
import java.util.List;

public final class RentalFactory {
    public static final List<String> PLANS = List.of("Daily", "Weekly", "Monthly");

    private RentalFactory() {}

    public static Rental create(int id, String plan, Vehicle vehicle, String customer,
                                String contact, LocalDate start, int duration) {
        if (plan == null) throw new IllegalArgumentException("Please choose a rental plan.");
        switch (plan) {
            case "Daily":   return new DailyRental(id, vehicle, customer, contact, start, duration);
            case "Weekly":  return new WeeklyRental(id, vehicle, customer, contact, start, duration);
            case "Monthly": return new MonthlyRental(id, vehicle, customer, contact, start, duration);
            default: throw new IllegalArgumentException("Unknown rental plan: " + plan);
        }
    }
}
