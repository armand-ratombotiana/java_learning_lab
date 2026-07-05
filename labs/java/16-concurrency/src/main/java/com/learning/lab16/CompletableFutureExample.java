package com.learning.lab16;

import java.util.concurrent.*;

/**
 * Demonstrates CompletableFuture for async computation and composition.
 */
public class CompletableFutureExample {

    public static void showCompletableFuture() throws InterruptedException, ExecutionException {
        System.out.println("=== CompletableFuture ===");

        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "Hello";
        });

        CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> {
            sleep(150);
            return "World";
        });

        CompletableFuture<String> combined = hello.thenCombine(world, (h, w) -> h + " " + w + "!");
        System.out.println("Combined: " + combined.get());

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 42)
            .thenApply(n -> n * 2)
            .thenApply(n -> n + 1);

        System.out.println("Pipeline result: " + future.get());

        CompletableFuture<Integer> withError = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Something went wrong");
            return 42;
        }).exceptionally(ex -> {
            System.out.println("  Handled error: " + ex.getMessage());
            return -1;
        });

        System.out.println("Error handled result: " + withError.get());
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
