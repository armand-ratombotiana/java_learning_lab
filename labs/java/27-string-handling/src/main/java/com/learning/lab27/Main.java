package com.learning.lab27;

/**
 * Main entry point for Lab 27: String Handling.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 27: String Handling");
        System.out.println("========================================\n");

        StringPoolExample.showStringPool();
        System.out.println();
        TextBlockExample.showTextBlocks();
        System.out.println();
        StringBuilderVsBufferExample.showBuilderVsBuffer();

        System.out.println("\n========================================");
        System.out.println("   Lab 27 Complete");
        System.out.println("========================================");
    }
}
