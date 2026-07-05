package com.learning.lab25;

/**
 * Main entry point for Lab 25: Optional.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 25: Optional");
        System.out.println("========================================\n");

        OptionalCreationExample.showOptionalCreation();
        System.out.println();
        OptionalOperationsExample.showOptionalOperations();
        System.out.println();
        OptionalFlatMapExample.showOptionalFlatMap();

        System.out.println("\n========================================");
        System.out.println("   Lab 25 Complete");
        System.out.println("========================================");
    }
}
