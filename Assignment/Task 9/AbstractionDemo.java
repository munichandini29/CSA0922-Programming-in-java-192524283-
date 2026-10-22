// ========== Interface ==========
interface Chargeable {
    double chargingFee(int kWh);

    // default method (Java 8+)
    default void showChargingInfo() {
        System.out.println("This vehicle supports electric charging.");
    }
}

// ========== Abstract Class ==========
abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    // Abstract method - must be implemented by subclasses
    protected abstract double ratePerHour();

    // Concrete method
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

// ========== Concrete Classes ==========
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

class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }

    @Override
    public double chargingFee(int kWh) {
        return 12.0 * kWh;
    }
}

// ========== Demo ==========
public class Main {
    public static void main(String[] args) {

        Vehicle[] vehicles = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };

        System.out.println("=== Abstraction Demo ===");
        for (Vehicle v : vehicles) {
            System.out.print(v + " -> Parking Fee: Rs " + v.calculateFee(120));

            if (v instanceof Chargeable) {
                Chargeable c = (Chargeable) v;
                System.out.print(" | Charging Fee: Rs " + c.chargingFee(8));
                c.showChargingInfo();
            } else {
                System.out.println();
            }
        }
    }
}
