package com.databases.dbtesting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MigrationTester {
    private final String migrationName;
    private final List<MigrationStep> steps = new ArrayList<>();
    private final List<ValidationRule> validations = new ArrayList<>();
    private final Map<String, AtomicLong> metrics = new ConcurrentHashMap<>();

    public record MigrationStep(int order, String description, Runnable action) {}
    public record ValidationRule(String description, boolean required, Runnable check) {}

    public MigrationTester(String name) { this.migrationName = name; }

    public MigrationTester addStep(int order, String desc, Runnable action) {
        steps.add(new MigrationStep(order, desc, action));
        return this;
    }

    public MigrationTester addValidation(String desc, boolean required, Runnable check) {
        validations.add(new ValidationRule(desc, required, check));
        return this;
    }

    public MigrationResult execute() {
        var sb = new StringBuilder();
        sb.append("Migration: ").append(migrationName).append("\n");
        boolean success = true;

        steps.sort(Comparator.comparingInt(MigrationStep::order));
        for (var step : steps) {
            try {
                long start = System.nanoTime();
                step.action().run();
                long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                sb.append("  [OK] Step ").append(step.order()).append(": ").append(step.description())
                    .append(" (").append(ms).append("ms)\n");
                recordMetric("step_" + step.order() + "_ms", ms);
            } catch (Exception e) {
                sb.append("  [FAIL] Step ").append(step.order()).append(": ").append(step.description())
                    .append(" - ").append(e.getMessage()).append("\n");
                success = false;
                break;
            }
        }

        if (success) {
            for (var v : validations) {
                try {
                    v.check().run();
                    sb.append("  [OK] Validation: ").append(v.description()).append("\n");
                } catch (Exception e) {
                    sb.append("  [").append(v.required ? "FAIL" : "WARN").append("] Validation: ")
                        .append(v.description()).append(" - ").append(e.getMessage()).append("\n");
                    if (v.required()) success = false;
                }
            }
        }

        sb.append("Result: ").append(success ? "PASS" : "FAIL").append("\n");
        recordMetric("duration_ms", parseDuration(sb.toString()));
        return new MigrationResult(success, sb.toString());
    }

    public record MigrationResult(boolean success, String log) {}

    private void recordMetric(String name, long value) {
        metrics.computeIfAbsent(name, k -> new AtomicLong()).addAndGet(value);
    }

    private long parseDuration(String log) {
        var m = java.util.regex.Pattern.compile("(\\d+)ms").matcher(log);
        long total = 0;
        while (m.find()) total += Long.parseLong(m.group(1));
        return total;
    }

    public Map<String, Long> getMetrics() {
        Map<String, Long> result = new HashMap<>();
        metrics.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }
}
