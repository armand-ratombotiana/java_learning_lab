package com.learning.lab07;

/**
 * Demonstrates method overriding rules, final keyword on methods and classes.
 */
public class OverrideFinalExample {

    public static void showOverrideAndFinal() {
        System.out.println("=== Override & Final ===");

        Vehicle car = new Car();
        System.out.println(car.move());

        Vehicle bike = new Bike();
        System.out.println(bike.move());
    }
}

class Vehicle {
    public String move() {
        return "Vehicle moves";
    }
}

class Car extends Vehicle {
    @Override
    public final String move() {
        return "Car drives on roads";
    }
}

final class Bike extends Vehicle {
    @Override
    public String move() {
        return "Bike pedals on paths";
    }
}
