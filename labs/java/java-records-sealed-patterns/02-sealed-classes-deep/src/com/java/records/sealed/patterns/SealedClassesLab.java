package com.java.records.sealed.patterns;

import java.util.*;

/**
 * Lab 02: Sealed Classes Deep Dive — sealed hierarchies, permits,
 * exhaustive switches, and algebraic data types.
 */
public class SealedClassesLab {

    // --- Sealed interface with same-file records ---
    sealed interface JsonValue permits JsonString, JsonNumber, JsonBool, JsonArray, JsonObject, JsonNull {}
    record JsonString(String value) implements JsonValue {}
    record JsonNumber(double value) implements JsonValue {}
    record JsonBool(boolean value) implements JsonValue {}
    record JsonArray(List<JsonValue> items) implements JsonValue {}
    record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}
    record JsonNull() implements JsonValue {}

    // --- Exhaustive switch on sealed type ---
    public String toJson(JsonValue v) {
        return switch (v) {
            case JsonString(var s) -> "\"" + escape(s) + "\"";
            case JsonNumber(var n) -> String.valueOf(n);
            case JsonBool(var b) -> String.valueOf(b);
            case JsonArray(var items) ->
                    "[" + items.stream().map(this::toJson).collect(Collectors.joining(",")) + "]";
            case JsonObject(var fields) -> {
                var entries = fields.entrySet().stream()
                        .map(e -> "\"" + escape(e.getKey()) + "\":" + toJson(e.getValue()))
                        .collect(Collectors.joining(","));
                yield "{" + entries + "}";
            }
            case JsonNull _ -> "null";
        };
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // --- Sealed class with permits ---
    sealed abstract class Shape permits Circle, Rectangle, Triangle {}

    final class Circle extends Shape {
        final double radius;
        Circle(double radius) { this.radius = radius; }
    }

    final class Rectangle extends Shape {
        final double width, height;
        Rectangle(double w, double h) { this.width = w; this.height = h; }
    }

    non-sealed class Triangle extends Shape {
        final double a, b, c;
        Triangle(double a, double b, double c) { this.a = a; this.b = b; this.c = c; }
    }

    public double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius * c.radius;
            case Rectangle r -> r.width * r.height;
            case Triangle t -> {
                double s = (t.a + t.b + t.c) / 2;
                yield Math.sqrt(s * (s - t.a) * (s - t.b) * (s - t.c));
            }
        };
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new SealedClassesLab();

        JsonValue json = new JsonObject(Map.of(
                "name", new JsonString("Alice"),
                "age", new JsonNumber(30),
                "active", new JsonBool(true),
                "tags", new JsonArray(List.of(new JsonString("admin"), new JsonString("user"))),
                "address", new JsonNull()
        ));
        System.out.println(lab.toJson(json));

        Shape circle = lab.new Circle(5);
        System.out.println("Circle area: " + lab.area(circle));
    }
}
