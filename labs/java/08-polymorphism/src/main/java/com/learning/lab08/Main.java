package com.learning.lab08;

/**
 * Main entry point for Lab 08: Polymorphism.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 08: Polymorphism");
        System.out.println("========================================\n");

        DynamicDispatchExample.showDynamicDispatch();
        System.out.println();
        InstanceOfExample.showInstanceOf();
        System.out.println();
        CovariantReturnExample.showCovariantReturns();

        System.out.println("\n========================================");
        System.out.println("   Lab 08 Complete");
        System.out.println("========================================");
    }
}
