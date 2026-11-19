package com.example.carrental.model;

public class Motorcycle extends Vehicle {
    public Motorcycle(String name, String brand, String transmission, double dailyRate,
                double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    @Override public String getType() { return "Motorcycle"; }
    @Override public double getInsuranceFeePerDay() { return 50.0; }
    @Override public int getSeats() { return 2; }
}
