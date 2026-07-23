package com.prod.solutions.deployment;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates a canary deployment strategy where a new version is
 * gradually rolled out to a small percentage of traffic first.
 *
 * Stages: 1% → 5% → 20% → 100%
 * Each stage monitors error rate before proceeding.
 */
public class CanaryDeployment {

    static class DeploymentStage {
        final int percentage;
        final int durationMinutes;
        final double maxErrorRate;

        DeploymentStage(int percentage, int durationMinutes, double maxErrorRate) {
            this.percentage = percentage;
            this.durationMinutes = durationMinutes;
            this.maxErrorRate = maxErrorRate;
        }
    }

    static class Metrics {
        final AtomicInteger requests = new AtomicInteger();
        final AtomicInteger errors = new AtomicInteger();

        void recordRequest(boolean isError) {
            requests.incrementAndGet();
            if (isError) errors.incrementAndGet();
        }

        double getErrorRate() {
            return requests.get() > 0 ? (double) errors.get() / requests.get() : 0;
        }

        void reset() {
            requests.set(0);
            errors.set(0);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Canary Deployment Simulator ===");

        DeploymentStage[] stages = {
            new DeploymentStage(1, 10, 0.01),   // 1% for 10 min, max 1% errors
            new DeploymentStage(5, 20, 0.01),   // 5% for 20 min, max 1% errors
            new DeploymentStage(20, 30, 0.005), // 20% for 30 min, max 0.5% errors
            new DeploymentStage(100, 60, 0.005) // 100% for 60 min, max 0.5% errors
        };

        boolean newVersionHasBug = true; // Toggle to test rollback

        Metrics metrics = new Metrics();

        for (int stageIdx = 0; stageIdx < stages.length; stageIdx++) {
            DeploymentStage stage = stages[stageIdx];
            System.out.printf("%nStage %d: Rolling to %d%% of traffic%n",
                    stageIdx + 1, stage.percentage);

            // Simulate traffic for this stage's duration
            int sampleRequests = 1000;
            for (int i = 0; i < sampleRequests; i++) {
                boolean routesToNewVersion =
                        ThreadLocalRandom.current().nextInt(100) < stage.percentage;

                if (routesToNewVersion) {
                    // New version has a 2% error rate if bug exists
                    boolean isError = newVersionHasBug &&
                            ThreadLocalRandom.current().nextDouble() < 0.02;
                    metrics.recordRequest(isError);
                } else {
                    metrics.recordRequest(false); // old version works fine
                }
            }

            double errorRate = metrics.getErrorRate();
            System.out.printf("  Requests: %d, Errors: %d, Error rate: %.3f%%%n",
                    metrics.requests.get(), metrics.errors.get(), errorRate * 100);
            System.out.printf("  Threshold: %.2f%%%n", stage.maxErrorRate * 100);

            if (errorRate > stage.maxErrorRate) {
                System.out.println("  ✗ ERROR RATE EXCEEDED THRESHOLD!");
                System.out.println("  >>> AUTOMATIC ROLLBACK TRIGGERED <<<");
                return;
            }

            System.out.println("  ✓ Threshold passed, proceeding to next stage.");
            metrics.reset();
        }

        System.out.printf("%n✓ CANARY DEPLOYMENT COMPLETE — 100%% rollout successful%n");
    }
}
