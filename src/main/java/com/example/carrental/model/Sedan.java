package com.example.carrental.model;

public class Sedan extends Car {
    public Sedan(String name, String brand, String transmission, double dailyRate,
                double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    @Override public String getType() { return "Sedan"; }
    @Override public double getInsuranceFeePerDay() { return 100.0; }
    @Override public int getSeats() { return 5; }
    @Override public int getDoors() { return 4; }
}
