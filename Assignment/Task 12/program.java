import java.util.*;

// ==================== INTERFACE ====================
interface Chargeable {
    double chargingFee(int kWh);
}

// ==================== ABSTRACT CLASS ====================
abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty())
            throw new IllegalArgumentException("Registration number cannot be empty");
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Minutes cannot be negative");
        if (minutes == 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + regNo;
    }
}

// ==================== EXISTING + NEW VEHICLE TYPES ====================
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10.0; }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
}

class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
    @Override public double chargingFee(int kWh) { return 12.0 * kWh; }
}

// ===== NEW: E-Bike (flat fee) =====
class EBike extends Vehicle {
    public EBike(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 0.0; } // not used
    @Override
    public double calculateFee(int minutes) {
        return 25.0; // flat fee regardless of duration
    }
}

// ===== NEW: Delivery Van (double car rate during 9AM-5PM) =====
class DeliveryVan extends Vehicle {
    public DeliveryVan(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; } // base = car rate

    // Special calculation will be handled with time check
    public double calculateFee(int minutes, boolean isPeakHours) {
        double rate = isPeakHours ? 60.0 : 30.0; // double during 9AM-5PM
        int hours = (minutes + 59) / 60;
        return hours * rate;
    }
}

// ==================== PARKING TICKET ====================
class ParkingTicket {
    private static int nextTicketNo = 1001;

    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final long entryTimeMinutes;
    private final boolean hasMonthlyPass;
    private final boolean hasOneDayPass; // NEW

    public ParkingTicket(Vehicle vehicle, int floor, int slot,
                         long entryTimeMinutes, boolean hasMonthlyPass, boolean hasOneDayPass) {
        this.ticketNo = nextTicketNo++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
        this.hasMonthlyPass = hasMonthlyPass;
        this.hasOneDayPass = hasOneDayPass;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMinutes() { return entryTimeMinutes; }
    public boolean hasMonthlyPass() { return hasMonthlyPass; }
    public boolean hasOneDayPass() { return hasOneDayPass; }

    @Override
    public String toString() {
        String pass = hasMonthlyPass ? "Monthly Pass" : (hasOneDayPass ? "One-Day Pass" : "Visitor");
        return "Ticket#" + ticketNo + " | " + vehicle + " | Floor " + floor + " Slot " + slot + " | " + pass;
    }
}

// ==================== PARKING FACILITY ====================
class ParkingFacility {
    private final String[][] slots;
    private final Map<String, ParkingTicket> activeTickets;

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
        activeTickets = new HashMap<>();
    }

    public ParkingTicket allocate(Vehicle vehicle, long entryTimeMinutes,
                                  boolean hasMonthlyPass, boolean hasOneDayPass) {
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            System.out.println("Error: Vehicle already parked.");
            return null;
        }

        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicle.getRegNo();
                    ParkingTicket t = new ParkingTicket(vehicle, f, s, entryTimeMinutes,
                                                       hasMonthlyPass, hasOneDayPass);
                    activeTickets.put(vehicle.getRegNo(), t);
                    return t;
                }
            }
        }
        System.out.println("Parking Full!");
        return null;
    }

    public double release(String regNo, long exitTimeMinutes, int chargedKWh, boolean isPeakHours) {
        ParkingTicket ticket = activeTickets.get(regNo);
        if (ticket == null) {
            System.out.println("Error: No active ticket for " + regNo);
            return -1;
        }

        slots[ticket.getFloor()][ticket.getSlot()] = null;
        activeTickets.remove(regNo);

        long duration = exitTimeMinutes - ticket.getEntryTimeMinutes();
        double fee = 0.0;

        if (!ticket.hasMonthlyPass() && !ticket.hasOneDayPass()) {
            if (ticket.getVehicle() instanceof DeliveryVan) {
                fee = ((DeliveryVan) ticket.getVehicle()).calculateFee((int) duration, isPeakHours);
            } else {
                fee = ticket.getVehicle().calculateFee((int) duration);
            }
        }

        if (ticket.getVehicle() instanceof Chargeable && chargedKWh > 0) {
            fee += ((Chargeable) ticket.getVehicle()).chargingFee(chargedKWh);
        }

        System.out.println("Released: " + ticket);
        System.out.println("Duration: " + duration + " min | Fee: Rs " + fee);
        return fee;
    }

    public void displayStatus() {
        System.out.println("\n----- Parking Status -----");
        for (int f = 0; f < slots.length; f++) {
            System.out.print("Floor " + f + ": ");
            for (String s : slots[f]) {
                System.out.print((s == null ? "[Free]" : "[" + s + "]") + " ");
            }
            System.out.println();
        }
    }
}

// ==================== DEMO ====================
public class Main {
    public static void main(String[] args) {
        // Extended facility: 3 floors with different slots (5, 3, 2)
        ParkingFacility facility = new ParkingFacility(new int[]{5, 3, 2});

        System.out.println("========== UPDATED CAMPUS PARKING SYSTEM ==========\n");

        // New vehicle types
        facility.allocate(new EBike("EB01X1111"), 100, false, false);
        facility.allocate(new DeliveryVan("DV02Y2222"), 110, false, false);
        facility.allocate(new Car("TN03Z3333"), 120, false, true); // one-day pass
        facility.allocate(new ElectricCar("EV04A4444"), 130, false, false);

        facility.displayStatus();

        System.out.println("\n>>> Releasing vehicles");
        facility.release("EB01X1111", 200, 0, false);          // flat 25
        facility.release("DV02Y2222", 200, 0, true);           // peak hours → double rate
        facility.release("TN03Z3333", 200, 0, false);          // one-day pass → 0
        facility.release("EV04A4444", 250, 8, false);          // parking + charging

        facility.displayStatus();
    }
}
