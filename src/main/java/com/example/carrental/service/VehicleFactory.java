package com.example.carrental.service;

import com.example.carrental.model.*;

import java.util.List;

/** Builds the right Vehicle subclass from the type chosen in the UI. */
public final class VehicleFactory {
    public static final List<String> TYPES =
            List.of("Sedan", "SUV", "Hatchback", "Pickup", "Luxury", "Van");

    private VehicleFactory() {}

    public static Vehicle create(String type, String name, String brand, String transmission,
                                 double dailyRate, double rating, int reviewCount,
                                 String horsepower, String fuelEconomy) {
        if (type == null) throw new IllegalArgumentException("Please choose a vehicle type.");
        switch (type) {
            case "Sedan":      return new Sedan(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
            case "SUV":        return new SUV(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
            case "Hatchback":  return new Hatchback(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
            case "Pickup":     return new Pickup(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
            case "Luxury":     return new LuxuryCar(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
            case "Van":        return new Van(name, brand, transmission, dailyRate, rating, reviewCount, horsepower, fuelEconomy);
<<<<<<< HEAD
=======

>>>>>>> 5ec3a549beb86064a44c0485708e23f68a3df8b8
            default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}
