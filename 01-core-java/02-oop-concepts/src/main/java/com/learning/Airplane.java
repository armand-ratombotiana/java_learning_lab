package com.learning;

/**
 * Concrete implementation of Flyable interface.
 * Represents an airplane that can fly.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Airplane implements Flyable {
    private String model;
    private int passengerCapacity;
    
    public Airplane(String model) {
        this.model = model;
        this.passengerCapacity = 200; // Default
    }
    
    public Airplane(String model, int passengerCapacity) {
        this.model = model;
        this.passengerCapacity = passengerCapacity;
    }
    
    public String getModel() {
        return model;
    }
    
    public int getPassengerCapacity() {
        return passengerCapacity;
    }
    
    @Override
    public void fly() {
        System.out.println("Airplane " + model + " is flying with " + passengerCapacity + " passengers");
    }
    
    @Override
    public void land() {
        System.out.println("Airplane " + model + " is landing on runway");
    }
    
    @Override
    public int getMaxAltitude() {
        return 12000; // meters
    }
    
    public void boardPassengers(int count) {
        System.out.println("Boarding " + count + " passengers on " + model);
    }
    
    @Override
    public String toString() {
        return "Airplane{model='" + model + "', capacity=" + passengerCapacity + "}";
    }
}