package com.learning.lab23;

/**
 * Main entry point for Lab 23: Sealed Classes.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 23: Sealed Classes");
        System.out.println("========================================\n");

        SealedInterfaceExample.showSealedInterface();
        System.out.println();
        SealedClassExample.showSealedClass();
        System.out.println();
        ExhaustiveSwitchExample.showExhaustiveSwitch();

        System.out.println("\n========================================");
        System.out.println("   Lab 23 Complete");
        System.out.println("========================================");
    }
}
