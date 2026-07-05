package com.learning.lab28;

/**
 * Demonstrates enum declaration with fields, constructors, and methods.
 */
public class EnumBasicsExample {

    public static void showEnumBasics() {
        System.out.println("=== Enum with Fields & Methods ===");

        for (Planet planet : Planet.values()) {
            System.out.printf("  %s: mass=%.2e kg, radius=%.0f km%n", 
                planet, planet.getMass(), planet.getRadius());
        }

        Planet earth = Planet.EARTH;
        System.out.println("Earth ordinal: " + earth.ordinal());
        System.out.println("Earth name: " + earth.name());
        System.out.println("Parsed: " + Planet.valueOf("MARS"));
    }
}

enum Planet {
    MERCURY(3.303e23, 2_439.7),
    VENUS(4.869e24, 6_051.8),
    EARTH(5.976e24, 6_378.1),
    MARS(6.421e23, 3_397.2),
    JUPITER(1.9e27, 71_492),
    SATURN(5.688e26, 60_268),
    URANUS(8.686e25, 25_559),
    NEPTUNE(1.024e26, 24_764);

    private final double mass;
    private final double radius;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    public double getMass() { return mass; }
    public double getRadius() { return radius; }
}
