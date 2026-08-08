package com.cloud.deep.lab03;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ServerlessDeepDive {

    public enum FunctionPhase { INIT, INVOKE, SHUTDOWN, SNAPSHOT, RESTORE }

    public record InvocationContext(String requestId, long deadlineMs, FunctionPhase phase, long timestamp) {}

    public record ColdStartMetric(double initDurationMs, double invokeDurationMs, boolean isSnapStart, Instant timestamp) {}

    public record FilterRule(String attribute, String pattern, FilterType type) {
        public enum FilterType { PREFIX, SUFFIX, EXACT, BEGINS_WITH }
    }

    public record ExtensionRegistration(String extensionId, String name, List<String> events) {}

    public static class FunctionRuntime {
        private final Random rand = new Random();
        private final boolean snapStart;
        private final List<ColdStartMetric> metrics = new CopyOnWriteArrayList<>();
        private volatile boolean initialized = false;
        private double initDuration;

        public FunctionRuntime(boolean snapStart) { this.snapStart = snapStart; }

        public synchronized void init() {
            if (initialized) return;
            long start = System.nanoTime();
            simulateWork(200 + rand.nextInt(1800));
            initDuration = (System.nanoTime() - start) / 1_000_000.0;
            initialized = true;
        }

        public InvocationContext invoke(String requestId) {
            FunctionPhase phase;
            if (snapStart && !initialized) {
                phase = FunctionPhase.RESTORE;
                long start = System.nanoTime();
                simulateWork(50 + rand.nextInt(200));
                double restoreDuration = (System.nanoTime() - start) / 1_000_000.0;
                metrics.add(new ColdStartMetric(restoreDuration, 0, true, Instant.now()));
                initialized = true;
            } else if (!initialized) {
                phase = FunctionPhase.INIT;
                init();
                phase = FunctionPhase.INVOKE;
            } else {
                phase = FunctionPhase.INVOKE;
            }
            long start = System.nanoTime();
            simulateWork(50 + rand.nextInt(500));
            double invokeDuration = (System.nanoTime() - start) / 1_000_000.0;
            if (phase == FunctionPhase.INIT || !snapStart) {
                metrics.add(new ColdStartMetric(initDuration, invokeDuration, snapStart, Instant.now()));
            }
            return new InvocationContext(requestId, System.currentTimeMillis() + 5000, phase, System.currentTimeMillis());
        }

        public ColdStartMetric getColdStartStats() {
            return metrics.isEmpty() ? null : metrics.get(0);
        }

        private void simulateWork(int ms) {
            try { Thread.sleep(ms / 1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            long end = System.nanoTime() + ms * 1_000_000L;
            while (System.nanoTime() < end) { }
        }
    }

    public static class EventFilter {
        private final List<FilterRule> rules = new CopyOnWriteArrayList<>();

        public EventFilter addRule(FilterRule rule) { rules.add(rule); return this; }

        public boolean matches(String attributeValue, String attributeName) {
            return rules.stream()
                .filter(r -> r.attribute().equals(attributeName))
                .anyMatch(r -> switch (r.type()) {
                    case EXACT -> attributeValue.equals(r.pattern());
                    case PREFIX -> attributeValue.startsWith(r.pattern());
                    case SUFFIX -> attributeValue.endsWith(r.pattern());
                    case BEGINS_WITH -> attributeValue.startsWith(r.pattern());
                });
        }
    }

    public static class LambdaExtension {
        private final String name;
        private final List<String> subscribedEvents = new CopyOnWriteArrayList<>();

        public LambdaExtension(String name, String... events) {
            this.name = name;
            Collections.addAll(subscribedEvents, events);
        }

        public ExtensionRegistration register() {
            String id = "ext-" + UUID.randomUUID().toString().substring(0, 8);
            System.out.println("Extension " + name + " registered with id " + id);
            return new ExtensionRegistration(id, name, List.copyOf(subscribedEvents));
        }

        public void onInvoke(InvocationContext ctx) {
            System.out.println("Extension " + name + " processing " + ctx);
        }

        public void shutdown() {
            System.out.println("Extension " + name + " shutting down");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Cold Start Comparison ===");
        var standard = new FunctionRuntime(false);
        standard.invoke("req-1");
        var stats = standard.getColdStartStats();
        System.out.printf("Standard cold start: init=%.2fms invoke=%.2fms%n", stats.initDurationMs(), stats.invokeDurationMs());

        var snap = new FunctionRuntime(true);
        snap.invoke("req-2");
        var snapStats = snap.getColdStartStats();
        System.out.printf("SnapStart cold start: restore=%.2fms invoke=%.2fms%n", snapStats.initDurationMs(), snapStats.invokeDurationMs());

        System.out.println("\n=== Event Filtering ===");
        var filter = new EventFilter()
            .addRule(new FilterRule("env", "prod", FilterRule.FilterType.EXACT))
            .addRule(new FilterRule("eventType", "ORDER_", FilterRule.FilterType.BEGINS_WITH));
        System.out.println("prod/ORDER_CREATED matches: " + filter.matches("ORDER_CREATED", "eventType"));
        System.out.println("dev/ORDER_CREATED matches: " + filter.matches("ORDER_CREATED", "eventType"));

        System.out.println("\n=== Lambda Extension ===");
        var ext = new LambdaExtension("telemetry-collector", "INVOKE", "SHUTDOWN");
        ext.register();
        ext.onInvoke(new InvocationContext("req-3", 5000, FunctionPhase.INVOKE, System.currentTimeMillis()));
        ext.shutdown();
    }
}
