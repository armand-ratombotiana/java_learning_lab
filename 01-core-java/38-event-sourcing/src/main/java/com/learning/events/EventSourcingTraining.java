package com.learning.events;

public class EventSourcingTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 38: Event Sourcing & CQRS ===");
        demonstrateConcepts();
    }

    private static void demonstrateConcepts() {
        System.out.println("\n--- Event Sourcing ---");
        System.out.println("- Store all state changes as events");
        System.out.println("- Replay events to reconstruct state");
        System.out.println("- Complete audit trail");
        System.out.println("\n--- CQRS ---");
        System.out.println("- Command Query Responsibility Segregation");
        System.out.println("- Separate read and write models");
        System.out.println("- Optimize each for its purpose");
    }
}