package com.learning;

/**
 * Interface demonstrating interface concept.
 * Defines contract for objects that can fly.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public interface Flyable {
    // Abstract methods (implicitly public and abstract)
    void fly();
    
    void land();
    
    int getMaxAltitude();
    
    // Default method (Java 8+)
    default void displayFlightInfo() {
        System.out.println("Max Altitude: " + getMaxAltitude() + " meters");
    }
    
    // Static method (Java 8+)
    static void printFlightRules() {
        System.out.println("Flight Rules:");
        System.out.println("1. Check weather conditions");
        System.out.println("2. Maintain safe altitude");
        System.out.println("3. Follow air traffic control");
    }
}