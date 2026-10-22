abstract class Vehicle {
    private final String regNo;

    public Vehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    // This method will be overridden by subclasses
    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;   // ceiling
        return hours * ratePerHour();
    }

    // ===== Overriding Object class methods =====
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

    @Override
    protected double ratePerHour() {
        return 30.0;   // same as Car, but can be changed later
    }

    public double chargingFee(int kWh) {
        return 12.0 * kWh;
    }
}

public class Main {
    public static void main(String[] args) {

        Vehicle v1 = new TwoWheeler("TN01A1111");
        Vehicle v2 = new Car("TN02B2222");
        Vehicle v3 = new ElectricCar("TN03C3333");
        Vehicle v4 = new Car("TN02B2222");   // same regNo as v2

        System.out.println("=== toString() Overriding ===");
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);

        System.out.println("\n=== calculateFee() Overriding ===");
        System.out.println(v1 + " -> Rs " + v1.calculateFee(90));
        System.out.println(v2 + " -> Rs " + v2.calculateFee(90));
        System.out.println(v3 + " -> Rs " + v3.calculateFee(90));

        System.out.println("\n=== equals() and hashCode() Overriding ===");
        System.out.println("v2.equals(v4) : " + v2.equals(v4));   // true (same regNo)
        System.out.println("v1.equals(v2) : " + v1.equals(v2));   // false
        System.out.println("v2.hashCode() == v4.hashCode() : " + (v2.hashCode() == v4.hashCode()));
    }
}
