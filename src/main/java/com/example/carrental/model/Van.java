package com.example.carrental.model;

public class Van extends Vehicle {
    public Van(String name, String brand, String transmission, double dailyRate,
                double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    @Override public String getType() { return "Van"; }
    @Override public double getInsuranceFeePerDay() { return 250.0; }
    @Override public int getSeats() { return 15; }
}
