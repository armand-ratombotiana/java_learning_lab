package com.learning.lab22;

/**
 * Demonstrates record declaration, accessor methods, equals/hashCode/toString auto-generation.
 */
public class RecordDeclarationExample {

    public static void showRecordBasics() {
        System.out.println("=== Record Declaration ===");

        PersonRecord alice = new PersonRecord("Alice", 30);
        PersonRecord bob = new PersonRecord("Bob", 25);
        PersonRecord alice2 = new PersonRecord("Alice", 30);

        System.out.println("alice: " + alice);
        System.out.println("bob: " + bob);
        System.out.println("alice.name(): " + alice.name());
        System.out.println("alice.age(): " + alice.age());
        System.out.println("alice.equals(alice2): " + alice.equals(alice2));
        System.out.println("alice.hashCode() == alice2.hashCode(): " 
            + (alice.hashCode() == alice2.hashCode()));
        System.out.println("alice == alice2: " + (alice == alice2));
    }
}

record PersonRecord(String name, int age) {}
