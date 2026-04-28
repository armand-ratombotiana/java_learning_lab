package com.learning;

/**
 * Abstract class demonstrating polymorphism.
 * Defines common behavior for all shapes.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public abstract class Shape {
    // Abstract method (must be implemented by subclasses)
    public abstract double calculateArea();
    
    public abstract double calculatePerimeter();
    
    // Concrete method (can be used by all subclasses)
    public void displayInfo() {
        System.out.println("Shape: " + getShapeName());
        System.out.println("Area: " + calculateArea());
        System.out.println("Perimeter: " + calculatePerimeter());
    }
    
    // Method to be overridden
    public String getShapeName() {
        return "Generic Shape";
    }
}