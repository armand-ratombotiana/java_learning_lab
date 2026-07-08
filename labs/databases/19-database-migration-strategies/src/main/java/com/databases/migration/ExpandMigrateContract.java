package com.databases.migration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExpandMigrateContract {
    public enum Phase { EXPAND, MIGRATE, CONTRACT }
    public enum Status { PENDING, RUNNING, COMPLETED, FAILED }

    private Phase currentPhase = Phase.EXPAND;
    private final Map<String, MigrationStep> steps = new LinkedHashMap<>();
    private final Map<String, AtomicLong> metrics = new ConcurrentHashMap<>();

    public record MigrationStep(String name, String description, Runnable action) {}

    public ExpandMigrateContract addStep(String name, String desc, Runnable action) {
        steps.put(name, new MigrationStep(name, desc, action));
        return this;
    }

    public Status executePhase(Phase phase) {
        currentPhase = phase;
        System.out.println("Starting phase: " + phase);
        for (var step : steps.values()) {
            try {
                long start = System.nanoTime();
                step.action().run();
                long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                recordMetric("step_" + step.name() + "_ms", ms);
                System.out.println("  [OK] " + step.name() + ": " + step.description() + " (" + ms + "ms)");
            } catch (Exception e) {
                System.out.println("  [FAIL] " + step.name() + ": " + e.getMessage());
                return Status.FAILED;
            }
        }
        return Status.COMPLETED;
    }

    public Phase getCurrentPhase() { return currentPhase; }

    public void transitionTo(Phase phase) {
        System.out.println("Transitioning from " + currentPhase + " to " + phase);
        currentPhase = phase;
    }

    private void recordMetric(String name, long value) {
        metrics.computeIfAbsent(name, k -> new AtomicLong()).addAndGet(value);
    }

    public Map<String, Long> getMetrics() {
        Map<String, Long> result = new HashMap<>();
        metrics.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    public static ExpandMigrateContract createDefaultMigration() {
        var emc = new ExpandMigrateContract();
        emc.addStep("add_new_schema", "Add new schema alongside existing", () -> {});
        emc.addStep("dual_write", "Enable dual writes to both schemas", () -> {});
        emc.addStep("backfill", "Backfill historical data to new schema", () -> {});
        emc.addStep("verify", "Verify data consistency between schemas", () -> {});
        emc.addStep("read_cutover", "Cut over reads to new schema", () -> {});
        emc.addStep("remove_old", "Remove old schema", () -> {});
        return emc;
    }
}
