package com.learning;

/**
 * Concrete implementation of Vehicle demonstrating abstraction.
 * Represents a motorcycle with specific behavior.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Motorcycle extends Vehicle {
    private boolean hasSidecar;
    
    public Motorcycle(String brand, String model, int year) {
        super(brand, model, year);
        this.hasSidecar = false;
    }
    
    public Motorcycle(String brand, String model, int year, boolean hasSidecar) {
        super(brand, model, year);
        this.hasSidecar = hasSidecar;
    }
    
    public boolean hasSidecar() {
        return hasSidecar;
    }
    
    public void setHasSidecar(boolean hasSidecar) {
        this.hasSidecar = hasSidecar;
    }
    
    @Override
    public void start() {
        System.out.println("Motorcycle " + brand + " " + model + " is starting with kick/button");
    }
    
    @Override
    public void stop() {
        System.out.println("Motorcycle " + brand + " " + model + " is stopping");
    }
    
    @Override
    public int getMaxSpeed() {
        return 180; // km/h
    }
    
    public void wheelie() {
        System.out.println("Motorcycle " + brand + " " + model + " is doing a wheelie!");
    }
    
    @Override
    public String toString() {
        return "Motorcycle{brand='" + brand + "', model='" + model + "', year=" + year + 
       ", hasSidecar=" + hasSidecar + "}";
    }
}