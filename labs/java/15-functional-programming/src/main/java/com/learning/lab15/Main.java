package com.learning.lab15;

/**
 * Main entry point for Lab 15: Functional Programming.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 15: Functional Programming");
        System.out.println("========================================\n");

        OptionalChainingExample.showOptionalChaining();
        System.out.println();
        StreamPipelineExample.showStreamPipelines();
        System.out.println();
        PureFunctionExample.showPureFunctions();

        System.out.println("\n========================================");
        System.out.println("   Lab 15 Complete");
        System.out.println("========================================");
    }
}
