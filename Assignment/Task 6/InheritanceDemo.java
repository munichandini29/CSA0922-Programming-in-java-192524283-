// ========== Superclass ==========
abstract class Vehicle {
    private final String regNo;

    public Vehicle(String regNo) {
        this.regNo = regNo;
        System.out.println("Vehicle constructor called for " + regNo);
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

// ========== Subclasses ==========
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) {
        super(regNo);   // calls Vehicle constructor
        System.out.println("TwoWheeler constructor called");
    }

    @Override
    protected double ratePerHour() {
        return 10.0;
    }
}

class Car extends Vehicle {
    public Car(String regNo) {
        super(regNo);   // calls Vehicle constructor
        System.out.println("Car constructor called");
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }
}

class ElectricCar extends Car {
    public ElectricCar(String regNo) {
        super(regNo);   // calls Car constructor → which calls Vehicle constructor
        System.out.println("ElectricCar constructor called");
    }

    public double chargingFee(int kWh) {
        return 12.0 * kWh;
    }
}

// ========== Test Class ==========
public class Main {
    public static void main(String[] args) {

        System.out.println("=== Creating TwoWheeler ===");
        Vehicle v1 = new TwoWheeler("TN01A1111");
        System.out.println();

        System.out.println("=== Creating Car ===");
        Vehicle v2 = new Car("TN02B2222");
        System.out.println();

        System.out.println("=== Creating ElectricCar ===");
        Vehicle v3 = new ElectricCar("TN03C3333");
        System.out.println();

        System.out.println("=== Fee Calculation ===");
        System.out.println(v1 + " -> Rs " + v1.calculateFee(90));
        System.out.println(v2 + " -> Rs " + v2.calculateFee(90));
        System.out.println(v3 + " -> Rs " + v3.calculateFee(90));
    }
}
