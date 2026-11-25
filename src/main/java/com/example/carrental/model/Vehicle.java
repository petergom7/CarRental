package com.example.carrental.model;

public abstract class Vehicle {
    private String name;
    private String brand;
    private String transmission;
    private double dailyRate;
    private double rating;
    private int reviewCount;
    private String horsepower;
    private String fuelEconomy;
    private boolean available = true;

    protected Vehicle(String name, String brand, String transmission, double dailyRate,
                      double rating, int reviewCount, String horsepower, String fuelEconomy) {
        setName(name);
        setBrand(brand);
        setTransmission(transmission);
        setDailyRate(dailyRate);
        setRating(rating);
        setReviewCount(reviewCount);
        setHorsepower(horsepower);
        setFuelEconomy(fuelEconomy);
    }

    public abstract String getType();
    public abstract double getInsuranceFeePerDay();
    public abstract int getSeats();

    public double getTotalDailyCost() {
        return dailyRate + getInsuranceFeePerDay();
    }

    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getTransmission() { return transmission; }
    public double getDailyRate() { return dailyRate; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getHorsepower() { return horsepower; }
    public String getFuelEconomy() { return fuelEconomy; }
    public boolean isAvailable() { return available; }


    public void setName(String name) { this.name = Check.text(name, "Vehicle name"); }
    public void setBrand(String brand) { this.brand = Check.text(brand, "Brand"); }

    public void setTransmission(String transmission) {
        if (transmission == null || !(transmission.equals("Automatic") || transmission.equals("Manual"))) {
            throw new IllegalArgumentException("Please choose Automatic or Manual transmission.");
        }
        this.transmission = transmission;
    }

    public void setDailyRate(double dailyRate) {
        if (Double.isNaN(dailyRate) || dailyRate <= 0) {
            throw new IllegalArgumentException("Daily rate must be greater than 0.");
        }
        this.dailyRate = dailyRate;
    }

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) throw new IllegalArgumentException("Rating must be between 0 and 5.");
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        if (reviewCount < 0) throw new IllegalArgumentException("Review count can't be negative.");
        this.reviewCount = reviewCount;
    }

    public void setHorsepower(String horsepower) { this.horsepower = Check.optionalText(horsepower, "N/A", "Horsepower"); }
    public void setFuelEconomy(String fuelEconomy) { this.fuelEconomy = Check.optionalText(fuelEconomy, "N/A", "Fuel economy"); }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return getType() + ": " + name;
    }
}
