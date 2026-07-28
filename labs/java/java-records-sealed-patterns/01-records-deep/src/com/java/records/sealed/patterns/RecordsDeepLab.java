package com.java.records.sealed.patterns;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

/**
 * Lab 01: Records Deep Dive — Record patterns, compact constructors,
 * local records, JPA-style DTOs.
 */
public class RecordsDeepLab {

    // --- Record with compact constructor ---
    record Temperature(double celsius) {
        Temperature {
            if (celsius < -273.15)
                throw new IllegalArgumentException("Below absolute zero");
            celsius = Math.round(celsius * 10) / 10.0;
        }
    }

    // --- Record implementing an interface ---
    record Point(int x, int y) implements Comparable<Point> {
        @Override
        public int compareTo(Point o) {
            int cmp = Integer.compare(this.x, o.x);
            return cmp != 0 ? cmp : Integer.compare(this.y, o.y);
        }
    }

    // --- Record for JPA-style DTO projection ---
    record TransactionSummary(String category, BigDecimal total) {}

    // --- Local record inside a method ---
    public List<String> summarizeTransactions(Map<String, BigDecimal> transactions) {
        record CategoryTotal(String category, BigDecimal total) {}

        return transactions.entrySet().stream()
                .map(e -> new CategoryTotal(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CategoryTotal::total).reversed())
                .map(c -> c.category() + ": $" + c.total())
                .toList();
    }

    // --- Record pattern matching ---
    sealed interface Shape permits Circle, Rectangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}

    public double area(Shape s) {
        return switch (s) {
            case Circle(double r) -> Math.PI * r * r;
            case Rectangle(double w, double h) -> w * h;
        };
    }

    // --- Nested record pattern ---
    record Address(String street, String city) {}
    record Person(String name, Address address) {}

    public String extractCity(Object obj) {
        if (obj instanceof Person(String name, Address(String street, String city))) {
            return name + " lives in " + city;
        }
        return "Unknown";
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new RecordsDeepLab();

        // Temperature validation
        try {
            new Temperature(-300);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Local records
        var summary = lab.summarizeTransactions(Map.of(
                "Groceries", BigDecimal.valueOf(150),
                "Utilities", BigDecimal.valueOf(200)));
        summary.forEach(System.out::println);

        // Record patterns
        Shape shape = new Circle(5);
        System.out.println("Circle area: " + lab.area(shape));

        // Nested record patterns
        var addr = new Address("123 Main St", "Springfield");
        var person = new Person("Homer", addr);
        System.out.println(lab.extractCity(person));
    }
}
