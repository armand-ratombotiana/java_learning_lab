package com.learning;

/**
 * Concrete implementation of Shape demonstrating polymorphism.
 * Represents a rectangle with width and height.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        if (width > 0) {
        this.width = width;
        }
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        if (height > 0) {
        this.height = height;
        }
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
    
    @Override
    public String getShapeName() {
        return "Rectangle";
    }
    
    public boolean isSquare() {
        return width == height;
    }
    
    @Override
    public String toString() {
        return "Rectangle{width=" + width + ", height=" + height + "}";
    }
}