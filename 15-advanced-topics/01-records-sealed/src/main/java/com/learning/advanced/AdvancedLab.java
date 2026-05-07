package com.learning.advanced;

public class AdvancedLab {

    public static void main(String[] args) {
        System.out.println("=== Advanced Java Features (Records & Sealed Classes) ===\n");

        demonstrateRecords();
        demonstrateSealedClasses();
        demonstratePatternMatching();
    }

    private static void demonstrateRecords() {
        System.out.println("--- Records ---");

        record Person(String name, int age, String email) {
            public String greeting() {
                return "Hello, I'm " + name;
            }
        }

        Person john = new Person("John", 30, "john@email.com");
        System.out.println(john.name());
        System.out.println(john.greeting());
        System.out.println("toString: " + john);
        System.out.println("equals: " + john.equals(new Person("John", 30, "john@email.com")));
    }

    private static void demonstrateSealedClasses() {
        System.out.println("\n--- Sealed Classes ---");

        sealed class Shape permits Circle, Rectangle, Square {}

        final class Circle extends Shape {
            double radius;
            Circle(double radius) { this.radius = radius; }
            double area() { return Math.PI * radius * radius; }
        }

        final class Rectangle extends Shape {
            double width, height;
            Rectangle(double w, double h) { width = w; height = h; }
            double area() { return width * height; }
        }

        final class Square extends Rectangle {
            Square(double side) { super(side, side); }
        }

        Shape circle = new Circle(5);
        System.out.println("Circle area: " + ((Circle) circle).area());
    }

    private static void demonstratePatternMatching() {
        System.out.println("\n--- Pattern Matching ---");

        Object obj = "Hello Java 21";

        if (obj instanceof String s && s.length() > 5) {
            System.out.println("String: " + s.toUpperCase());
        }

        Object[] objects = {"Hello", 42, 3.14, 'A'};
        for (Object o : objects) {
            String result = switch (o) {
                case String s -> "String: " + s;
                case Integer i -> "Integer: " + (i * 2);
                case Double d -> "Double: " + d;
                default -> "Unknown";
            };
            System.out.println(result);
        }
    }
}