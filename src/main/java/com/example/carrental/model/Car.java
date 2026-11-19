package com.example.carrental.model;

/**
 * ABSTRACTION + INHERITANCE (level 2): groups the passenger cars (Sedan, SUV, Hatchback,
 * Pickup, LuxuryCar). Vans and motorcycles extend Vehicle directly.
 * Hierarchy: Vehicle -> Car -> Sedan (3 levels).
 */
public abstract class Car extends Vehicle {
    protected Car(String name, String brand, String transmission, double dailyRate,
                  double rating, int reviewCount, String horsepower, String fuelEconomy) {
        super(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
    }

    public abstract int getDoors();
}
