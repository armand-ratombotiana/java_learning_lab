package com.learning.lab11;

/**
 * Main entry point for Lab 11: Generics.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 11: Generics");
        System.out.println("========================================\n");

        GenericClassExample.showGenericClass();
        System.out.println();
        GenericMethodExample.showGenericMethods();
        System.out.println();
        WildcardExample.showWildcards();

        System.out.println("\n========================================");
        System.out.println("   Lab 11 Complete");
        System.out.println("========================================");
    }
}
