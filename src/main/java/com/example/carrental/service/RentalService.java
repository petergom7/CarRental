package com.example.carrental.service;

import com.example.carrental.model.Car;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Toyota Vios 1.3 XLE", "Toyota", "Sedan", "Daily", 1800.0, 4.9, 167, "Automatic", "6993 hp", "425m (15min)"));
        cars.add(new Car("Honda City", "Honda", "Sedan", "Daily", 1950.0, 5.0, 93, "Automatic", "652 hp", "320m (10min)"));
        cars.add(new Car("Mitsubishi Mirage G4", "Mitsubishi", "Sedan", "Daily", 1600.0, 4.7, 133, "Manual", "652 hp", "110m (9min)"));
        cars.add(new Car("Mitsubishi Xpander", "Mitsubishi", "SUV", "Daily", 2500.0, 5.0, 134, "Automatic", "652 hp", "493m (7min)"));
        return cars;
    }
}