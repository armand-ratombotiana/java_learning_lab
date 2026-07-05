package com.learning.lab01;

/**
 * Main entry point for Lab 01: Java Syntax.
 * Runs all example demonstrations.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 01: Java Syntax");
        System.out.println("========================================\n");

        VariablesExample.showVariables();
        System.out.println();
        OperatorsExample.showOperators();
        System.out.println();
        ControlFlowExample.showControlFlow();
        System.out.println();
        BasicIOExample.showBasicIO();

        System.out.println("\n========================================");
        System.out.println("   Lab 01 Complete");
        System.out.println("========================================");
    }
}
