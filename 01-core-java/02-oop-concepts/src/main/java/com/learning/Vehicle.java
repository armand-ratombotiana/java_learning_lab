package com.learning;

/**
 * Abstract class demonstrating abstraction.
 * Defines common behavior for all vehicles.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public abstract class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    
    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }
    
    // Abstract methods (must be implemented by subclasses)
    public abstract void start();
    
    public abstract void stop();
    
    public abstract int getMaxSpeed();
    
    // Concrete methods
    public void displayInfo() {
        System.out.println("Vehicle: " + brand + " " + model + " (" + year + ")");
        System.out.println("Max Speed: " + getMaxSpeed() + " km/h");
    }
    
    public String getBrand() {
        return brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public int getYear() {
        return year;
    }
    
    @Override
    public String toString() {
        return "Vehicle{brand='" + brand + "', model='" + model + "', year=" + year + "}";
    }
}