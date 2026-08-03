# Problem Walkthrough: Model Registry & Versioning

## Problem 1: Champion/Challenger Promotion with Auto-Archive — Company: Stripe
### Interview Scenario
"You're at Stripe. The fraud team's `fraud_detector` model has four candidate versions from the experiment tracker (run_abc123 through run_jkl012), and promotions are done by hand-editing a config file — last month a mistaken edit made two versions 'production' and the serving layer started alternating between two artifacts. Build a registry that assigns immutable versions, moves them through staging → production → archived, automatically retires the incumbent on promotion, and prints a champion/challenger report the team can read in one glance."

### The Problem
1. Register four `fraud_detector` versions with their metrics, parameters, and artifact paths.
2. Enforce a single production version: `promoteToProduction` must auto-archive the incumbent.
3. Walk the full lifecycle: v1 → prod, v2 → staging, v3 → staging → prod (displacing v1), v4 → staging, v2 → archived.
4. Print the registry annotated with `★ CHAMPION` (production) and `☆ CHALLENGER` (staging) tags.
5. Resolve and report the champion and challenger via `getProductionModel` / `getStagingModel`.

### Solution Walkthrough
- Step 1: Reuse the lab's `ModelVersion` — version number, `runId`, `metrics`, `parameters`, `artifactPath`, and a `stage` starting at `NONE` — and its `Stage` enum.
- Step 2: Register v1-v4 with `registerVersion("fraud_detector", runId, metrics, parameters, artifactPath)`, matching the lab's data: accuracies 0.923 / 0.935 / 0.947 / 0.941, XGBoost for v2/v3, NeuralNet for v4.
- Step 3: Drive the lifecycle with the lab's transition methods: `promoteToStaging(1)`, `promoteToProduction(1)`, `promoteToStaging(2)`, `promoteToStaging(3)`, `promoteToProduction(3)` — the last one triggers the auto-archive of v1.
- Step 4: Continue with `promoteToStaging(4)` (new challenger) and `archiveVersion(2)` (manual retirement) exactly as the demo does.
- Step 5: Print the registry via `printRegistry`, which appends the champion/challenger tags to each version line.
- Step 6: Resolve `getProductionModel("fraud_detector")` and `getStagingModel("fraud_detector")` and print them as the final answer the serving layer would consume.
- Step 7: Note the invariants the run proves: exactly one production version at the end, the incumbent auto-archived, and all four versions still listed — history is immutable.

### Code
```java
package com.mlops.lab03;

import java.util.*;

public class ModelRegistryWalkthrough {

    enum Stage { NONE, STAGING, PRODUCTION, ARCHIVED }

    static class ModelVersion {
        final int version;
        final String modelName;
        final String runId;
        final Map<String, Double> metrics;
        final Map<String, String> parameters;
        final String artifactPath;
        Stage stage;

        ModelVersion(int version, String modelName, String runId,
                      Map<String, Double> metrics, Map<String, String> parameters,
                      String artifactPath) {
            this.version = version;
            this.modelName = modelName;
            this.runId = runId;
            this.metrics = metrics;
            this.parameters = parameters;
            this.artifactPath = artifactPath;
            this.stage = Stage.NONE;
        }

        void transitionTo(Stage newStage) {
            System.out.printf("  Model %s v%d: %s → %s%n",
                    modelName, version, stage, newStage);
            this.stage = newStage;
        }

        @Override
        public String toString() {
            return String.format("v%d [%s] acc=%.4f run=%s",
                    version, stage, metrics.getOrDefault("accuracy", 0.0),
                    runId.substring(0, 8));
        }
    }

    static class ModelRegistry {
        private final Map<String, List<ModelVersion>> models = new HashMap<>();
        private int versionCounter = 0;

        ModelVersion registerVersion(String modelName, String runId,
                                      Map<String, Double> metrics,
                                      Map<String, String> parameters,
                                      String artifactPath) {
            ModelVersion mv = new ModelVersion(++versionCounter, modelName, runId,
                    metrics, parameters, artifactPath);
            models.computeIfAbsent(modelName, k -> new ArrayList<>()).add(mv);
            System.out.printf("Registered %s v%d (accuracy=%.4f)%n",
                    modelName, mv.version, metrics.getOrDefault("accuracy", 0.0));
            return mv;
        }

        ModelVersion getVersion(String modelName, int version) {
            return models.getOrDefault(modelName, List.of()).stream()
                    .filter(v -> v.version == version)
                    .findFirst().orElse(null);
        }

        ModelVersion getProductionModel(String modelName) {
            return models.getOrDefault(modelName, List.of()).stream()
                    .filter(v -> v.stage == Stage.PRODUCTION)
                    .findFirst().orElse(null);
        }

        ModelVersion getStagingModel(String modelName) {
            return models.getOrDefault(modelName, List.of()).stream()
                    .filter(v -> v.stage == Stage.STAGING)
                    .findFirst().orElse(null);
        }

        void promoteToStaging(String modelName, int version) {
            getVersion(modelName, version).transitionTo(Stage.STAGING);
        }

        void promoteToProduction(String modelName, int version) {
            ModelVersion mv = getVersion(modelName, version);
            ModelVersion current = getProductionModel(modelName);
            if (current != null && current.version != version) {
                current.transitionTo(Stage.ARCHIVED);
            }
            mv.transitionTo(Stage.PRODUCTION);
        }

        void archiveVersion(String modelName, int version) {
            getVersion(modelName, version).transitionTo(Stage.ARCHIVED);
        }

        void printRegistry(String modelName) {
            System.out.printf("%nModel Registry: %s%n", modelName);
            System.out.println("----------------------------");
            for (ModelVersion mv : models.getOrDefault(modelName, List.of())) {
                String tag = mv.stage == Stage.PRODUCTION ? " ★ CHAMPION" :
                        mv.stage == Stage.STAGING ? " ☆ CHALLENGER" : "";
                System.out.printf("  %s%s%n", mv, tag);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Model Registry & Versioning ===\n");

        ModelRegistry registry = new ModelRegistry();
        ModelVersion v1 = registry.registerVersion("fraud_detector", "run_abc123",
                Map.of("accuracy", 0.923, "precision", 0.91, "recall", 0.89, "f1", 0.90),
                Map.of("lr", "0.01", "epochs", "50", "model", "RandomForest"),
                "s3://models/fraud_detector/v1/model.pkl");
        ModelVersion v2 = registry.registerVersion("fraud_detector", "run_def456",
                Map.of("accuracy", 0.935, "precision", 0.93, "recall", 0.91, "f1", 0.92),
                Map.of("lr", "0.005", "epochs", "100", "model", "XGBoost"),
                "s3://models/fraud_detector/v2/model.pkl");
        ModelVersion v3 = registry.registerVersion("fraud_detector", "run_ghi789",
                Map.of("accuracy", 0.947, "precision", 0.94, "recall", 0.93, "f1", 0.935),
                Map.of("lr", "0.001", "epochs", "150", "model", "XGBoost"),
                "s3://models/fraud_detector/v3/model.pkl");
        ModelVersion v4 = registry.registerVersion("fraud_detector", "run_jkl012",
                Map.of("accuracy", 0.941, "precision", 0.935, "recall", 0.92, "f1", 0.927),
                Map.of("lr", "0.01", "epochs", "200", "model", "NeuralNet"),
                "s3://models/fraud_detector/v4/model.pkl");

        System.out.println("\n--- Lifecycle Transitions ---");
        registry.promoteToStaging("fraud_detector", 1);
        registry.promoteToProduction("fraud_detector", 1);
        registry.promoteToStaging("fraud_detector", 2);
        registry.promoteToStaging("fraud_detector", 3);
        registry.promoteToProduction("fraud_detector", 3);
        registry.promoteToStaging("fraud_detector", 4);
        registry.archiveVersion("fraud_detector", 2);

        registry.printRegistry("fraud_detector");

        ModelVersion champion = registry.getProductionModel("fraud_detector");
        System.out.printf("%nChampion: %s%n", champion);
        System.out.printf("Challenger: %s%n", registry.getStagingModel("fraud_detector"));
        System.out.printf("One production version: %s%n",
                champion != null && champion.stage == Stage.PRODUCTION ? "true" : "false");
    }
}
```

### Expected Output
```
=== Model Registry & Versioning ===

Registered fraud_detector v1 (accuracy=0.9230)
Registered fraud_detector v2 (accuracy=0.9350)
Registered fraud_detector v3 (accuracy=0.9470)
Registered fraud_detector v4 (accuracy=0.9410)

--- Lifecycle Transitions ---
  Model fraud_detector v1: NONE → STAGING
  Model fraud_detector v1: STAGING → PRODUCTION
  Model fraud_detector v2: NONE → STAGING
  Model fraud_detector v3: NONE → STAGING
  Model fraud_detector v1: PRODUCTION → ARCHIVED
  Model fraud_detector v3: STAGING → PRODUCTION
  Model fraud_detector v4: NONE → STAGING
  Model fraud_detector v2: STAGING → ARCHIVED

Model Registry: fraud_detector
----------------------------
  v1 [ARCHIVED] acc=0.9230 run=run_abc1
  v2 [ARCHIVED] acc=0.9350 run=run_def4
  v3 [PRODUCTION] acc=0.9470 run=run_ghi7 ★ CHAMPION
  v4 [STAGING] acc=0.9410 run=run_jkl0 ☆ CHALLENGER

Champion: v3 [PRODUCTION] acc=0.9470 run=run_ghi7
Challenger: v4 [STAGING] acc=0.9410 run=run_jkl0
One production version: true
```

---

## Problem 2: Registry Rollback by Re-Tagging — Company: Amazon
### Interview Scenario
"You're at Amazon. Three days after v3 of `fraud_detector` promoted to production, the fraud team sees a 12% precision drop in online metrics — v1's behavior was safer. Roll the production pointer back without losing any history."

### The Problem
1. Roll back by re-tagging v1 as production, not by deleting v3.
2. Auto-archive the incumbent champion (v3) during the rollback.
3. Show the immutable history still lists every version and transition.

### Solution Walkthrough
- Step 1: Call `promoteToProduction("fraud_detector", 1)` — the same code path used for forward promotion, because rollback is just re-tagging an older immutable version.
- Step 2: Observe the two transitions: v3 `PRODUCTION → ARCHIVED` (incumbent auto-archive) then v1 `ARCHIVED → PRODUCTION`.
- Step 3: Print the registry: v1 is champion again, v3 remains listed (retired, not deleted), and the audit trail of promotions is untouched.
- Step 4: Note the production-grade add-on from the INTERVIEW notes: validate the rollback target against the current data schema before re-tagging.

### Code
```java
// Rollback from v3 back to v1 — same API as promotion
registry.promoteToProduction("fraud_detector", 1);

System.out.println("\nModel Registry after rollback:");
for (ModelVersion mv : registry.listVersions("fraud_detector")) {
    System.out.printf("  v%d [%s]%n", mv.version, mv.stage);
}
System.out.printf("Production pointer now: %s%n",
        registry.getProductionModel("fraud_detector"));
```
### Expected Output
```
  Model fraud_detector v3: PRODUCTION → ARCHIVED
  Model fraud_detector v1: ARCHIVED → PRODUCTION

Model Registry after rollback:
  v1 [PRODUCTION]
  v2 [ARCHIVED]
  v3 [ARCHIVED]
  v4 [STAGING]
Production pointer now: v1 [PRODUCTION] acc=0.9230 run=run_abc1
```

---

## Problem 3: Traffic Routing Between Champion and Challenger — Company: Netflix
### Interview Scenario
"You're at Netflix. The recommendation team wants to shadow-test challenger v2 against champion v1 by routing a deterministic 10% of traffic to it, without changing the registry's single-champion invariant."

### The Problem
1. Route requests by a stable hash of the user ID — the same user always hits the same version.
2. Route 90% to the champion, 10% to the challenger.
3. Report the actual split over 10,000 simulated requests.

### Solution Walkthrough
- Step 1: Use `Math.abs(Long.hashCode(userId)) % 100 < 10` as the 10% routing rule — deterministic per user, matching the A/B assignment pattern from Lab 10.
- Step 2: The registry stays untouched: `getProductionModel` still returns one champion; routing is a serving-layer concern.
- Step 3: Count hits per version and print the split, which should land near the configured ratio.

### Code
```java
Random rng = new Random(42);
int championHits = 0, challengerHits = 0;
for (long userId = 0; userId < 10000; userId++) {
    if (Math.abs(Long.hashCode(userId)) % 100 < 10) {
        challengerHits++;   // route to v2 challenger
    } else {
        championHits++;     // route to v1 champion
    }
}
System.out.printf("Champion (v1): %d (%.1f%%)%n", championHits, championHits / 100.0);
System.out.printf("Challenger (v2): %d (%.1f%%)%n", challengerHits, challengerHits / 100.0);
```
### Expected Output
```
Champion (v1): 9000 (90.0%)
Challenger (v2): 1000 (10.0%)
```
