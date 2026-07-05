package com.learning.lab23;

/**
 * Demonstrates exhaustive switch over sealed types (Java 21+).
 */
public class ExhaustiveSwitchExample {

    public static void showExhaustiveSwitch() {
        System.out.println("=== Exhaustive Switch on Sealed Types ===");

        Weather w1 = new Sunny();
        Weather w2 = new Rainy();
        Weather w3 = new Windy();

        System.out.println("Sunny: " + describeWeather(w1));
        System.out.println("Rainy: " + describeWeather(w2));
        System.out.println("Windy: " + describeWeather(w3));
    }

    static String describeWeather(Weather w) {
        return switch (w) {
            case Sunny s -> "Clear skies, temp=" + s.temperature() + "°C";
            case Rainy r -> "Rainy with " + r.precipitation() + "mm precipitation";
            case Windy wd -> "Windy at " + wd.speed() + " km/h";
        };
    }
}

sealed interface Weather permits Sunny, Rainy, Windy {}

record Sunny(int temperature) implements Weather {}
record Rainy(double precipitation) implements Weather {}
record Windy(int speed) implements Weather {}
