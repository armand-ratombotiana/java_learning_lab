package com.learning.patterns;

import java.util.*;

public class AdvancedPatternsTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 36: Advanced Design Patterns ===");
        demonstrateCreationalPatterns();
        demonstrateStructuralPatterns();
        demonstrateBehavioralPatterns();
    }

    private static void demonstrateCreationalPatterns() {
        System.out.println("\n--- Creational Patterns ---");
        System.out.println("1. Builder - Complex object construction");
        System.out.println("2. Factory Method - Subclass decides creation");
        System.out.println("3. Abstract Factory - Family of related objects");
        System.out.println("4. Singleton - One instance globally");
        System.out.println("5. Prototype - Clone existing objects");
    }

    private static void demonstrateStructuralPatterns() {
        System.out.println("\n--- Structural Patterns ---");
        System.out.println("1. Adapter - Convert interface");
        System.out.println("2. Bridge - Decouple abstraction from implementation");
        System.out.println("3. Composite - Tree structure");
        System.out.println("4. Decorator - Add behavior dynamically");
        System.out.println("5. Facade - Simplified interface");
        System.out.println("6. Flyweight - Share common objects");
        System.out.println("7. Proxy - Placeholder for real object");
    }

    private static void demonstrateBehavioralPatterns() {
        System.out.println("\n--- Behavioral Patterns ---");
        System.out.println("1. Chain of Responsibility - Pass request along chain");
        System.out.println("2. Command - Encapsulate request as object");
        System.out.println("3. Iterator - Traverse collection");
        System.out.println("4. Mediator - Central communication hub");
        System.out.println("5. Memento - Save/restore object state");
        System.out.println("6. Observer - Notify dependents of changes");
        System.out.println("7. State - Object changes behavior with state");
        System.out.println("8. Strategy - Interchangeable algorithms");
        System.out.println("9. Template Method - Define algorithm skeleton");
        System.out.println("10. Visitor - Operations on elements");
    }
}