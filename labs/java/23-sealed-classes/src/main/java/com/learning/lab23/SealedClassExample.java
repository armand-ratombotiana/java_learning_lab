package com.learning.lab23;

/**
 * Demonstrates sealed class hierarchy with permits and exhaustive switch.
 */
public class SealedClassExample {

    public static void showSealedClass() {
        System.out.println("=== Sealed Class Hierarchy ===");

        Shape circle = new CircleS(5);
        Shape rectangle = new RectangleS(4, 6);
        Shape triangle = new TriangleS(3, 8);

        printArea(circle);
        printArea(rectangle);
        printArea(triangle);
    }

    static void printArea(Shape shape) {
        System.out.println(shape.getClass().getSimpleName() + " area: " + shape.area());
    }
}

sealed abstract class Shape permits CircleS, RectangleS, TriangleS {
    public abstract double area();
}

final class CircleS extends Shape {
    private double radius;

    public CircleS(double radius) { this.radius = radius; }

    @Override
    public double area() { return Math.PI * radius * radius; }
}

non-sealed class RectangleS extends Shape {
    private double w, h;

    public RectangleS(double w, double h) { this.w = w; this.h = h; }

    @Override
    public double area() { return w * h; }
}

final class TriangleS extends Shape {
    private double base, height;

    public TriangleS(double base, double height) { this.base = base; this.height = height; }

    @Override
    public double area() { return 0.5 * base * height; }
}
