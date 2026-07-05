package com.learning.lab16;

/**
 * Main entry point for Lab 16: Concurrency.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Lab 16: Concurrency");
        System.out.println("========================================\n");

        ThreadCreationExample.showThreadCreation();
        System.out.println();
        SyncExample.showSynchronization();
        System.out.println();
        ExecutorServiceExample.showExecutorService();
        System.out.println();
        CompletableFutureExample.showCompletableFuture();

        System.out.println("\n========================================");
        System.out.println("   Lab 16 Complete");
        System.out.println("========================================");
    }
}
