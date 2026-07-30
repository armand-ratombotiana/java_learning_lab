package com.devops.deep.lab07;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SreLab {
    public static void main(String[] args) {
        var sli = new ServiceLevelIndicator("api-gateway");
        sli.recordLatency(150, true);
        sli.recordLatency(50, true);
        sli.recordLatency(300, true);
        sli.recordLatency(120, false);
        sli.recordLatency(80, true);

        var slo = new ServiceLevelObjective("api-gateway", 99.9, Duration.ofDays(30));
        var errorBudget = new ErrorBudget(slo);
        errorBudget.consume(sli.failedRequests());
        System.out.println("Error Budget: " + errorBudget.remaining());

        var toilTracker = new ToilTracker();
        toilTracker.logToil("Manual DB backup", 2.5);
        toilTracker.logToil("Restart crashed pods", 1.0);
        toilTracker.logToil("Respond to pager", 0.5);
        System.out.println("Total toil: " + toilTracker.totalToilHours() + "h this week");

        var postmortem = new Postmortem("INC-2024-07-30", "API Gateway Latency Spike");
        postmortem.setTimeline(Instant.now().minusSeconds(3600), Instant.now());
        postmortem.setRootCause("Connection pool exhaustion due to misconfigured max_connections");
        postmortem.addActionItem("Increase max_connections to 200", "SRE-Team", "P0");
        postmortem.addActionItem("Add connection pool monitoring alert", "SRE-Team", "P1");
        System.out.println(postmortem.summary());
    }
}

class ServiceLevelIndicator {
    private final String name;
    private final List<Long> latencies = new CopyOnWriteArrayList<>();
    private int successes = 0;
    private int failures = 0;

    ServiceLevelIndicator(String name) { this.name = name; }

    void recordLatency(long ms, boolean success) {
        latencies.add(ms);
        if (success) successes++; else failures++;
    }

    double p99Latency() {
        var sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        int idx = (int) Math.ceil(0.99 * sorted.size()) - 1;
        return sorted.get(Math.max(0, idx));
    }

    double availability() {
        int total = successes + failures;
        return total == 0 ? 100.0 : (successes * 100.0 / total);
    }

    int failedRequests() { return failures; }
    int totalRequests() { return successes + failures; }
}

record ServiceLevelObjective(String name, double targetPercent, Duration period) {}

class ErrorBudget {
    private final ServiceLevelObjective slo;
    private double consumedErrors = 0;
    private double totalAllowed;

    ErrorBudget(ServiceLevelObjective slo) {
        this.slo = slo;
        this.totalAllowed = 100.0 - slo.targetPercent();
    }

    void consume(int failureCount) {
        consumedErrors += failureCount;
    }

    double remaining() {
        var remaining = totalAllowed - consumedErrors;
        return Math.max(0, remaining);
    }
}

class ToilTracker {
    private final List<Double> toilHours = new ArrayList<>();

    void logToil(String task, double hours) { toilHours.add(hours); }
    double totalToilHours() { return toilHours.stream().mapToDouble(Double::doubleValue).sum(); }
}

class Postmortem {
    private final String incidentId;
    private final String title;
    private Instant startedAt;
    private Instant resolvedAt;
    private String rootCause;
    private final List<String> actionItems = new ArrayList<>();

    Postmortem(String incidentId, String title) {
        this.incidentId = incidentId;
        this.title = title;
    }

    void setTimeline(Instant startedAt, Instant resolvedAt) {
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
    }

    void setRootCause(String rootCause) { this.rootCause = rootCause; }
    void addActionItem(String item, String owner, String priority) { actionItems.add("[" + priority + "] " + item + " (" + owner + ")"); }

    String summary() {
        var sb = new StringBuilder();
        sb.append("Postmortem: ").append(incidentId).append(" - ").append(title).append("\n");
        sb.append("  Duration: ").append(Duration.between(startedAt, resolvedAt).toMinutes()).append("m\n");
        sb.append("  Root cause: ").append(rootCause).append("\n");
        sb.append("  Action items:\n");
        actionItems.forEach(a -> sb.append("    - ").append(a).append("\n"));
        return sb.toString();
    }
}
