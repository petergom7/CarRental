package com.example.carrental.model;

public class Rental {
    private Car car;
    private String rentalType;
    private double totalPrice;

    public Rental(Car car, String rentalType, double totalPrice) {
        this.car = car;
        this.rentalType = rentalType;
        this.totalPrice = totalPrice;
    }

    public Car getCar() { return car; }
    public String getRentalType() { return rentalType; }
    public double getTotalPrice() { return totalPrice; }
}