package com.learning.lab13;

/**
 * Main entry point for Lab 13: Streams.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 13: Streams");
        System.out.println("========================================\n");

        StreamCreationExample.showStreams();
        System.out.println();
        CollectorsExample.showCollectors();
        System.out.println();
        ParallelStreamExample.showParallelStream();

        System.out.println("\n========================================");
        System.out.println("   Lab 13 Complete");
        System.out.println("========================================");
    }
}
