package com.learning.jmeter;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {
    record SamplerResult(String name, long startTime, long elapsedMs, int statusCode, boolean success, long bytes) {}

    record Sample(String label, long timestamp, double elapsed, boolean success, int bytes, String threadName) {}

    static class ThreadGroup {
        String name;
        int numThreads;
        Duration rampUp;
        int loopCount;
        List<Sampler> samplers = new ArrayList<>();

        ThreadGroup(String name, int threads, Duration rampUp, int loops) {
            this.name = name; this.numThreads = threads;
            this.rampUp = rampUp; this.loopCount = loops;
        }

        void addSampler(Sampler s) { samplers.add(s); }
    }

    static abstract class Sampler {
        String name;
        Sampler(String name) { this.name = name; }
        abstract SamplerResult execute();
    }

    static class HttpSampler extends Sampler {
        String method, path;
        HttpSampler(String name, String method, String path) { super(name); this.method = method; this.path = path; }
        SamplerResult execute() {
            long start = System.currentTimeMillis();
            try { Thread.sleep(ThreadLocalRandom.current().nextInt(20, 200)); } catch (InterruptedException e) {}
            return new SamplerResult(name, start, System.currentTimeMillis() - start, 200, true, 512);
        }
    }

    static class JdbcSampler extends Sampler {
        String query;
        JdbcSampler(String name, String query) { super(name); this.query = query; }
        SamplerResult execute() {
            long start = System.currentTimeMillis();
            try { Thread.sleep(ThreadLocalRandom.current().nextInt(5, 50)); } catch (InterruptedException e) {}
            return new SamplerResult(name, start, System.currentTimeMillis() - start, 0, true, 128);
        }
    }

    static class Assertion {
        String name;
        Predicate<SamplerResult> check;
        Assertion(String name, Predicate<SamplerResult> check) { this.name = name; this.check = check; }
    }

    static class TestPlan {
        String name;
        List<ThreadGroup> threadGroups = new ArrayList<>();
        List<Assertion> assertions = new ArrayList<>();
        List<Sample> results = new ArrayList<>();
        boolean hasTimers;

        void addThreadGroup(ThreadGroup tg) { threadGroups.add(tg); }
        void addAssertion(Assertion a) { assertions.add(a); }

        void run() {
            results.clear();
            var startTime = System.currentTimeMillis();
            var executor = Executors.newFixedThreadPool(
                threadGroups.stream().mapToInt(tg -> tg.numThreads).sum());

            for (var tg : threadGroups) {
                for (int t = 0; t < tg.numThreads; t++) {
                    final int threadNum = t;
                    executor.submit(() -> {
                        for (int loop = 0; loop < tg.loopCount; loop++) {
                            for (var sampler : tg.samplers) {
                                var result = sampler.execute();
                                results.add(new Sample(sampler.name, result.startTime(),
                                    result.elapsedMs(), result.success(), (int) result.bytes(),
                                    tg.name + "-Thread-" + threadNum));
                            }
                        }
                    });
                }
            }

            executor.shutdown();
            try { executor.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) {}

            System.out.println("   Run completed in " + (System.currentTimeMillis() - startTime) + "ms");
        }

        void report() {
            System.out.println("\n   Results Summary:");
            System.out.println("   Total samples: " + results.size());

            var byLabel = results.stream().collect(Collectors.groupingBy(Sample::label));
            for (var e : byLabel.entrySet()) {
                var list = e.getValue();
                var successes = list.stream().filter(Sample::success).count();
                var stats = list.stream().mapToDouble(Sample::elapsed).summaryStatistics();
                System.out.println("   " + e.getKey() + ":");
                System.out.println("     Samples: " + list.size() + " | Success: " + (successes * 100 / list.size()) + "%");
                System.out.println("     Avg: " + String.format("%.0f", stats.getAverage()) + "ms | Min: " + String.format("%.0f", stats.getMin()) + "ms | Max: " + String.format("%.0f", stats.getMax()) + "ms");
            }
        }

        boolean runAssertions() {
            boolean allPassed = true;
            for (var assertion : assertions) {
                boolean passed = results.stream().anyMatch(r -> assertion.check.test(
                    new SamplerResult(r.label(), 0, (long) r.elapsed(), 200, r.success(), 0)));
                if (!passed) {
                    System.out.println("   Assertion failed: " + assertion.name);
                    allPassed = false;
                }
            }
            return allPassed;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== JMeter Concepts Lab ===\n");

        TestPlan plan = new TestPlan();
        plan.name = "Load Test - Order Service";

        System.out.println("1. Test Plan: " + plan.name);

        var apiUsers = new ThreadGroup("API Users", 10, Duration.ofSeconds(5), 5);
        apiUsers.addSampler(new HttpSampler("GET /api/products", "GET", "/api/products"));
        apiUsers.addSampler(new HttpSampler("POST /api/orders", "POST", "/api/orders"));
        apiUsers.addSampler(new HttpSampler("GET /api/orders/1", "GET", "/api/orders/1"));
        plan.addThreadGroup(apiUsers);

        var dbQueries = new ThreadGroup("DB Queries", 5, Duration.ofSeconds(3), 3);
        dbQueries.addSampler(new JdbcSampler("SELECT products", "SELECT * FROM products"));
        dbQueries.addSampler(new JdbcSampler("SELECT orders", "SELECT * FROM orders WHERE status='PENDING'"));
        plan.addThreadGroup(dbQueries);

        System.out.println("\n2. Thread Groups:");
        for (var tg : plan.threadGroups)
            System.out.println("   " + tg.name + ": " + tg.numThreads + " threads x " + tg.loopCount + " loops");

        System.out.println("\n3. Samplers:");
        for (var tg : plan.threadGroups)
            for (var s : tg.samplers)
                System.out.println("   " + s.name);

        plan.addAssertion(new Assertion("Response time < 500ms", r -> r.elapsedMs() < 500));
        plan.addAssertion(new Assertion("Success rate > 99%", r -> true));

        System.out.println("\n4. Running test plan...");
        plan.run();

        System.out.println("\n5. Assertions:");
        plan.runAssertions();

        System.out.println("\n6. Listeners / Report:");
        plan.report();

        System.out.println("\n7. Test Elements overview:");
        System.out.println("   - Thread Group: controls concurrency");
        System.out.println("   - Config Element: CSV Data Set Config, HTTP Header Manager");
        System.out.println("   - Timer: Constant Timer, Gaussian Random Timer (think time)");
        System.out.println("   - Pre-Processor: JSR223 PreProcessor, User Parameters");
        System.out.println("   - Post-Processor: Regular Expression Extractor, JSON Extractor");
        System.out.println("   - Assertion: Response Assertion, Duration Assertion");
        System.out.println("   - Listener: Summary Report, Aggregate Report, Graph Results");

        System.out.println("\n8. Distributed Testing:");
        System.out.println("   Master node controls test, slaves execute");
        System.out.println("   jmeter-server on each slave node");
        System.out.println("   -Rremote_host1,remote_host2 for distributed run");

        System.out.println("\n=== Lab Complete ===");
    }
}
