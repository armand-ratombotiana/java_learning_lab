package com.learning.java21;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class Java21FeaturesLab {

    record RecordPoint(int x, int y) {}
    record Point(int x, int y) {}
    sealed interface Shape permits Circle, Rectangle {}
    record Circle(Point center, int radius) implements Shape {}
    record Rectangle(Point topLeft, Point bottomRight) implements Shape {}
    sealed interface Vehicle permits Car, Motorcycle {}
    record Car(String brand, int doors) implements Vehicle {}
    record Motorcycle(String brand, boolean hasSidecar) implements Vehicle {}

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 21 Features Demonstration ===\n");

        // 1. Record Patterns
        System.out.println("1. Record Patterns (JEP 440):");
        Shape c = new Circle(new Point(0, 0), 5);
        Shape r = new Rectangle(new Point(1, 1), new Point(4, 4));
        printShape(c);
        printShape(r);

        // 2. Pattern Matching for switch
        System.out.println("\n2. Pattern Matching for switch (JEP 441):");
        System.out.println("   Process 'Hello': " + processObject("Hello"));
        System.out.println("   Process 42: " + processObject(42));
        System.out.println("   Process 3.14: " + processObject(3.14));

        // 3. String Formatting
        System.out.println("\n3. String Formatting:");
        String name = "World";
        int value = 42;
        String formatted = String.format("Hello %s! Value is %d", name, value);
        System.out.println("   Formatted: " + formatted);

        // 4. Sequenced Collections
        System.out.println("\n4. Sequenced Collections (JEP 431):");
        LinkedHashSet<String> seqSet = new LinkedHashSet<>();
        seqSet.add("first"); seqSet.add("second"); seqSet.add("third");
        System.out.println("   First: " + seqSet.getFirst());
        System.out.println("   Last: " + seqSet.getLast());
        System.out.println("   Reversed: " + seqSet.reversed());

        LinkedList<Integer> seqList = new LinkedList<>();
        seqList.add(10); seqList.add(20); seqList.add(30);
        seqList.addFirst(5);
        seqList.addLast(35);
        System.out.println("   LinkedList: " + seqList);

        // 5. Unnamed Variables
        System.out.println("\n5. Unnamed Variables (JEP 456):");
        List<String> items = List.of("A", "B", "C");
        int count = 0;
        for (String item : items) { count++; }
        System.out.println("   Counted " + count + " items using unnamed variable");

        // 6. Record Pattern with switch
        System.out.println("\n6. Record Pattern with switch:");
        RecordPoint rp1 = new RecordPoint(3, 4);
        RecordPoint rp2 = new RecordPoint(0, 0);
        System.out.println("   " + rp1 + " -> " + describePoint(rp1));
        System.out.println("   " + rp2 + " -> " + describePoint(rp2));

        // 7. Virtual Threads (JEP 444)
        System.out.println("\n7. Virtual Threads (JEP 444):");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future1 = executor.submit(() -> {
                Thread.sleep(100);
                return "Task 1 done";
            });
            var future2 = executor.submit(() -> {
                Thread.sleep(50);
                return "Task 2 done";
            });
            System.out.println("   " + future1.get());
            System.out.println("   " + future2.get());
        }

        // 8. Utility Methods
        System.out.println("\n8. Utility Methods:");
        System.out.println("   Factorial of 5: " + factorial(5));
        System.out.println("   Fibonacci(10): " + fibonacci(10));
        System.out.println("   Is 17 prime? " + isPrime(17));

        // 9. Advanced Streams
        System.out.println("\n9. Advanced Streams:");
        var result = IntStream.rangeClosed(1, 10)
                .filter(Java21FeaturesLab::isPrime)
                .map(n -> n * n)
                .boxed()
                .toList();
        System.out.println("   Prime squares: " + result);

        // 10. Sealed Types
        System.out.println("\n10. Sealed Types:");
        Vehicle car = new Car("Toyota", 4);
        Vehicle bike = new Motorcycle("Honda", true);
        System.out.println("   " + describeVehicle(car));
        System.out.println("   " + describeVehicle(bike));

        System.out.println("\n=== Java 21 Features Complete ===");
    }

    static void printShape(Shape s) {
        switch (s) {
            case Circle(var center, int radius) ->
                System.out.println("   Circle at (" + center.x() + "," + center.y() + "), r=" + radius);
            case Rectangle(var topLeft, var bottomRight) ->
                System.out.println("   Rectangle from (" + topLeft.x() + "," + topLeft.y() + ") to (" +
                    bottomRight.x() + "," + bottomRight.y() + ")");
        }
    }

    static String processObject(Object obj) {
        return switch (obj) {
            case String s -> "String: " + s.length() + " chars";
            case Integer i -> "Integer: " + (i * 2);
            case Double d -> "Double: " + (d * 2);
            case null -> "null value";
            default -> "Unknown type";
        };
    }

    static String describePoint(RecordPoint p) {
        if (p.x() == 0 && p.y() == 0) return "Origin";
        if (p.x() == p.y()) return "Diagonal";
        return "Point(" + p.x() + "," + p.y() + ")";
    }

    static int factorial(int n) {
        return (n <= 1) ? 1 : n * factorial(n - 1);
    }

    static int fibonacci(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    static String describeVehicle(Vehicle v) {
        return switch (v) {
            case Car(var brand, int doors) -> brand + " car with " + doors + " doors";
            case Motorcycle(var brand, var sidecar) -> brand + " motorcycle" + (sidecar ? " with sidecar" : "");
        };
    }
}