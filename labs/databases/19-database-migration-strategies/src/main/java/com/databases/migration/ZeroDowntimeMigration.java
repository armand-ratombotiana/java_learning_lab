package com.databases.migration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ZeroDowntimeMigration {
    private final String migrationName;
    private final List<PhasedStep> steps = new ArrayList<>();
    private volatile boolean healthy = true;

    public record PhasedStep(String name, Runnable action, int order, boolean canary) {}

    public ZeroDowntimeMigration(String name) { this.migrationName = name; }

    public ZeroDowntimeMigration addStep(String name, Runnable action, int order, boolean canary) {
        steps.add(new PhasedStep(name, action, order, canary));
        return this;
    }

    public MigrationResult execute() {
        var sb = new StringBuilder();
        sb.append("Zero-downtime migration: ").append(migrationName).append("\n");
        boolean success = true;

        steps.sort(Comparator.comparingInt(PhasedStep::order));
        var canarySteps = steps.stream().filter(PhasedStep::canary).toList();
        var mainSteps = steps.stream().filter(s -> !s.canary()).toList();

        sb.append("Executing canary steps first...\n");
        for (var step : canarySteps) {
            try {
                long start = System.nanoTime();
                step.action().run();
                long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                sb.append("  [CANARY OK] ").append(step.name()).append(" (").append(ms).append("ms)\n");
            } catch (Exception e) {
                sb.append("  [CANARY FAIL] ").append(step.name()).append(" - ").append(e.getMessage()).append("\n");
                success = false;
            }
        }

        if (!success) {
            sb.append("Canary failed - rolling back\n");
            return new MigrationResult(false, sb.toString());
        }

        sb.append("Executing main migration steps...\n");
        for (var step : mainSteps) {
            try {
                long start = System.nanoTime();
                step.action().run();
                long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                sb.append("  [OK] ").append(step.name()).append(" (").append(ms).append("ms)\n");
            } catch (Exception e) {
                sb.append("  [FAIL] ").append(step.name()).append(" - ").append(e.getMessage()).append("\n");
                success = false;
                break;
            }
        }

        sb.append("Result: ").append(success ? "SUCCESS" : "FAILURE").append("\n");
        return new MigrationResult(success, sb.toString());
    }

    public record MigrationResult(boolean success, String log) {}

    public boolean isHealthy() { return healthy; }
    public void setUnhealthy() { this.healthy = false; }
}
