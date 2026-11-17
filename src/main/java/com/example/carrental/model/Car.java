package com.example.carrental.model;

public class Car {
    private String name;
    private String brand;
    private String type;
    private String rentalType;
    private double pricePerHour;
    private double rating;
    private int reviewCount;
    private String transmission;
    private String horsepower;
    private String fuelEconomy;

    public Car(String name, String brand, String type, String rentalType, double pricePerHour, double rating, int reviewCount, String transmission, String horsepower, String fuelEconomy) {
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.rentalType = rentalType;
        this.pricePerHour = pricePerHour;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.transmission = transmission;
        this.horsepower = horsepower;
        this.fuelEconomy = fuelEconomy;
    }

    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getType() { return type; }
    public String getRentalType() { return rentalType; }
    public double getPricePerHour() { return pricePerHour; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getTransmission() { return transmission; }
    public String getHorsepower() { return horsepower; }
    public String getFuelEconomy() { return fuelEconomy; }
}