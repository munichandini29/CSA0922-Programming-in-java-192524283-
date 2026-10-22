import java.util.*;

// ==================== INTERFACE ====================
interface Chargeable {
    double chargingFee(int kWh);
}

// ==================== ABSTRACT CLASS ====================
abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty");
        }
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() {
        return regNo;
    }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Minutes cannot be negative");
        if (minutes == 0) return 0.0;
        int hours = (minutes + 59) / 60; // ceiling
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + regNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        return regNo.equals(((Vehicle) o).regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

// ==================== CONCRETE VEHICLES ====================
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
    @Override public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative");
        return 12.0 * kWh;
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
    private final boolean hasMonthlyPass; // staff/student

    public ParkingTicket(Vehicle vehicle, int floor, int slot, long entryTimeMinutes, boolean hasMonthlyPass) {
        this.ticketNo = nextTicketNo++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
        this.hasMonthlyPass = hasMonthlyPass;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMinutes() { return entryTimeMinutes; }
    public boolean hasMonthlyPass() { return hasMonthlyPass; }

    @Override
    public String toString() {
        return "Ticket#" + ticketNo + " | " + vehicle + " | Floor " + floor + " Slot " + slot +
               (hasMonthlyPass ? " | Monthly Pass" : " | Visitor");
    }
}

// ==================== PARKING FACILITY ====================
class ParkingFacility {
    private final String[][] slots;                 // stores regNo or null
    private final Map<String, ParkingTicket> activeTickets; // regNo → ticket

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
        activeTickets = new HashMap<>();
    }

    public ParkingTicket allocate(Vehicle vehicle, long entryTimeMinutes, boolean hasMonthlyPass) {
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            System.out.println("Error: Vehicle " + vehicle.getRegNo() + " is already parked.");
            return null;
        }

        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicle.getRegNo();
                    ParkingTicket ticket = new ParkingTicket(vehicle, f, s, entryTimeMinutes, hasMonthlyPass);
                    activeTickets.put(vehicle.getRegNo(), ticket);
                    return ticket;
                }
            }
        }
        System.out.println("Parking Full! Cannot allocate slot for " + vehicle.getRegNo());
        return null;
    }

    public double release(String regNo, long exitTimeMinutes, int chargedKWh) {
        ParkingTicket ticket = activeTickets.get(regNo);
        if (ticket == null) {
            System.out.println("Error: No active ticket found for " + regNo);
            return -1;
        }

        int floor = ticket.getFloor();
        int slot = ticket.getSlot();

        // free the slot
        slots[floor][slot] = null;
        activeTickets.remove(regNo);

        // calculate fee
        long duration = exitTimeMinutes - ticket.getEntryTimeMinutes();
        double fee = 0.0;

        if (!ticket.hasMonthlyPass()) {
            fee = ticket.getVehicle().calculateFee((int) duration);
        }

        // add charging fee if applicable
        if (ticket.getVehicle() instanceof Chargeable && chargedKWh > 0) {
            Chargeable c = (Chargeable) ticket.getVehicle();
            fee += c.chargingFee(chargedKWh);
        }

        System.out.println("Released: " + ticket);
        System.out.println("Duration: " + duration + " minutes | Total Fee: Rs " + fee);
        return fee;
    }

    public void displayStatus() {
        System.out.println("\n----- Current Parking Status -----");
        for (int f = 0; f < slots.length; f++) {
            System.out.print("Floor " + f + ": ");
            for (int s = 0; s < slots[f].length; s++) {
                System.out.print((slots[f][s] == null ? "[Free]" : "[" + slots[f][s] + "]") + " ");
            }
            System.out.println();
        }
        System.out.println("Active tickets: " + activeTickets.size());
    }
}

// ==================== MAIN DEMO + TESTING ====================
public class Main {
    public static void main(String[] args) {

        // Facility: Floor 0 → 2 slots, Floor 1 → 1 slot
        ParkingFacility facility = new ParkingFacility(new int[]{2, 1});

        System.out.println("========== CAMPUS SMART PARKING SYSTEM ==========\n");

        // ---------- Normal Operations ----------
        System.out.println(">>> Test 1: Normal Entry of different vehicles");
        ParkingTicket t1 = facility.allocate(new TwoWheeler("TN01A1111"), 100, false); // visitor
        ParkingTicket t2 = facility.allocate(new Car("TN02B2222"), 110, true);          // staff (free)
        ParkingTicket t3 = facility.allocate(new ElectricCar("TN03C3333"), 120, false); // visitor EV

        facility.displayStatus();

        // ---------- Boundary: Parking Full ----------
        System.out.println("\n>>> Test 2: Boundary - Parking Full");
        facility.allocate(new Car("TN04D4444"), 130, false);

        // ---------- Release vehicles ----------
        System.out.println("\n>>> Test 3: Normal Exit + Fee Calculation");
        facility.release("TN01A1111", 190, 0);   // 90 minutes → 2 hours × 10 = 20
        facility.release("TN02B2222", 200, 0);   // staff → fee 0
        facility.release("TN03C3333", 240, 10);  // 120 min → 2×30=60 + 10×12=120 → 180

        facility.displayStatus();

        // ---------- Invalid Cases ----------
        System.out.println("\n>>> Test 4: Invalid Cases");
        facility.release("TN99Z9999", 300, 0);                 // unknown vehicle
        facility.allocate(new Car("TN01A1111"), 300, false);   // already left, can re-enter
        facility.allocate(new Car("TN01A1111"), 310, false);   // duplicate entry attempt

        // ---------- Small scenario total ----------
        System.out.println("\n>>> Test 5: Final Status");
        facility.displayStatus();
    }
}
