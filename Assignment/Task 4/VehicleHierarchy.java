// ========== Abstract Superclass ==========
abstract class Vehicle {
    private final String regNo;

    // Parameterised constructor
    public Vehicle(String regNo) {
        this.regNo = regNo;                 // using 'this'
    }

    public String getRegNo() {
        return regNo;
    }

    // Abstract method - must be overridden
    protected abstract double ratePerHour();

    // Common fee calculation
    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;    // ceiling
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + regNo;
    }
}

// ========== Subclasses ==========
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) {
        super(regNo);                       // constructor chaining
    }

    @Override
    protected double ratePerHour() {
        return 10.0;
    }
}

class Car extends Vehicle {
    public Car(String regNo) {
        super(regNo);                       // constructor chaining
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }
}

class ElectricCar extends Car {
    public ElectricCar(String regNo) {
        super(regNo);                       // constructor chaining
    }

    public double chargingFee(int kWh) {
        return 12.0 * kWh;
    }
}

// ========== ParkingTicket Class ==========
class ParkingTicket {
    private static int nextTicketNo = 1001; // static member

    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final long entryTimeMinutes;

    // Parameterised constructor
    public ParkingTicket(Vehicle vehicle, int floor, int slot, long entryTimeMinutes) {
        this.ticketNo = nextTicketNo++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }

    @Override
    public String toString() {
        return "Ticket#" + ticketNo + " | " + vehicle +
               " | Floor " + floor + " Slot " + slot;
    }
}

// ========== Test Class ==========
public class VehicleHierarchy {
    public static void main(String[] args) {
        Vehicle v1 = new TwoWheeler("TN01AB1111");
        Vehicle v2 = new Car("TN02CD2222");
        Vehicle v3 = new ElectricCar("TN03EF3333");

        ParkingTicket t1 = new ParkingTicket(v1, 0, 0, 100);
        ParkingTicket t2 = new ParkingTicket(v2, 0, 1, 120);
        ParkingTicket t3 = new ParkingTicket(v3, 1, 0, 150);

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);

        System.out.println("Fee for 90 minutes (TwoWheeler): Rs " + v1.calculateFee(90));
        System.out.println("Fee for 90 minutes (Car): Rs " + v2.calculateFee(90));
        System.out.println("Fee for 90 minutes (ElectricCar): Rs " + v3.calculateFee(90));
    }
}
