// ==============================================
// PACKAGE STRUCTURE (explained in comments)
// In real project we would use:
// package parking.model;
// package parking.service;
// package parking.app;
// ==============================================

// ========== Model Classes (would be in parking.model) ==========

abstract class Vehicle {
    // private → strong encapsulation
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty");
        }
        this.regNo = regNo;
    }

    // public getter (read-only access)
    public String getRegNo() {
        return regNo;
    }

    // no setter → registration number cannot be changed after creation

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }
        if (minutes == 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + regNo;
    }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 10.0;
    }
}

class Car extends Vehicle {
    public Car(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }
}

class ElectricCar extends Car {
    public ElectricCar(String regNo) {
        super(regNo);
    }

    public double chargingFee(int kWh) {
        if (kWh < 0) {
            throw new IllegalArgumentException("kWh cannot be negative");
        }
        return 12.0 * kWh;
    }
}

class ParkingTicket {
    private static int nextTicketNo = 1001;

    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;

    public ParkingTicket(Vehicle vehicle, int floor, int slot) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (floor < 0 || slot < 0) {
            throw new IllegalArgumentException("Floor and slot must be non-negative");
        }
        this.ticketNo = nextTicketNo++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
    }

    // Getters only (no setters) → immutable ticket
    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }

    @Override
    public String toString() {
        return "Ticket#" + ticketNo + " | " + vehicle + " | Floor " + floor + " Slot " + slot;
    }
}

// ========== Demo ==========
public class Main {
    public static void main(String[] args) {

        System.out.println("=== Encapsulation Demo ===");

        Vehicle v1 = new TwoWheeler("TN01A1111");
        Vehicle v2 = new Car("TN02B2222");
        ElectricCar v3 = new ElectricCar("TN03C3333");

        ParkingTicket t1 = new ParkingTicket(v1, 0, 0);
        ParkingTicket t2 = new ParkingTicket(v2, 0, 1);
        ParkingTicket t3 = new ParkingTicket(v3, 1, 0);

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);

        System.out.println("\nFee for 90 minutes:");
        System.out.println(v1 + " -> Rs " + v1.calculateFee(90));
        System.out.println(v2 + " -> Rs " + v2.calculateFee(90));
        System.out.println(v3 + " -> Rs " + v3.calculateFee(90));
        System.out.println("Charging fee (10 kWh): Rs " + v3.chargingFee(10));

        // This will throw exception (validation working)
        try {
            Vehicle bad = new Car("");   // empty regNo
        } catch (IllegalArgumentException e) {
            System.out.println("\nValidation working: " + e.getMessage());
        }
    }
}
