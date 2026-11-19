package com.example.carrental.service;

import com.example.carrental.model.Rental;
import com.example.carrental.model.Vehicle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * All the app's business logic lives here, NOT in the controllers.
 * Data is kept in two ArrayLists (mixed subclasses) and auto-saved to CSV after every change.
 */
public class RentalService {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path VEHICLE_FILE = DATA_DIR.resolve("vehicles.csv");
    private static final Path RENTAL_FILE = DATA_DIR.resolve("rentals.csv");

    private static RentalService instance;

    public static RentalService getInstance() {
        if (instance == null) instance = new RentalService();
        return instance;
    }

    private final ArrayList<Vehicle> vehicles = new ArrayList<>();   // holds Sedan, SUV, Van, ...
    private final ArrayList<Rental> rentals = new ArrayList<>();     // holds Daily/Weekly/MonthlyRental
    private final List<Runnable> listeners = new ArrayList<>();
    private int nextRentalId = 1;

    private RentalService() {
        if (!load()) {
            vehicles.clear();
            rentals.clear();
            seedVehicles();
            save();
        }
    }

    // ------------------------------------------------------------------ change listeners
    /** Controllers register here so every screen refreshes when data changes. */
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyChanges() {
        save();
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    // ------------------------------------------------------------------ READ
    public List<Vehicle> getAllVehicles() { return new ArrayList<>(vehicles); }

    public List<Vehicle> getAvailableCars() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) if (v.isAvailable()) result.add(v);
        return result;
    }

    public List<Rental> getRentals() { return new ArrayList<>(rentals); }

    /** Empty brand/type sets mean "no filter". */
    public List<Vehicle> search(String query, Set<String> brands, Set<String> types, boolean availableOnly) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (availableOnly && !v.isAvailable()) continue;
            if (!q.isEmpty() && !(v.getName().toLowerCase().contains(q)
                    || v.getBrand().toLowerCase().contains(q)
                    || v.getType().toLowerCase().contains(q))) continue;
            if (!brands.isEmpty() && !brands.contains(v.getBrand())) continue;
            if (!types.isEmpty() && !types.contains(v.getType())) continue;
            result.add(v);
        }
        return result;
    }

    // ------------------------------------------------------------------ CREATE / UPDATE / DELETE: vehicles
    public void addVehicle(Vehicle v) {
        if (v == null) throw new IllegalArgumentException("No vehicle to add.");
        checkDuplicateName(v.getName(), null);
        vehicles.add(v);
        notifyChanges();
    }

    /** Edits in place. The type can't change (that would need a different subclass). */
    public void updateVehicle(Vehicle target, String name, String brand, String transmission,
                              double dailyRate, String horsepower, String fuelEconomy) {
        if (target == null || !vehicles.contains(target)) throw new IllegalArgumentException("Select a vehicle first.");
        // Build a throw-away copy first: if any value is invalid it throws BEFORE we touch the real one.
        Vehicle probe = VehicleFactory.create(target.getType(), name, brand, transmission, dailyRate,
                target.getRating(), target.getReviewCount(), horsepower, fuelEconomy);
        checkDuplicateName(probe.getName(), target);
        target.setName(probe.getName());
        target.setBrand(probe.getBrand());
        target.setTransmission(probe.getTransmission());
        target.setDailyRate(probe.getDailyRate());
        target.setHorsepower(probe.getHorsepower());
        target.setFuelEconomy(probe.getFuelEconomy());
        notifyChanges();
    }

    public void deleteVehicle(Vehicle v) {
        if (v == null || !vehicles.contains(v)) throw new IllegalArgumentException("Select a vehicle first.");
        for (Rental r : rentals) {
            if (r.getVehicle() == v && r.isActive()) {
                throw new IllegalStateException(v.getName() + " is currently rented. Return it before deleting.");
            }
        }
        rentals.removeIf(r -> r.getVehicle() == v);   // removeIf is safe; removing inside a for-each would crash
        vehicles.remove(v);
        notifyChanges();
    }

    public void setFavorite(Vehicle v, boolean favorite) {
        v.setFavorite(favorite);
        notifyChanges();
    }

    private void checkDuplicateName(String name, Vehicle ignore) {
        for (Vehicle other : vehicles) {
            if (other != ignore && other.getName().equalsIgnoreCase(name.trim())) {
                throw new IllegalArgumentException("A vehicle named \"" + name.trim() + "\" already exists.");
            }
        }
    }

    // ------------------------------------------------------------------ CREATE / UPDATE / DELETE: rentals
    public Rental createRental(Vehicle vehicle, String plan, String customer, String contact,
                               LocalDate start, int duration) {
        if (vehicle == null) throw new IllegalArgumentException("Please select a car to rent.");
        if (!vehicles.contains(vehicle)) throw new IllegalStateException("That vehicle no longer exists.");
        if (!vehicle.isAvailable()) throw new IllegalStateException(vehicle.getName() + " is already rented out.");
        if (start != null && start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date can't be in the past.");
        }
        Rental rental = RentalFactory.create(nextRentalId, plan, vehicle, customer, contact, start, duration);
        nextRentalId++;
        rentals.add(rental);
        vehicle.setAvailable(false);
        notifyChanges();
        return rental;
    }

    public void updateRental(Rental rental, String customer, String contact, int duration) {
        if (rental == null || !rentals.contains(rental)) throw new IllegalArgumentException("Select a rental first.");
        if (rental.isReturned()) throw new IllegalStateException("Returned rentals can't be edited.");
        rental.update(customer, contact, duration);
        notifyChanges();
    }

    public void returnRental(Rental rental) {
        if (rental == null || !rentals.contains(rental)) throw new IllegalArgumentException("Select a rental first.");
        if (rental.isReturned()) throw new IllegalStateException("This car was already returned.");
        rental.markReturned();
        rental.getVehicle().setAvailable(true);
        notifyChanges();
    }

    public void deleteRental(Rental rental) {
        if (rental == null || !rentals.contains(rental)) throw new IllegalArgumentException("Select a rental first.");
        if (rental.isActive()) rental.getVehicle().setAvailable(true);
        rentals.remove(rental);
        notifyChanges();
    }

    // ------------------------------------------------------------------ summary + price quote
    public double getAverageRating() {
        if (vehicles.isEmpty()) return 0;
        double sum = 0;
        for (Vehicle v : vehicles) sum += v.getRating();
        return sum / vehicles.size();
    }

    public int getActiveRentalCount() {
        int count = 0;
        for (Rental r : rentals) if (r.isActive()) count++;
        return count;
    }

    /** Polymorphism in action: computeTotal() runs a different formula per rental subclass. */
    public double getTotalBooked() {
        double total = 0;
        for (Rental r : rentals) total += r.computeTotal();
        return total;
    }

    /** Price for `duration` units of `plan` (1 day / 1 week / 1 month each). Nothing is saved. */
    public double quote(Vehicle vehicle, String plan, int duration) {
        return RentalFactory.create(0, plan, vehicle, "Quote", "09000000000", LocalDate.now(), duration).computeTotal();
    }

    // ------------------------------------------------------------------ CSV save / load
    private void save() {
        try {
            Files.createDirectories(DATA_DIR);
            List<String> vLines = new ArrayList<>();
            for (Vehicle v : vehicles) {
                vLines.add(String.join("|", v.getType(), v.getName(), v.getBrand(), v.getTransmission(),
                        String.valueOf(v.getDailyRate()), String.valueOf(v.getRating()),
                        String.valueOf(v.getReviewCount()), v.getHorsepower(), v.getFuelEconomy(),
                        String.valueOf(v.isAvailable()), String.valueOf(v.isFavorite())));
            }
            Files.write(VEHICLE_FILE, vLines);

            List<String> rLines = new ArrayList<>();
            for (Rental r : rentals) {
                rLines.add(String.join("|", String.valueOf(r.getId()), r.getRentalType(), r.getVehicle().getName(),
                        r.getCustomerName(), r.getContact(), r.getStartDate().toString(),
                        String.valueOf(r.getDuration()), String.valueOf(r.isReturned())));
            }
            Files.write(RENTAL_FILE, rLines);
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
        }
    }

    private boolean load() {
        if (!Files.exists(VEHICLE_FILE)) return false;
        try {
            for (String line : Files.readAllLines(VEHICLE_FILE)) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                Vehicle v = VehicleFactory.create(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]),
                        Double.parseDouble(p[5]), Integer.parseInt(p[6]), p[7], p[8]);
                v.setAvailable(Boolean.parseBoolean(p[9]));
                v.setFavorite(Boolean.parseBoolean(p[10]));
                vehicles.add(v);
            }
            if (Files.exists(RENTAL_FILE)) {
                for (String line : Files.readAllLines(RENTAL_FILE)) {
                    if (line.isBlank()) continue;
                    String[] p = line.split("\\|", -1);
                    Vehicle v = findByName(p[2]);
                    if (v == null) continue;
                    int id = Integer.parseInt(p[0]);
                    Rental r = RentalFactory.create(id, p[1], v, p[3], p[4], LocalDate.parse(p[5]), Integer.parseInt(p[6]));
                    if (Boolean.parseBoolean(p[7])) r.markReturned();
                    rentals.add(r);
                    nextRentalId = Math.max(nextRentalId, id + 1);
                }
            }
            return !vehicles.isEmpty();
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not read saved data, starting fresh: " + e.getMessage());
            return false;
        }
    }

    private Vehicle findByName(String name) {
        for (Vehicle v : vehicles) if (v.getName().equals(name)) return v;
        return null;
    }

    private void seedVehicles() {
        vehicles.add(VehicleFactory.create("Luxury", "BMW M5 Touring", "Bmw", "Automatic", 38400, 4.9, 27, "727 hp", "9 km/L"));
        vehicles.add(VehicleFactory.create("Luxury", "Lexus LC 500 ", "Toyota", "Automatic", 34600, 5.0, 45, "471  hp", "8.62 km/L"));
        vehicles.add(VehicleFactory.create("Sedan", "Toyota Vios 1.3 XLE", "Toyota", "Automatic", 1800, 4.9, 167, "98 hp", "17 km/L"));
        vehicles.add(VehicleFactory.create("Sedan", "Honda City", "Honda", "Automatic", 1950, 5.0, 93, "121 hp", "16 km/L"));
        vehicles.add(VehicleFactory.create("Sedan", "Mitsubishi Mirage G4", "Mitsubishi", "Manual", 1600, 4.7, 133, "78 hp", "20 km/L"));
        vehicles.add(VehicleFactory.create("SUV", "Mitsubishi Xpander", "Mitsubishi", "Automatic", 2500, 5.0, 134, "104 hp", "14 km/L"));
        vehicles.add(VehicleFactory.create("Hatchback", "Suzuki Swift", "Suzuki", "Automatic", 1700, 4.6, 58, "82 hp", "21 km/L"));
        vehicles.add(VehicleFactory.create("Pickup", "Toyota Hilux", "Toyota", "Manual", 3200, 4.8, 76, "148 hp", "12 km/L"));
        vehicles.add(VehicleFactory.create("Van", "Nissan Urvan", "Nissan", "Manual", 3000, 4.5, 41, "129 hp", "10 km/L"));
    }
}
