package com.learning.lab24;

/**
 * Main entry point for Lab 24: Pattern Matching.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 24: Pattern Matching");
        System.out.println("========================================\n");

        InstanceOfPatternMatchingExample.showInstanceOfMatching();
        System.out.println();
        SwitchPatternMatchingExample.showSwitchMatching();
        System.out.println();
        GuardedPatternsExample.showGuardedPatterns();

        System.out.println("\n========================================");
        System.out.println("   Lab 24 Complete");
        System.out.println("========================================");
    }
}
