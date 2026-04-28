package com.learning;

/**
 * Concrete implementation of Vehicle demonstrating abstraction.
 * Represents a car with specific behavior.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Car extends Vehicle {
    private int numberOfDoors;
    
    public Car(String brand, String model, int year) {
        super(brand, model, year);
        this.numberOfDoors = 4; // Default
    }
    
    public Car(String brand, String model, int year, int numberOfDoors) {
        super(brand, model, year);
        this.numberOfDoors = numberOfDoors;
    }
    
    public int getNumberOfDoors() {
        return numberOfDoors;
    }
    
    public void setNumberOfDoors(int numberOfDoors) {
        if (numberOfDoors > 0) {
        this.numberOfDoors = numberOfDoors;
        }
    }
    
    @Override
    public void start() {
        System.out.println("Car " + brand + " " + model + " is starting with ignition key");
    }
    
    @Override
    public void stop() {
        System.out.println("Car " + brand + " " + model + " is stopping");
    }
    
    @Override
    public int getMaxSpeed() {
        return 200; // km/h
    }
    
    public void openTrunk() {
        System.out.println("Opening trunk of " + brand + " " + model);
    }
    
    @Override
    public String toString() {
        return "Car{brand='" + brand + "', model='" + model + "', year=" + year + 
       ", doors=" + numberOfDoors + "}";
    }
}