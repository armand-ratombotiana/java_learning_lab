package com.learning.arch;

public class CleanArchitectureTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 37: Clean Architecture ===");
        demonstrateLayers();
    }

    private static void demonstrateLayers() {
        System.out.println("\n--- Clean Architecture Layers ---");
        System.out.println("1. Domain Layer - Business rules, entities");
        System.out.println("2. Application Layer - Use cases, business logic");
        System.out.println("3. Infrastructure Layer - Database, external services");
        System.out.println("4. Presentation Layer - Controllers, APIs");
        System.out.println("\nDependencies point inward - Domain has no dependencies");
    }
}