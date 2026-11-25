package com.example.carrental.model;


public abstract class Car extends Vehicle {
    protected Car(String name, String brand, String transmission, double dailyRate,
                  double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    public abstract int getDoors();
}
