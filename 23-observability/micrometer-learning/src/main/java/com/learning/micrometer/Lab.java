package com.learning.micrometer;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class Lab {
    // Meter types
    static class Counter {
        final String name;
        final AtomicLong count = new AtomicLong(0);
        Counter(String name) { this.name = name; }
        void increment() { count.incrementAndGet(); }
        void increment(double n) { count.addAndGet((long)(n * 1000)); }
        double count() { return count.get() / 1000.0; }
    }

    static class Gauge {
        final String name;
        final Supplier<Double> supplier;
        Gauge(String name, Supplier<Double> supplier) { this.name = name; this.supplier = supplier; }
        double value() { return supplier.get(); }
    }

    static class Timer {
        final String name;
        final AtomicLong totalTime = new AtomicLong(0);
        final AtomicLong count = new AtomicLong(0);
        Timer(String name) { this.name = name; }
        Sample start() { return new Sample(System.nanoTime()); }
        void stop(Sample sample) {
            var elapsed = System.nanoTime() - sample.nanoTime;
            totalTime.addAndGet(elapsed);
            count.addAndGet(1);
        }
        double mean() { return count.get() == 0 ? 0 : totalTime.get() / (double) count.get(); }
        long total() { return totalTime.get(); }
        long count() { return count.get(); }
        record Sample(long nanoTime) {}
    }

    static class DistributionSummary {
        final String name;
        final List<Double> samples = new CopyOnWriteArrayList<>();
        DistributionSummary(String name) { this.name = name; }
        void record(double amount) { samples.add(amount); }
        double max() { return samples.stream().mapToDouble(d -> d).max().orElse(0); }
        double mean() { return samples.stream().mapToDouble(d -> d).average().orElse(0); }
        long count() { return samples.size(); }
    }

    static class MeterRegistry {
        final Map<String, Counter> counters = new ConcurrentHashMap<>();
        final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
        final Map<String, Timer> timers = new ConcurrentHashMap<>();
        final Map<String, DistributionSummary> summaries = new ConcurrentHashMap<>();

        Counter counter(String name) {
            return counters.computeIfAbsent(name, Counter::new);
        }

        Gauge gauge(String name, Supplier<Double> supplier) {
            return gauges.computeIfAbsent(name, n -> new Gauge(n, supplier));
        }

        Timer timer(String name) {
            return timers.computeIfAbsent(name, Timer::new);
        }

        DistributionSummary summary(String name) {
            return summaries.computeIfAbsent(name, DistributionSummary::new);
        }

        void report() {
            System.out.println("\n=== Metrics Report ===");
            counters.forEach((n, c) -> System.out.println("  Counter[" + n + "] = " + c.count()));
            gauges.forEach((n, g) -> System.out.println("  Gauge[" + n + "] = " + g.value()));
            timers.forEach((n, t) -> System.out.println("  Timer[" + n + "] count=" + t.count() + " mean=" + t.mean() / 1_000_000 + "ms"));
            summaries.forEach((n, s) -> System.out.println("  Summary[" + n + "] count=" + s.count() + " max=" + s.max() + " mean=" + s.mean()));
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Micrometer: Metrics Collection ===");
        var registry = new MeterRegistry();

        // Counters
        var httpRequests = registry.counter("http.requests");
        var errors = registry.counter("http.errors");
        for (int i = 0; i < 100; i++) httpRequests.increment();
        for (int i = 0; i < 3; i++) errors.increment();

        // Gauges
        var queue = new ArrayBlockingQueue<String>(10);
        for (int i = 0; i < 5; i++) queue.offer("msg-" + i);
        registry.gauge("queue.size", () -> (double) queue.size());
        registry.gauge("memory.used", () -> (double) Runtime.getRuntime().totalMemory() / 1024 / 1024);

        // Timers
        var requestTimer = registry.timer("http.request.duration");
        var executor = Executors.newFixedThreadPool(4);
        var futures = new ArrayList<Future<?>>();
        for (int i = 0; i < 8; i++) {
            futures.add(executor.submit(() -> {
                var sample = requestTimer.start();
                try { Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200)); } catch (InterruptedException e) {}
                requestTimer.stop(sample);
            }));
        }
        for (var f : futures) f.get();

        // Distribution Summary
        var responseSizes = registry.summary("http.response.size");
        var rng = ThreadLocalRandom.current();
        for (int i = 0; i < 20; i++) responseSizes.record(100 + rng.nextDouble(900));

        registry.report();

        // Tagged metrics
        System.out.println("\n--- Tagged Metrics ---");
        var statusCounters = Map.of(
            "status.2xx", registry.counter("http.requests"),
            "status.4xx", registry.counter("http.requests"),
            "status.5xx", registry.counter("http.requests")
        );
        registry.counter("http.requests").increment(95);
        System.out.println("  Total requests: " + registry.counter("http.requests").count());

        executor.shutdown();
    }
}
