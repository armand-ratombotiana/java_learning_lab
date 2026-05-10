package com.learning.lab.module65;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 65: DDD Lab ===\n");

        System.out.println("1. Domain-Driven Design:");
        System.out.println("   - Ubiquitous Language");
        System.out.println("   - Bounded Contexts");
        System.out.println("   - Strategic design patterns");

        System.out.println("\n2. Building Blocks:");
        System.out.println("   - Entity: unique identity");
        System.out.println("   - Value Object: immutable, no identity");
        System.out.println("   - Aggregate: cluster of related objects");
        System.out.println("   - Domain Event: significant business event");

        System.out.println("\n3. Repository Pattern:");
        System.out.println("   - Abstract data access");
        System.out.println("   - Interface in domain");
        System.out.println("   - Implementation in infrastructure");
        System.out.println("   - Collection-like interface");

        System.out.println("\n4. Domain Services:");
        System.out.println("   - Operations spanning multiple entities");
        System.out.println("   - No state, pure logic");
        System.out.println("   - Application Services orchestration");

        System.out.println("\n5. Bounded Contexts:");
        System.out.println("   - Subdomain mapping");
        System.out.println("   - Context map visualization");
        System.out.println("   - Anticorruption layer");
        System.out.println("   - Published language");

        System.out.println("\n6. Aggregates:");
        System.out.println("   - Aggregate root: entry point");
        System.out.println("   - Invariant enforcement");
        System.out.println("   - Transaction boundary");
        System.out.println("   - Reference by ID");

        System.out.println("\n7. Event Sourcing:");
        System.out.println("   - Events as source of truth");
        System.out.println("   - Event store");
        System.out.println("   - Projections for read model");
        System.out.println("   - CQRS integration");

        System.out.println("\n=== DDD Lab Complete ===");
    }
}