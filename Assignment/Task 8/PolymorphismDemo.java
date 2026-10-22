// ========== Interface ==========
interface Chargeable {
    double chargingFee(int kWh);
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

    protected abstract double ratePerHour();

    // Overloaded method 1
    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;   // ceiling
        return hours * ratePerHour();
    }

    // Overloaded method 2 (compile-time polymorphism)
    public double calculateFee(int entryHour, int exitHour) {
        int minutes = (exitHour - entryHour) * 60;
        return calculateFee(minutes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + regNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle other = (Vehicle) o;
        return this.regNo.equals(other.regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

// ========== Subclasses ==========
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

// ========== Demo Class ==========
public class Main {
    public static void main(String[] args) {

        // Runtime Polymorphism: array of Vehicle references holding different types
        Vehicle[] parked = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };

        System.out.println("=== Runtime Polymorphism (Dynamic Method Dispatch) ===");
        for (Vehicle v : parked) {
            double fee = v.calculateFee(180);   // 180 minutes = 3 hours

            if (v instanceof Chargeable) {
                Chargeable c = (Chargeable) v;
                fee = fee + c.chargingFee(10);  // add charging for 10 kWh
            }

            System.out.println(v.getClass().getSimpleName() + " " + v.getRegNo() + " -> Rs " + fee);
        }

        // equals() test
        System.out.println();
        System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2")));
    }
}
