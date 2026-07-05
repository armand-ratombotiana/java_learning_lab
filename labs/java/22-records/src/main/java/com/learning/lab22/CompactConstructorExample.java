package com.learning.lab22;

/**
 * Demonstrates compact constructors and validation in records.
 */
public class CompactConstructorExample {

    public static void showCompactConstructor() {
        System.out.println("=== Compact Constructor & Validation ===");

        try {
            ValidatedRecord valid = new ValidatedRecord("Alice", 25);
            System.out.println("Valid: " + valid);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            ValidatedRecord invalid = new ValidatedRecord("", -5);
            System.out.println("Invalid: " + invalid);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
}

record ValidatedRecord(String name, int age) {
    public ValidatedRecord {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
}
