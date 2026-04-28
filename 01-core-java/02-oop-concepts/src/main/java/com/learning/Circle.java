package com.learning;

/**
 * Concrete implementation of Shape demonstrating polymorphism.
 * Represents a circle with radius.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public void setRadius(double radius) {
        if (radius > 0) {
        this.radius = radius;
        }
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
    
    @Override
    public String getShapeName() {
        return "Circle";
    }
    
    @Override
    public String toString() {
        return "Circle{radius=" + radius + "}";
    }
}