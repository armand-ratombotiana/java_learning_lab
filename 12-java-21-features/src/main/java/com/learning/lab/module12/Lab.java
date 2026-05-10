package com.learning.lab.module12;

import java.util.*;

public class Lab {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 12: Java 21 Features ===");
        virtualThreadsDemo();
        patternMatchingDemo();
        recordDemo();
        sealedClassesDemo();
        switchExpressionsDemo();
    }

    static void virtualThreadsDemo() throws Exception {
        System.out.println("\n--- Virtual Threads ---");
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 5).forEach(i ->
                executor.submit(() -> {
                    System.out.println("Virtual thread: " + i);
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    return i;
                })
            );
        }
        System.out.println("Virtual threads demo completed");
    }

    static void patternMatchingDemo() {
        System.out.println("\n--- Pattern Matching ---");
        Object obj1 = "Hello";
        Object obj2 = 42;
        Object obj3 = List.of(1, 2, 3);

        if (obj1 instanceof String s) {
            System.out.println("String length: " + s.length());
        }

        if (obj2 instanceof Integer i && i > 0) {
            System.out.println("Positive integer: " + i);
        }

        String result = switch (obj3) {
            case String s -> "String: " + s;
            case Integer i -> "Integer: " + i;
            case List<?> l -> "List with " + l.size() + " elements";
            default -> "Unknown type";
        };
        System.out.println(result);

        record Point(int x, int y) {}
        record Circle(Point center, int radius) {}

        Object shape = new Circle(new Point(0, 0), 5);
        if (shape instanceof Circle(Point(int x, int y), int r)) {
            System.out.println("Circle at (" + x + "," + y + ") radius " + r);
        }
    }

    static void recordDemo() {
        System.out.println("\n--- Records ---");
        record Person(String name, int age) {
            public Person {
                if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
            }
            public String greet() { return "Hello, I'm " + name; }
        }

        Person john = new Person("John", 30);
        System.out.println("Person: " + john.name() + ", Age: " + john.age());
        System.out.println(john.greet());
        System.out.println("Equals: " + john.equals(new Person("John", 30)));
        System.out.println("HashCode: " + john.hashCode());
        System.out.println("toString: " + john);
    }

    static void sealedClassesDemo() {
        System.out.println("\n--- Sealed Classes ---");
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);

        for (Shape s : List.of(circle, rectangle)) {
            System.out.println(s.getClass().getSimpleName() + " area: " + s.area());
        }

        String result = switch (circle) {
            case Circle c -> "Circle with radius " + c.radius();
            case Rectangle r -> "Rectangle " + r.width() + "x" + r.height();
        };
        System.out.println(result);
    }

    static void switchExpressionsDemo() {
        System.out.println("\n--- Switch Expressions ---");
        String day = "MONDAY";
        String dayType = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> "Unknown";
        };
        System.out.println(day + " is a " + dayType);

        int score = 85;
        String grade = switch (score / 10) {
            case 9, 10 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            default -> "F";
        };
        System.out.println("Score " + score + " = Grade " + grade);
    }

    sealed interface Shape permits Circle, Rectangle {
        double area();
    }
    record Circle(int radius) implements Shape {
        public double area() { return Math.PI * radius * radius; }
    }
    record Rectangle(int width, int height) implements Shape {
        public double area() { return width * height; }
    }
}

import java.util.stream.IntStream;
