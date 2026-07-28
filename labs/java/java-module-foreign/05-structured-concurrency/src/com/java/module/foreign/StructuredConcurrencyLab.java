package com.java.module.foreign;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Lab 05: Structured Concurrency — StructuredTaskScope,
 * ShutdownOnSuccess, ShutdownOnFailure, timeouts, error handling.
 */
public class StructuredConcurrencyLab {

    private final Random rng = new Random();

    // Simulate remote service call
    private String callService(String name, int delayMs, boolean fail) {
        try {
            Thread.sleep(Duration.ofMillis(delayMs));
        } catch (InterruptedException e) {
            System.out.println(name + " interrupted");
            Thread.currentThread().interrupt();
            return null;
        }
        if (fail) throw new RuntimeException(name + " failed");
        return name + "-result";
    }

    // --- ShutdownOnFailure: all must succeed ---
    record AggregateResult(String a, String b, String c) {}

    public AggregateResult fetchAll() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> fa = scope.fork(() -> callService("ServiceA", 100, false));
            Future<String> fb = scope.fork(() -> callService("ServiceB", 150, false));
            Future<String> fc = scope.fork(() -> callService("ServiceC", 80, false));

            scope.join();
            scope.throwIfFailed();

            return new AggregateResult(fa.resultNow(), fb.resultNow(), fc.resultNow());
        }
    }

    // --- Failure propagation ---
    public AggregateResult fetchWithFailure() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> fa = scope.fork(() -> callService("ServiceA", 50, false));
            Future<String> fb = scope.fork(() -> callService("ServiceB", 100, true)); // fails
            Future<String> fc = scope.fork(() -> callService("ServiceC", 200, false));

            scope.join();
            scope.throwIfFailed();
            return new AggregateResult(fa.resultNow(), fb.resultNow(), fc.resultNow());
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
            return null;
        }
    }

    // --- ShutdownOnSuccess: first success wins ---
    public String fastestProvider() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> callService("Slow", 300, false));
            scope.fork(() -> callService("Medium", 200, false));
            scope.fork(() -> callService("Fast", 100, false));

            scope.join();
            return scope.result();
        }
    }

    // --- Timeout with joinUntil ---
    public String fetchWithTimeout() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> fa = scope.fork(() -> callService("Slowpoke", 5000, false));

            scope.joinUntil(Instant.now().plusMillis(200));

            if (!fa.isDone()) {
                scope.shutdown();
                throw new TimeoutException("Service timed out");
            }
            return fa.resultNow();
        }
    }

    // --- Demo ---
    public static void main(String[] args) throws Exception {
        var lab = new StructuredConcurrencyLab();

        // All succeed
        var result = lab.fetchAll();
        System.out.println("Aggregate: " + result);

        // Failure propagation
        lab.fetchWithFailure();

        // Fastest provider
        String fastest = lab.fastestProvider();
        System.out.println("Fastest: " + fastest);

        // Timeout
        try {
            lab.fetchWithTimeout();
        } catch (TimeoutException e) {
            System.out.println("Caught timeout: " + e.getMessage());
        }
    }
}
