package com.example.carrental.model;

import java.time.LocalDate;

public class MonthlyRental extends Rental {
    private static final double DISCOUNT = 0.20;

    public MonthlyRental(int id, Vehicle vehicle, String customerName, String contact, LocalDate startDate, int duration) {
        super(id, vehicle, customerName, contact, startDate, duration);
    }

    @Override public String getRentalType() { return "Monthly"; }
    @Override public int getUnitDays() { return 30; }

    @Override
    public double computeTotal() {
        return baseCostPerDay() * getUnitDays() * getDuration() * (1 - DISCOUNT);
    }
}
