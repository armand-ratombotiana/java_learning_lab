package com.aiengineering.lab10;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * Demonstrates AI deployment and CI/CD concepts: model deployment
 * strategies, canary releases with traffic splitting, rollback
 * mechanisms, and blue-green deployment switching.
 * <p>
 * Includes a ModelRegistry, a DeploymentManager that simulates
 * canary releases, and a health-check based router.
 */
public class AiDeploymentAndCiCdDemo {

    // ---------- Model Version ----------

    public record ModelVersion(String id, String version, double accuracy, boolean healthy) {}

    // ---------- Model Registry ----------

    static class ModelRegistry {
        private final Map<String, List<ModelVersion>> models = new ConcurrentHashMap<>();

        void register(String modelId, String version, double accuracy) {
            models.computeIfAbsent(modelId, k -> new CopyOnWriteArrayList<>())
                .add(new ModelVersion(modelId, version, accuracy, true));
        }

        ModelVersion getLatest(String modelId) {
            List<ModelVersion> versions = models.get(modelId);
            if (versions == null || versions.isEmpty()) return null;
            return versions.get(versions.size() - 1);
        }

        List<ModelVersion> getHistory(String modelId) {
            return List.copyOf(models.getOrDefault(modelId, List.of()));
        }
    }

    // ---------- Traffic Router ----------

    static class TrafficRouter {
        private final Map<String, Double> weights = new ConcurrentHashMap<>();

        void setWeight(String modelVersion, double percentage) {
            weights.put(modelVersion, percentage);
        }

        String route(String requestId) {
            double r = new Random(requestId.hashCode()).nextDouble() * 100;
            double cumulative = 0;
            for (var entry : weights.entrySet()) {
                cumulative += entry.getValue();
                if (r <= cumulative) return entry.getKey();
            }
            return weights.isEmpty() ? "none" : weights.keySet().iterator().next();
        }

        void printWeights() {
            System.out.println("  Traffic weights:");
            weights.forEach((k, v) -> System.out.printf("    %s: %.1f%%%n", k, v));
        }
    }

    // ---------- Deployment Manager ----------

    enum DeploymentStrategy { BLUE_GREEN, CANARY, ROLLING }

    static class DeploymentManager {
        private final ModelRegistry registry;
        private final TrafficRouter router;
        private final AtomicBoolean canaryActive = new AtomicBoolean(false);
        private String activeDeployment = "v1";
        private String previousDeployment;

        DeploymentManager(ModelRegistry registry, TrafficRouter router) {
            this.registry = registry;
            this.router = router;
        }

        void blueGreenDeploy(String modelId, String newVersion, double accuracy) {
            System.out.println("\n--- Blue-Green Deployment ---");
            System.out.println("  Blue (current): " + activeDeployment);
            System.out.println("  Green (new): " + newVersion);

            registry.register(modelId, newVersion, accuracy);
            previousDeployment = activeDeployment;

            // Health check simulation
            boolean healthCheck = simulateHealthCheck(newVersion);
            if (healthCheck) {
                activeDeployment = newVersion;
                router.setWeight("model:" + newVersion, 100);
                System.out.println("  Health check passed. Switched to " + newVersion);
            } else {
                System.out.println("  Health check FAILED. Keeping " + activeDeployment);
            }
        }

        void canaryRelease(String modelId, String newVersion, double accuracy) {
            System.out.println("\n--- Canary Release ---");
            System.out.println("  Current: " + activeDeployment);
            System.out.println("  Canary: " + newVersion + " (5% traffic)");

            registry.register(modelId, newVersion, accuracy);
            canaryActive.set(true);

            // Route 5% traffic to new version
            router.setWeight("model:" + activeDeployment, 95);
            router.setWeight("model:" + newVersion, 5);
            router.printWeights();
        }

        void promoteCanary(String modelId, String newVersion) {
            System.out.println("\n--- Promote Canary to Full ---");
            if (!canaryActive.get()) {
                System.out.println("  No active canary to promote.");
                return;
            }
            previousDeployment = activeDeployment;
            activeDeployment = newVersion;
            router.setWeight("model:" + newVersion, 100);
            canaryActive.set(false);
            System.out.println("  Canary promoted. " + newVersion + " now serves 100% traffic.");
        }

        void rollback() {
            System.out.println("\n--- Rollback ---");
            if (previousDeployment == null) {
                System.out.println("  No previous deployment to rollback to.");
                return;
            }
            activeDeployment = previousDeployment;
            router.setWeight("model:" + previousDeployment, 100);
            System.out.println("  Rolled back to " + previousDeployment);
            previousDeployment = null;
        }

        private boolean simulateHealthCheck(String version) {
            // Simulate health check with occasional failure
            return new Random(version.hashCode()).nextDouble() > 0.1;
        }

        String getActiveDeployment() { return activeDeployment; }
    }

    // ---------- CI/CD Pipeline Simulator ----------

    static class CiCdPipeline {
        private final DeploymentManager deploymentManager;
        private final List<String> buildHistory = new ArrayList<>();
        private int buildNumber = 0;

        CiCdPipeline(DeploymentManager dm) { this.deploymentManager = dm; }

        boolean runBuildAndDeploy(String modelId, String version, double accuracy) {
            buildNumber++;
            String buildId = "build-#" + buildNumber;
            System.out.println("\n=== CI/CD Pipeline: " + buildId + " ===");

            // Stage 1: Build
            System.out.println("  Stage 1: Build — compiling model " + modelId + ":" + version);
            sleep(5);

            // Stage 2: Test
            System.out.println("  Stage 2: Test — running unit tests...");
            boolean testsPass = accuracy >= 0.7;
            if (!testsPass) {
                System.out.println("  Tests FAILED (accuracy " + accuracy + " < 0.7). Aborting.");
                buildHistory.add(buildId + ":FAILED");
                return false;
            }
            System.out.println("  All tests passed.");

            // Stage 3: Deploy (using canary for new versions)
            System.out.println("  Stage 3: Deploy — executing canary release...");
            deploymentManager.canaryRelease(modelId, version, accuracy);
            buildHistory.add(buildId + ":SUCCESS");
            return true;
        }

        void printBuildHistory() {
            System.out.println("\nBuild history:");
            buildHistory.forEach(h -> System.out.println("  " + h));
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 10: AI Deployment & CI/CD ===\n");

        ModelRegistry registry = new ModelRegistry();
        TrafficRouter router = new TrafficRouter();
        DeploymentManager dm = new DeploymentManager(registry, router);

        // Initial deployment
        registry.register("sentiment-model", "v1", 0.85);
        router.setWeight("model:v1", 100);
        System.out.println("Initial state: v1 serving 100% traffic");

        // Blue-Green Deployment
        dm.blueGreenDeploy("sentiment-model", "v2", 0.92);

        // Canary Release
        dm.canaryRelease("sentiment-model", "v3", 0.88);

        // Simulate some requests routing
        System.out.println("\n--- Simulated Request Routing ---");
        String[] requests = {"req-001", "req-002", "req-003", "req-004", "req-005",
                             "req-006", "req-007", "req-008", "req-009", "req-010",
                             "req-011", "req-012", "req-013", "req-014", "req-015",
                             "req-016", "req-017", "req-018", "req-019", "req-020"};
        Map<String, Long> routingCount = new HashMap<>();
        for (String req : requests) {
            String target = router.route(req);
            routingCount.merge(target, 1L, Long::sum);
        }
        System.out.println("  Request distribution across " + requests.length + " requests:");
        routingCount.forEach((k, v) -> System.out.printf("    %s: %d requests (%.0f%%)%n",
            k, v, v * 100.0 / requests.length));

        // Promote canary
        dm.promoteCanary("sentiment-model", "v3");

        // Rollback
        dm.rollback();

        // CI/CD Pipeline
        CiCdPipeline cicd = new CiCdPipeline(dm);
        cicd.runBuildAndDeploy("sentiment-model", "v4", 0.93);
        cicd.runBuildAndDeploy("sentiment-model", "v5", 0.65); // should fail
        cicd.printBuildHistory();

        // Model Registry History
        System.out.println("\n--- Model Registry History ---");
        System.out.println("  Versions of sentiment-model:");
        for (ModelVersion mv : registry.getHistory("sentiment-model")) {
            System.out.printf("    %s (acc=%.2f, healthy=%b)%n",
                mv.version(), mv.accuracy(), mv.healthy());
        }

        System.out.println("\nDemo complete. " + registry.getHistory("sentiment-model").size()
            + " model versions registered, " + cicd.buildHistory.size() + " CI/CD builds.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
