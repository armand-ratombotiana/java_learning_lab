package com.learning.lab08;

/**
 * Demonstrates runtime polymorphism through dynamic method dispatch.
 */
public class DynamicDispatchExample {

    public static void showDynamicDispatch() {
        System.out.println("=== Dynamic Method Dispatch ===");

        Shape[] shapes = {new Circle(5), new Rectangle(4, 6), new Triangle(3, 8)};

        for (Shape shape : shapes) {
            System.out.println(shape.getClass().getSimpleName() + " area: " + shape.area());
        }
    }
}

abstract class Shape {
    public abstract double area();
}

class Circle extends Shape {
    private double radius;

    public Circle(double radius) { this.radius = radius; }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private double w, h;

    public Rectangle(double w, double h) { this.w = w; this.h = h; }

    @Override
    public double area() {
        return w * h;
    }
}

class Triangle extends Shape {
    private double base, height;

    public Triangle(double base, double height) { this.base = base; this.height = height; }

    @Override
    public double area() {
        return 0.5 * base * height;
    }
}
