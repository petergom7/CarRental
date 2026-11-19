package com.example.carrental.model;

public class Hatchback extends Car {
    public Hatchback(String name, String brand, String transmission, double dailyRate,
                double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    @Override public String getType() { return "Hatchback"; }
    @Override public double getInsuranceFeePerDay() { return 80.0; }
    @Override public int getSeats() { return 5; }
    @Override public int getDoors() { return 5; }
}
