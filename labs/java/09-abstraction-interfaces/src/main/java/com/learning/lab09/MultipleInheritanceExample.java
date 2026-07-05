package com.learning.lab09;

/**
 * Demonstrates how interfaces solve the multiple inheritance problem with default method resolution.
 */
public class MultipleInheritanceExample {

    public static void showMultipleInheritance() {
        System.out.println("=== Multiple Inheritance with Interfaces ===");

        FlyingCar car = new FlyingCar();
        car.drive();
        car.fly();
        car.move();
    }
}

interface Drivable {
    default void drive() { System.out.println("Driving on road"); }
    default void move() { System.out.println("Drivable moving"); }
}

interface Flyable {
    default void fly() { System.out.println("Flying in air"); }
    default void move() { System.out.println("Flyable moving"); }
}

class FlyingCar implements Drivable, Flyable {
    @Override
    public void move() {
        System.out.println("FlyingCar chooses to both drive and fly");
        Drivable.super.move();
        Flyable.super.move();
    }
}
