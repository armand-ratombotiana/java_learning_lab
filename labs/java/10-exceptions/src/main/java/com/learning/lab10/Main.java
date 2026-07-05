package com.learning.lab10;

/**
 * Main entry point for Lab 10: Exceptions.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 10: Exceptions");
        System.out.println("========================================\n");

        TryCatchFinallyExample.showTryCatchFinally();
        System.out.println();
        TryWithResourcesExample.showTryWithResources();
        System.out.println();
        CustomExceptionExample.showCustomExceptions();

        System.out.println("\n========================================");
        System.out.println("   Lab 10 Complete");
        System.out.println("========================================");
    }
}
