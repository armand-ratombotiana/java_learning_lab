# Problem Walkthrough: AI Deployment & CI/CD

## Problem 1: Canary with Automated Rollback and CI/CD Gates — Company: Stripe

### Interview Scenario
"You're on Stripe's ML platform team, deploying a fraud-detection model that scores every payment in real time — a bad deployment means bad decisions at global scale. You need the full playbook in code: blue-green with health checks (and a candidate that must fail), a 5% canary whose traffic split is verifiable request-by-request, promotion with one-step rollback, a canary monitor that auto-rolls-back after 3 consecutive quality breaches, and a CI/CD pipeline that gates every build on an accuracy threshold. The lab's `ModelRegistry`, `TrafficRouter`, `DeploymentManager`, and `CiCdPipeline` are your building blocks — but the lab's router has a stale-weight bug that can send 100% of traffic to a retired version, so you must harden it."

### The Problem
1. Harden the lab's `TrafficRouter`: sorted deterministic iteration, and `clearExcept` so stale weights never survive a deployment change
2. Blue-green deploy v2 (must pass health check) and v2-broken (must fail and keep v2 active)
3. Canary-release v3 at 95/5 and verify the split deterministically over 20 requests — exactly 19/1
4. Prove routing consistency: the same request ID must hit the same version
5. Promote the canary, then roll back — both paths must work (promoted rollback and unpromoted canary rollback)
6. Add a `CanaryMonitor` that auto-rolls-back after 3 consecutive quality breaches below 0.70
7. Run the CI/CD pipeline: v5 (0.93) must deploy, v6 (0.65) must be aborted with the lab's exact message
8. Print registry history and build history

### Solution Walkthrough
- Step 1: Copy the lab's `ModelRegistry` and `ModelVersion` record verbatim
- Step 2: Harden the router: `TreeMap` for sorted iteration (the lab's `ConcurrentHashMap` iteration order is nondeterministic — the lab demo's own output routes all 20 requests to a retired v1 because its stale 100% weight won the hash-ordered race); `clearExcept` wipes and resets the table so old versions can never linger
- Step 3: Deterministic routing: `(requestId.hashCode() & 0x7fffffff) % 100` instead of a seeded `Random` — same request, same target, verified by calling `route("req-005")` twice
- Step 4: Copy the lab's `DeploymentManager`; make the health check deterministic (versions containing "broken" fail) and add the canary-aware rollback branch — an unpromoted canary must be deactivated, restoring 100% to the stable version, instead of printing "No previous deployment to rollback to"
- Step 5: Build `CanaryMonitor.observe(version, quality, threshold)` — breach streaks reset on OK, and 3 consecutive breaches trigger `dm.rollback()`
- Step 6: Copy the lab's `CiCdPipeline` — build, test with `accuracy >= 0.7`, deploy via canary, and build history
- Step 7: Sequence the full lifecycle and verify the routing counts by hand: request IDs were chosen so exactly one (req-060) hashes to the canary band — 19 v2 + 1 v3

### Code
```java
// File: src/com/aiengineering/lab10/DeploymentWalkthrough.java
package com.aiengineering.lab10;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Walkthrough: deployment strategies with CI/CD gating for a
 * fraud-detection model. Reuses the lab's ModelRegistry, TrafficRouter,
 * DeploymentManager, and CiCdPipeline. Fixes the lab demo's stale-weight
 * bug (router weights are replaced, never left behind) and makes
 * routing deterministic via sorted map iteration. Adds an automated
 * canary monitor that rolls back after 3 consecutive quality breaches.
 */
public class DeploymentWalkthrough {

    // ---------- Model Registry (lab) ----------

    public record ModelVersion(String id, String version, double accuracy, boolean healthy) {}

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

    // ---------- Traffic Router (lab, hardened) ----------

    static class TrafficRouter {
        private final Map<String, Double> weights = new TreeMap<>(); // sorted = deterministic iteration

        void setWeight(String modelVersion, double percentage) {
            weights.put(modelVersion, percentage);
        }

        void clearExcept(String modelVersion) {
            weights.clear();
            weights.put(modelVersion, 100.0);
        }

        String route(String requestId) {
            double r = (requestId.hashCode() & 0x7fffffff) % 100;
            double cumulative = 0;
            for (var entry : weights.entrySet()) {
                cumulative += entry.getValue();
                if (r <= cumulative) return entry.getKey();
            }
            return weights.isEmpty() ? "none" : ((TreeMap<String, Double>) weights).firstKey();
        }

        void printWeights() {
            System.out.println("  Traffic weights:");
            weights.forEach((k, v) -> System.out.printf("    %s: %.1f%%%n", k, v));
        }
    }

    // ---------- Deployment Manager (lab, with deterministic health check) ----------

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

            boolean healthCheck = simulateHealthCheck(newVersion);
            if (healthCheck) {
                activeDeployment = newVersion;
                router.clearExcept("model:" + newVersion);
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

            router.clearExcept("model:" + activeDeployment);
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
            router.clearExcept("model:" + newVersion);
            canaryActive.set(false);
            System.out.println("  Canary promoted. " + newVersion + " now serves 100% traffic.");
        }

        void rollback() {
            System.out.println("\n--- Rollback ---");
            if (previousDeployment != null) {
                activeDeployment = previousDeployment;
                router.clearExcept("model:" + previousDeployment);
                System.out.println("  Rolled back to " + previousDeployment);
                previousDeployment = null;
                return;
            }
            if (canaryActive.get()) {
                canaryActive.set(false);
                router.clearExcept("model:" + activeDeployment);
                System.out.println("  Canary rolled back; " + activeDeployment + " serves 100% traffic.");
                return;
            }
            System.out.println("  No previous deployment to rollback to.");
        }

        private boolean simulateHealthCheck(String version) {
            return !version.contains("broken");
        }

        String getActiveDeployment() { return activeDeployment; }
    }

    // ---------- Canary monitor with automatic rollback ----------

    static class CanaryMonitor {
        private final DeploymentManager dm;
        private int consecutiveBreaches = 0;

        CanaryMonitor(DeploymentManager dm) { this.dm = dm; }

        void observe(String version, double accuracy, double threshold) {
            boolean breach = accuracy < threshold;
            consecutiveBreaches = breach ? consecutiveBreaches + 1 : 0;
            System.out.printf("    canary %s quality=%.2f (threshold %.2f) — %s (breach streak %d)%n",
                version, accuracy, threshold, breach ? "BREACH" : "OK", consecutiveBreaches);
            if (consecutiveBreaches >= 3) {
                System.out.println("  AUTO-ROLLBACK triggered: 3 consecutive quality breaches.");
                dm.rollback();
                consecutiveBreaches = 0;
            }
        }
    }

    // ---------- CI/CD Pipeline (lab) ----------

    static class CiCdPipeline {
        private final DeploymentManager deploymentManager;
        private final List<String> buildHistory = new ArrayList<>();
        private int buildNumber = 0;

        CiCdPipeline(DeploymentManager dm) { this.deploymentManager = dm; }

        boolean runBuildAndDeploy(String modelId, String version, double accuracy) {
            buildNumber++;
            String buildId = "build-#" + buildNumber;
            System.out.println("\n=== CI/CD Pipeline: " + buildId + " ===");

            System.out.println("  Stage 1: Build — compiling model " + modelId + ":" + version);
            System.out.println("  Stage 2: Test — running unit tests...");
            boolean testsPass = accuracy >= 0.7;
            if (!testsPass) {
                System.out.println("  Tests FAILED (accuracy " + accuracy + " < 0.7). Aborting.");
                buildHistory.add(buildId + ":FAILED");
                return false;
            }
            System.out.println("  All tests passed.");

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

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Deployment & CI/CD for a Fraud-Detection Model ===\n");

        ModelRegistry registry = new ModelRegistry();
        TrafficRouter router = new TrafficRouter();
        DeploymentManager dm = new DeploymentManager(registry, router);

        // Initial deployment
        registry.register("fraud-model", "v1", 0.85);
        router.setWeight("model:v1", 100);
        System.out.println("Initial state: v1 serving 100% traffic");

        // Blue-green: healthy v2
        dm.blueGreenDeploy("fraud-model", "v2", 0.92);

        // Blue-green: broken candidate must be kept out
        dm.blueGreenDeploy("fraud-model", "v2-broken", 0.60);

        // Canary release with deterministic routing
        dm.canaryRelease("fraud-model", "v3", 0.88);

        System.out.println("\n--- Simulated Request Routing (v2 95% / v3 5%) ---");
        String[] requests = {"req-001", "req-002", "req-003", "req-004", "req-005",
                             "req-006", "req-007", "req-008", "req-009", "req-010",
                             "req-011", "req-012", "req-013", "req-014", "req-015",
                             "req-016", "req-017", "req-018", "req-019", "req-060"};
        Map<String, Long> routingCount = new TreeMap<>();
        for (String req : requests) {
            String target = router.route(req);
            routingCount.merge(target, 1L, Long::sum);
        }
        routingCount.forEach((k, v) -> System.out.printf("    %s: %d requests (%.0f%%)%n",
            k, v, v * 100.0 / requests.length));

        System.out.println("  Routing consistency (hash-based):");
        String first = router.route("req-005");
        String second = router.route("req-005");
        System.out.printf("    req-005 -> %s on both calls: %b%n", first, first.equals(second));

        // Promote and rollback
        dm.promoteCanary("fraud-model", "v3");
        dm.rollback();

        // Canary with automatic rollback on quality breaches
        System.out.println("\n--- Canary Quality Monitoring (auto-rollback) ---");
        dm.canaryRelease("fraud-model", "v4", 0.62);
        CanaryMonitor monitor = new CanaryMonitor(dm);
        double[] observed = {0.90, 0.64, 0.61, 0.58};
        for (double quality : observed) {
            monitor.observe("v4", quality, 0.70);
        }
        System.out.println("  Active deployment after monitoring: " + dm.getActiveDeployment());

        // CI/CD pipeline
        CiCdPipeline cicd = new CiCdPipeline(dm);
        cicd.runBuildAndDeploy("fraud-model", "v5", 0.93);
        cicd.runBuildAndDeploy("fraud-model", "v6", 0.65); // should fail
        cicd.printBuildHistory();

        // Model Registry History
        System.out.println("\n--- Model Registry History ---");
        System.out.println("  Versions of fraud-model:");
        for (ModelVersion mv : registry.getHistory("fraud-model")) {
            System.out.printf("    %s (acc=%.2f, healthy=%b)%n",
                mv.version(), mv.accuracy(), mv.healthy());
        }

        System.out.println("\nWalkthrough complete. " + registry.getHistory("fraud-model").size()
            + " model versions registered, " + cicd.buildHistory.size() + " CI/CD builds.");
    }
}
```

### Expected Output
```
=== Walkthrough: Deployment & CI/CD for a Fraud-Detection Model ===

Initial state: v1 serving 100% traffic

--- Blue-Green Deployment ---
  Blue (current): v1
  Green (new): v2
  Health check passed. Switched to v2
  ... second blue-green: "Health check FAILED. Keeping v2" (v2-broken) ...

--- Canary Release ---
  Canary: v3 (5% traffic)
  Traffic weights:
    model:v2: 95.0%
    model:v3: 5.0%

--- Simulated Request Routing (v2 95% / v3 5%) ---
    model:v2: 19 requests (95%)
    model:v3: 1 requests (5%)
  Routing consistency: req-005 -> model:v2 on both calls: true

--- Promote Canary to Full ---
  Canary promoted. v3 now serves 100% traffic.

--- Rollback ---
  Rolled back to v2

--- Canary Quality Monitoring (auto-rollback) ---
    canary v4 quality=0.90 (threshold 0.70) — OK (breach streak 0)
    canary v4 quality=0.64 (threshold 0.70) — BREACH (breach streak 1)
    canary v4 quality=0.61 (threshold 0.70) — BREACH (breach streak 2)
    canary v4 quality=0.58 (threshold 0.70) — BREACH (breach streak 3)
  AUTO-ROLLBACK triggered: 3 consecutive quality breaches.
  Canary rolled back; v2 serves 100% traffic.

=== CI/CD Pipeline: build-#1 ===
  All tests passed. (v5, acc 0.93)
  ... build-#2: "Tests FAILED (accuracy 0.65 < 0.7). Aborting." ...

Build history:
  build-#1:SUCCESS
  build-#2:FAILED

--- Model Registry History ---
  Versions of fraud-model:
    v1 (acc=0.85, healthy=true)
    v2 (acc=0.92, healthy=true)
    ...
    v5 (acc=0.93, healthy=true)

Walkthrough complete. 6 model versions registered, 2 CI/CD builds.
```

### Company Evaluation
- Oracle: Strategy design: weight semantics, canary rules, and rollback atomicity.
- Deloitte: Change governance: release process, approval gates, and rollback procedures.
- Accenture: Practice: deployment automation, canary methodology, and verification.
- PwC: Control: deployment auditability, change records, and continuity planning.
- Amazon: Scale: fleet deployment orchestration and self-healing patterns.

---

## Problem 2: Version Pin via Configuration — Company: Netflix

### Interview Scenario
"You're on Netflix's ML platform team. Every model version is an artifact in the registry; the serving service reads its version from config. Rolling back is a config change. Implement the registry lookup and config switch."

### The Problem
1. Register three versions of the ranking model
2. Load the active version from a config string
3. Switch the config to the previous version — the rollback
4. Print the serving state before and after

### Solution Walkthrough
- Step 1: Use the lab's `ModelRegistry.register` and `getHistory` to build the version list
- Step 2: `findVersion(history, "v3")` resolves the config value to a registered artifact — the Q&A's mechanism: the service loads a specific version via configuration
- Step 3: Rollback = rewriting the config to "v2" and re-resolving; no redeploy, no new artifact

### Code
```java
ModelRegistry registry = new ModelRegistry();
registry.register("ranking-model", "v1", 0.81);
registry.register("ranking-model", "v2", 0.89);
registry.register("ranking-model", "v3", 0.93);

List<ModelVersion> history = registry.getHistory("ranking-model");
String config = "v3";
System.out.println("Serving " + findVersion(history, config).version() + " (acc="
    + findVersion(history, config).accuracy() + ")");

config = "v2"; // rollback is a config change
System.out.println("Rollback: serving " + findVersion(history, config).version() + " (acc="
    + findVersion(history, config).accuracy() + ")");
```
with:
```java
static ModelVersion findVersion(List<ModelVersion> history, String version) {
    return history.stream().filter(v -> v.version().equals(version)).findFirst().orElseThrow();
}
```
Output:
```
Serving v3 (acc=0.93)
Rollback: serving v2 (acc=0.89)
```
The registry never forgets a version and the service never hardcodes one — that split is what makes rollback a seconds-scale operation instead of a redeploy.

### Company Evaluation
- Oracle: Config design: pin semantics, validation, and compatibility enforcement.
- Deloitte: Change management: config approval, rollout review, and rollback.
- Accenture: Practice: config-driven releases, testing, and documentation.
- PwC: Audit: config change records and compliance evidence.
- Amazon: Scale: config distribution consistency and fleet rollout.

---

## Problem 3: Weight-Based Gradual Ramp — Company: Uber

### Interview Scenario
"You're on Uber's trip-routing ML team. The canary ramp is 5% -> 25% -> 50% -> 100%, each step gated by the health of the previous one. Implement the ramp with the lab's router."

### The Problem
1. Start the canary at 5%
2. Step through the ramp, re-weighting the router at each stage
3. Gate each step: if the current stage's quality breaches its threshold, roll back instead of ramping up
4. Print the weight table at each step

### Solution Walkthrough
- Step 1: Use the hardened router's `clearExcept` + `setWeight` to rebuild the split at each stage — never accumulate weights
- Step 2: The gate reads the canary's live quality; the walkthrough uses a fixed table so the failure branch is exercised deterministically
- Step 3: On breach, keep the stable version at 100% — the blast radius at 5% was one request in twenty

### Code
```java
TrafficRouter router = new TrafficRouter();
router.clearExcept("model:v1");
router.setWeight("model:v1", 95);
router.setWeight("model:v2", 5);

double[] ramp = {25, 50, 100};
double[] quality = {0.96, 0.94, 0.55}; // stage 3 degrades
for (int i = 0; i < ramp.length; i++) {
    if (quality[i] < 0.70) {
        router.clearExcept("model:v1");
        System.out.printf("Stage %d: quality %.2f BREACH — rollback, v1 at 100%%%n", i + 1, quality[i]);
        return;
    }
    router.clearExcept("model:v2");
    router.setWeight("model:v2", ramp[i]);
    System.out.printf("Stage %d: quality %.2f OK — v2 now at %.0f%%%n", i + 1, quality[i], ramp[i]);
}
```
Output:
```
Stage 1: quality 0.96 OK — v2 now at 25%
Stage 2: quality 0.94 OK — v2 now at 50%
Stage 3: quality 0.55 BREACH — rollback, v1 at 100%
```
The canary grew to 50% before failing, but the failure surfaced while the majority of traffic was still on v1 — that's the canary contract: catch problems at low exposure, and every ramp step re-evaluates the gate the walkthrough's `CanaryMonitor` applies continuously.

### Company Evaluation
- Oracle: Ramp design: weight increments, request hashing, and determinism.
- Deloitte: Rollout governance: ramp plan, risk assessment, and decision gates.
- Accenture: Practice: gradual rollout methodology, monitoring, and escalation.
- PwC: Control: ramp auditability, change records, and performance compliance.
- Amazon: Scale: automated ramps, health-gated promotion, and fleet tooling.
