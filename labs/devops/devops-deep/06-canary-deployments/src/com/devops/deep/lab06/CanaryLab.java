package com.devops.deep.lab06;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CanaryLab {
    public static void main(String[] args) {
        var rollout = new ArgoRollout("web-app", "nginx:1.25", "nginx:1.26");

        rollout.addStep(new CanaryStep(10, "pause 2m"));
        rollout.addStep(new CanaryStep(25, "pause 2m"));
        rollout.addStep(new CanaryStep(50, "pause 1m"));
        rollout.addStep(new CanaryStep(75, "pause 1m"));
        rollout.addStep(new CanaryStep(100, "done"));

        var prometheus = new MetricsService();
        prometheus.setErrorRate("nginx:1.25", 0.5);
        prometheus.setErrorRate("nginx:1.26", 0.5);

        rollout.execute(prometheus);

        System.out.println("\nFinal: " + rollout.status());

        // Simulate bad canary
        var badRollout = new ArgoRollout("web-app", "nginx:1.25", "nginx:1.26-bad");
        badRollout.addStep(new CanaryStep(10, "pause 1m"));
        prometheus.setErrorRate("nginx:1.26-bad", 15.0);
        badRollout.execute(prometheus);
        System.out.println("Bad rollout: " + badRollout.status());
    }
}

record CanaryStep(int weight, String action) {}

class ArgoRollout {
    private final String name;
    private final String stableVersion;
    private final String canaryVersion;
    private final List<CanaryStep> steps = new ArrayList<>();
    private int currentStep = 0;
    private String state = "Pending";
    private boolean rolledBack = false;

    ArgoRollout(String name, String stableVersion, String canaryVersion) {
        this.name = name;
        this.stableVersion = stableVersion;
        this.canaryVersion = canaryVersion;
    }

    void addStep(CanaryStep step) { steps.add(step); }

    void execute(MetricsService metrics) {
        state = "Progressing";
        System.out.println("Starting canary: " + name + " (" + stableVersion + " -> " + canaryVersion + ")");
        for (int i = 0; i < steps.size(); i++) {
            var step = steps.get(i);
            currentStep = i;
            System.out.println("  Step " + (i + 1) + ": " + step.weight() + "% canary traffic");
            var stableErrorRate = metrics.getErrorRate(stableVersion);
            var canaryErrorRate = metrics.getErrorRate(canaryVersion);
            System.out.println("    Stable error rate: " + stableErrorRate + "% | Canary error rate: " + canaryErrorRate + "%");
            if (canaryErrorRate > 10.0) {
                System.out.println("    ROLLBACK triggered! Canary error rate exceeds threshold.");
                rolledBack = true;
                state = "RolledBack";
                return;
            }
        }
        state = "Promoted";
        System.out.println("  Canary promoted to 100%");
    }

    String status() {
        return name + ": " + state + (rolledBack ? " (rolled back to " + stableVersion + ")" : " -> " + canaryVersion);
    }
}

class MetricsService {
    private final Map<String, Double> errorRates = new ConcurrentHashMap<>();

    void setErrorRate(String version, double rate) { errorRates.put(version, rate); }
    double getErrorRate(String version) { return errorRates.getOrDefault(version, 0.0); }
}
