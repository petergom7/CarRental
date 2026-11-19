package com.example.carrental.model;

public class SUV extends Car {
    public SUV(String name, String brand, String transmission, double dailyRate,
                double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    @Override public String getType() { return "SUV"; }
    @Override public double getInsuranceFeePerDay() { return 200.0; }
    @Override public int getSeats() { return 7; }
    @Override public int getDoors() { return 5; }
}
