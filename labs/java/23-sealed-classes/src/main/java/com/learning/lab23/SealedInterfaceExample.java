package com.learning.lab23;

/**
 * Demonstrates sealed interfaces with permits clause.
 */
public class SealedInterfaceExample {

    public static void showSealedInterface() {
        System.out.println("=== Sealed Interface ===");

        Vehicle car = new CarSealed();
        Vehicle truck = new Truck();
        Vehicle bus = new Bus();

        processVehicle(car);
        processVehicle(truck);
        processVehicle(bus);
    }

    static void processVehicle(Vehicle v) {
        System.out.println(v.getClass().getSimpleName() + ": " + v.getType());
    }
}

sealed interface Vehicle permits CarSealed, Truck, Bus {
    String getType();
}

final class CarSealed implements Vehicle {
    @Override
    public String getType() { return "Passenger car"; }
}

final class Truck implements Vehicle {
    @Override
    public String getType() { return "Cargo truck"; }
}

final class Bus implements Vehicle {
    @Override
    public String getType() { return "Public bus"; }
}
