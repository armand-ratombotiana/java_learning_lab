package com.javaacademy.lab41.threading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Explores CompletableFuture internals: thenApply, thenCompose, exceptionally,
 * allOf, anyOf, and explicit completion stage management.
 */
public class CompletableFutureDeepDive {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CompletableFuture Deep Dive ===\n");

        // thenApply: synchronous transformation
        CompletableFuture.supplyAsync(() -> 42)
            .thenApply(n -> n * 2)
            .thenApply(n -> "Result: " + n)
            .thenAccept(System.out::println);

        // thenCompose: flatMap for futures (avoids nested futures)
        CompletableFuture.supplyAsync(() -> "Hello")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"))
            .thenAccept(System.out::println);

        // exceptionally: recover from errors
        CompletableFuture.supplyAsync(() -> { throw new RuntimeException("Oops"); })
            .exceptionally(ex -> "Recovered: " + ex.getMessage())
            .thenAccept(System.out::println);

        // allOf: wait for multiple futures
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Task1");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Task2");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Task3");
        CompletableFuture.allOf(f1, f2, f3).thenAccept(v ->
            System.out.println("All completed: " + f1.join() + ", " + f2.join() + ", " + f3.join())
        ).join();

        // anyOf: first completed future wins
        CompletableFuture<Object> any = CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> { sleep(100); return "Slow"; }),
            CompletableFuture.supplyAsync(() -> { sleep(50); return "Fast"; })
        );
        System.out.println("AnyOf winner: " + any.get());

        // Explicit stage completion
        CompletableFuture<Integer> stage = new CompletableFuture<>();
        stage.thenAccept(n -> System.out.println("Explicit completion: " + n));
        stage.complete(99);
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
