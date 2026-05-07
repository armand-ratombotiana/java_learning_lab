package com.learning.kotlin;

import java.util.concurrent.*;
import java.util.stream.*;

public class KotlinCoroutinesLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Kotlin vs Java Async Patterns Lab ===\n");

        System.out.println("1. Java Virtual Threads (Kotlin coroutines equivalent):");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> task1 = executor.submit(() -> fetchDataJava("API-1", 200));
            Future<String> task2 = executor.submit(() -> fetchDataJava("API-2", 150));
            System.out.println("   " + task1.get() + ", " + task2.get());
        }

        System.out.println("\n2. Java CompletableFuture (Kotlin async/await equivalent):");
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "Data from API-1";
        });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            sleep(150);
            return "Data from API-2";
        });
        CompletableFuture.allOf(cf1, cf2).join();
        System.out.println("   " + cf1.get() + ", " + cf2.get());

        System.out.println("\n3. Java Parallel Stream (Kotlin Flow equivalent):");
        IntStream.rangeClosed(1, 5)
                .map(i -> i * i)
                .filter(i -> i % 2 == 0)
                .forEach(i -> System.out.println("   Received: " + i));

        System.out.println("\n4. Concept Comparison:");
        System.out.println("   Kotlin launch {}      -> Java Thread.startVirtualThread()");
        System.out.println("   Kotlin async/await    -> Java CompletableFuture");
        System.out.println("   Kotlin Flow           -> Java Reactive Streams / Stream API");
        System.out.println("   Kotlin Channel        -> Java BlockingQueue / Exchanger");
        System.out.println("   Kotlin delay()        -> Java Thread.sleep()");
        System.out.println("   Kotlin Dispatchers    -> Java ExecutorService");
        System.out.println("   Kotlin supervisorScope -> Java try-catch with scope");
        System.out.println("   Kotlin withTimeout    -> Java CompletableFuture.orTimeout()");

        System.out.println("\n=== Java Async Patterns Lab Complete ===");
    }

    static String fetchDataJava(String name, int delayMs) {
        sleep(delayMs);
        return "Data from " + name;
    }

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}