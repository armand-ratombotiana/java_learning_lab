package com.java.records.sealed.patterns;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lab 05: Virtual Threads Deep Dive — virtual threads, structured
 * concurrency, scoped values, pinning avoidance.
 */
public class VirtualThreadsLab {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    // Simulate I/O call
    private String fetchData(String service, int delayMs) {
        try {
            Thread.sleep(Duration.ofMillis(delayMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return service + " result [req: " + REQUEST_ID.orElse("none") + "]";
    }

    // --- Structured concurrency with ShutdownOnFailure ---
    record AggregatedData(String user, String orders, String inventory) {}

    public AggregatedData fetchAll() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            StructuredTaskScope.Subtask<String> user      = scope.fork(() -> fetchData("UserService", 100));
            StructuredTaskScope.Subtask<String> orders    = scope.fork(() -> fetchData("OrderService", 150));
            StructuredTaskScope.Subtask<String> inventory = scope.fork(() -> fetchData("InventoryService", 80));

            scope.join();
            scope.throwIfFailed();

            return new AggregatedData(
                    user.get(),
                    orders.get(),
                    inventory.get()
            );
        }
    }

    // --- ShutdownOnSuccess — race multiple providers ---
    public String fastestProvider(String query) throws Exception {
        var providers = List.of(
                (Callable<String>) () -> fetchData("Provider-A", 200),
                (Callable<String>) () -> fetchData("Provider-B", 100),
                (Callable<String>) () -> fetchData("Provider-C", 300)
        );

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            for (var p : providers) scope.fork(p);
            scope.join();
            return scope.result();
        }
    }

    // --- Virtual thread executor ---
    public List<String> processBatch(List<String> items) {
        List<String> results = new CopyOnWriteArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = items.stream()
                    .map(item -> executor.submit(() -> {
                        var result = "Processed: " + item;
                        results.add(result);
                        return result;
                    }))
                    .toList();
            futures.forEach(f -> {
                try { f.get(); } catch (Exception ignored) {}
            });
        }
        return results;
    }

    // --- Scoped value propagation ---
    public String scopedValueDemo(String requestId) throws Exception {
        return ScopedValue.where(REQUEST_ID, requestId)
                .call(() -> fetchData("ScopedService", 50));
    }

    // --- Pinning demonstration (synchronized vs ReentrantLock) ---
    private final Lock lock = new ReentrantLock();
    private int counter = 0;

    public void safeIncrement() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    // --- Demo ---
    public static void main(String[] args) throws Exception {
        var lab = new VirtualThreadsLab();

        // Scoped value
        System.out.println(lab.scopedValueDemo("req-001"));

        // Structured concurrency
        var aggregated = lab.fetchAll();
        System.out.println("User: " + aggregated.user());
        System.out.println("Orders: " + aggregated.orders());
        System.out.println("Inventory: " + aggregated.inventory());

        // Fastest provider
        String fastest = lab.fastestProvider("test");
        System.out.println("Fastest: " + fastest);

        // Batch processing
        var results = lab.processBatch(List.of("A", "B", "C"));
        System.out.println("Batch: " + results);

        // Virtual thread identity
        Thread vt = Thread.ofVirtual().start(() ->
                System.out.println("Virtual: " + Thread.currentThread()));
        vt.join();
    }
}
