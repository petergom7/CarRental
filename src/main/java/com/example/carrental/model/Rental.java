package com.example.carrental.model;

import java.time.LocalDate;
import java.util.regex.Pattern;

public abstract class Rental {
    private static final Pattern CONTACT = Pattern.compile("^(09\\d{9}|\\+639\\d{9})$");

    private final int id;
    private final Vehicle vehicle;
    private String customerName;
    private String contact;
    private LocalDate startDate;
    private int duration;
    private boolean returned;

    protected Rental(int id, Vehicle vehicle, String customerName, String contact,
                     LocalDate startDate, int duration) {
        if (vehicle == null) throw new IllegalArgumentException("A vehicle is required.");
        if (startDate == null) throw new IllegalArgumentException("Please pick a start date.");
        this.id = id;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.customerName = Check.text(customerName, "Customer name");
        this.contact = checkContact(contact);
        this.duration = checkDuration(duration);
    }

    public abstract double computeTotal();
    public abstract String getRentalType();
    public abstract int getUnitDays();

    protected double baseCostPerDay() {
        return vehicle.getTotalDailyCost();
    }

    public void update(String customerName, String contact, int duration) {
        String c = Check.text(customerName, "Customer name");
        String k = checkContact(contact);
        int d = checkDuration(duration);
        this.customerName = c;
        this.contact = k;
        this.duration = d;
    }

    private String checkContact(String contact) {
        String c = Check.text(contact, "Contact number").replace(" ", "");
        if (!CONTACT.matcher(c).matches()) {
            throw new IllegalArgumentException("Contact number must look like 09123456789.");
        }
        return c;
    }

    private int checkDuration(int duration) {
        if (duration < 1) throw new IllegalArgumentException("Duration must be at least 1.");
        if ((long) duration * getUnitDays() > 365) {
            throw new IllegalArgumentException("A rental can't be longer than 365 days.");
        }
        return duration;
    }

    public LocalDate getEndDate() { return startDate.plusDays((long) duration * getUnitDays()); }
    public String getDurationLabel() {
        String unit = getUnitDays() == 1 ? "day" : getUnitDays() == 7 ? "week" : "month";
        return duration + " " + unit + (duration > 1 ? "s" : "");
    }

    public void markReturned() { this.returned = true; }
    public boolean isReturned() { return returned; }
    public boolean isActive() { return !returned; }
    public String getStatus() { return returned ? "Returned" : "Active"; }

    public int getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public String getCustomerName() { return customerName; }
    public String getContact() { return contact; }
    public LocalDate getStartDate() { return startDate; }
    public int getDuration() { return duration; }
}
