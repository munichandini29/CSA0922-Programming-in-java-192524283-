// Reusing the same classes from Task 4 (simplified)

abstract class Vehicle {
    private final String regNo;

    public Vehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + regNo;
    }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10.0; }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
}

class ElectricCar extends Car {
    public ElectricCar(String regNo) { super(regNo); }
    public double chargingFee(int kWh) { return 12.0 * kWh; }
}

class ParkingTicket {
    // ========== STATIC MEMBER ==========
    private static int nextTicketNo = 1001;   // belongs to the class, not to any object
    private static int totalTicketsIssued = 0;

    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;

    public ParkingTicket(Vehicle vehicle, int floor, int slot) {
        this.ticketNo = nextTicketNo++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        totalTicketsIssued++;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }

    // Static method
    public static int getTotalTicketsIssued() {
        return totalTicketsIssued;
    }

    public static int getNextTicketNo() {
        return nextTicketNo;
    }

    @Override
    public String toString() {
        return "Ticket#" + ticketNo + " | " + vehicle + " | Floor " + floor + " Slot " + slot;
    }
}

public class StaticAndLifeCycle {
    public static void main(String[] args) {

        System.out.println("=== Creating Tickets ===");
        ParkingTicket t1 = new ParkingTicket(new TwoWheeler("TN01A1111"), 0, 0);
        ParkingTicket t2 = new ParkingTicket(new Car("TN02B2222"), 0, 1);
        ParkingTicket t3 = new ParkingTicket(new ElectricCar("TN03C3333"), 1, 0);

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);

        System.out.println("\nTotal tickets issued so far: " + ParkingTicket.getTotalTicketsIssued());
        System.out.println("Next ticket number will be: " + ParkingTicket.getNextTicketNo());

        // ========== Object Life Cycle Demo ==========
        System.out.println("\n=== Object Life Cycle ===");
        System.out.println("t1, t2, t3 are currently reachable (alive)");

        // Make t2 eligible for garbage collection
        t2 = null;
        System.out.println("t2 reference is now null → object becomes eligible for Garbage Collection");

        // Suggest JVM to run Garbage Collector (not guaranteed)
        System.gc();

        System.out.println("After setting t2 = null, only t1 and t3 are still reachable.");
    }
}
