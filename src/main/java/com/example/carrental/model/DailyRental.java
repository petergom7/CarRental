package com.example.carrental.model;

import java.time.LocalDate;

public class DailyRental extends Rental {
    private static final double DISCOUNT = 0.0;

    public DailyRental(int id, Vehicle vehicle, String customerName, String contact, LocalDate startDate, int duration) {
        super(id, vehicle, customerName, contact, startDate, duration);
    }

    @Override public String getRentalType() { return "Daily"; }
    @Override public int getUnitDays() { return 1; }

    @Override
    public double computeTotal() {
        return baseCostPerDay() * getUnitDays() * getDuration() * (1 - DISCOUNT);
    }
}
