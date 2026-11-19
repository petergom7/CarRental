package com.example.carrental.model;

import java.time.LocalDate;

public class WeeklyRental extends Rental {
    private static final double DISCOUNT = 0.10;

    public WeeklyRental(int id, Vehicle vehicle, String customerName, String contact, LocalDate startDate, int duration) {
        super(id, vehicle, customerName, contact, startDate, duration);
    }

    @Override public String getRentalType() { return "Weekly"; }
    @Override public int getUnitDays() { return 7; }

    @Override
    public double computeTotal() {
        return baseCostPerDay() * getUnitDays() * getDuration() * (1 - DISCOUNT);
    }
}
