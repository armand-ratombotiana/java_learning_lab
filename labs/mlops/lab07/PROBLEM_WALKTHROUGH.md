# Problem Walkthrough: CI/CD for ML Pipelines

## Problem 1: Champion-Gated Retraining Pipeline — Company: Spotify
### Interview Scenario
"You're at Spotify. The weekly playlist-recommendation retrain runs manually: an engineer trains, eyeballs the accuracy, and promotes. Twice last quarter a regression reached production because nobody compared the new model against the current champion. Build a deterministic CI/CD simulation — the lab's `CiCdForMLPipelineLab` pattern — that runs the eight stages in dependency order, hard-stops the pipeline if the evaluation gate fails, and prints the pass/fail ledger for every stage."

### The Problem
1. Model the eight stages (Code Checkout → Data Validation → Feature Engineering → Model Training → Model Evaluation → Deploy Staging → Integration Tests → Deploy Production) with explicit dependencies.
2. Enforce the evaluation gate: `accuracy 0.947` must beat `champion 0.935` or the stage throws and the pipeline stops.
3. Print the `▶ Stage:` / `✓ passed` / `✗ FAILED` ledger for each stage, and `Pipeline FAILED at stage: X` on gate failure.
4. Prove the gate works both ways: run the happy path (gate passes) and a regression scenario (accuracy 0.931) in the same program.
5. Keep the simulation deterministic — no random failures — so the output is a reproducible review artifact.

### Solution Walkthrough
- Step 1: Reuse the lab's `PipelineStage` — `name`, `dependencies`, `Runnable action`, `passed` flag, and `execute()` printing `▶ Stage: X` then `✓ passed` or `✗ FAILED: reason`.
- Step 2: Chain stages with the lab's dependency map: `stageMap.get(d).passed` must be true before a stage runs; otherwise print the skip line `⧖ Stage: X — skipped (dependency failed)`.
- Step 3: Build the evaluation gate exactly like the lab: a local `accuracy` (0.947 for the happy path, 0.931 for the regression) vs `champion = 0.935`; throw `RuntimeException("Accuracy " + accuracy + " < champion " + champion)` when below.
- Step 4: Drive the stage loop with the lab's break-on-failure rule: after any `!stage.passed`, print `Pipeline FAILED at stage: X` and stop — downstream stages never run.
- Step 5: Run the pipeline twice with a parameterized gate — `runPipeline("playlist_retrain", 0.947)` then `runPipeline("playlist_retrain_regression", 0.931)` — printing the full ledger for each run, ending with the overall verdict.

### Code
```java
package com.mlops.lab07;

import java.util.*;
import java.util.stream.Collectors;

public class CiCdWalkthrough {

    static class PipelineStage {
        final String name;
        final List<String> dependencies;
        final Runnable action;
        boolean passed;

        PipelineStage(String name, List<String> dependencies, Runnable action) {
            this.name = name;
            this.dependencies = dependencies;
            this.action = action;
        }

        void execute() {
            System.out.printf("  ▶ Stage: %s%n", name);
            try {
                action.run();
                passed = true;
                System.out.printf("    ✓ %s passed%n", name);
            } catch (Exception e) {
                passed = false;
                System.out.printf("    ✗ %s FAILED: %s%n", name, e.getMessage());
            }
        }
    }

    static void runPipeline(String pipelineName, double accuracy) {
        System.out.printf("%n=== %s (accuracy=%.3f) ===%n", pipelineName, accuracy);

        List<PipelineStage> stages = List.of(
            new PipelineStage("Code Checkout", List.of(), () ->
                    System.out.println("    Repo cloned, commit pinned")),
            new PipelineStage("Data Validation", List.of("Code Checkout"), () ->
                    System.out.println("    Schema valid, distributions OK")),
            new PipelineStage("Feature Engineering", List.of("Data Validation"), () ->
                    System.out.println("    Features computed")),
            new PipelineStage("Model Training", List.of("Feature Engineering"), () ->
                    System.out.printf("    Model trained with accuracy=%.3f%n", accuracy)),
            new PipelineStage("Model Evaluation", List.of("Model Training"), () -> {
                double champion = 0.935;
                if (accuracy < champion) {
                    throw new RuntimeException(
                            "Accuracy " + accuracy + " < champion " + champion);
                }
                System.out.printf("    ✓ Accuracy %.3f > champion %.3f%n", accuracy, champion);
            }),
            new PipelineStage("Deploy Staging", List.of("Model Evaluation"), () ->
                    System.out.println("    Model deployed to staging")),
            new PipelineStage("Integration Tests", List.of("Deploy Staging"), () ->
                    System.out.println("    Shadow test passed (1000 requests, 0 errors)")),
            new PipelineStage("Deploy Production", List.of("Integration Tests"), () ->
                    System.out.println("    ✓ Model promoted to production via MLflow registry"))
        );

        Map<String, PipelineStage> stageMap = stages.stream()
                .collect(Collectors.toMap(s -> s.name, s -> s));

        for (PipelineStage stage : stages) {
            boolean depsOk = stage.dependencies.stream()
                    .allMatch(d -> stageMap.get(d).passed);
            if (!depsOk) {
                System.out.printf("  ⧖ Stage: %s — skipped (dependency failed)%n", stage.name);
                continue;
            }
            stage.execute();
            if (!stage.passed) {
                System.out.printf("%nPipeline FAILED at stage: %s%n", stage.name);
                return;
            }
        }
        System.out.println("\nPipeline SUCCEEDED — model promoted to production");
    }

    public static void main(String[] args) {
        System.out.println("=== CI/CD for ML Pipelines — Gate Simulation ===\n");

        runPipeline("playlist_retrain", 0.947);
        runPipeline("playlist_retrain_regression", 0.931);
    }
}
```

### Expected Output
```
=== CI/CD for ML Pipelines — Gate Simulation ===

=== playlist_retrain (accuracy=0.947) ===
  ▶ Stage: Code Checkout
    Repo cloned, commit pinned
    ✓ Code Checkout passed
  ▶ Stage: Data Validation
    Schema valid, distributions OK
    ✓ Data Validation passed
  ▶ Stage: Feature Engineering
    Features computed
    ✓ Feature Engineering passed
  ▶ Stage: Model Training
    Model trained with accuracy=0.947
    ✓ Model Training passed
  ▶ Stage: Model Evaluation
    ✓ Accuracy 0.947 > champion 0.935
    ✓ Model Evaluation passed
  ▶ Stage: Deploy Staging
    Model deployed to staging
    ✓ Deploy Staging passed
  ▶ Stage: Integration Tests
    Shadow test passed (1000 requests, 0 errors)
    ✓ Integration Tests passed
  ▶ Stage: Deploy Production
    ✓ Model promoted to production via MLflow registry
    ✓ Deploy Production passed

Pipeline SUCCEEDED — model promoted to production

=== playlist_retrain_regression (accuracy=0.931) ===
  ▶ Stage: Code Checkout
    Repo cloned, commit pinned
    ✓ Code Checkout passed
  ▶ Stage: Data Validation
    Schema valid, distributions OK
    ✓ Data Validation passed
  ▶ Stage: Feature Engineering
    Features computed
    ✓ Feature Engineering passed
  ▶ Stage: Model Training
    Model trained with accuracy=0.931
    ✓ Model Training passed
  ▶ Stage: Model Evaluation
    ✗ Model Evaluation FAILED: Accuracy 0.931 < champion 0.935

Pipeline FAILED at stage: Model Evaluation
```
*(The break-on-failure rule stops the loop at the first failed stage, so Deploy Staging and everything downstream never execute in the regression run.)*

---

## Problem 2: GitHub Actions Workflow with Registry Gate — Company: Uber
### Interview Scenario
"You're at Uber. The lab's generated workflow runs the pipeline, but the evaluation job only echoes the champion comparison. Generate a workflow where the evaluation result is consumed by a deployment gate."

### The Problem
1. Generate a GitHub Actions workflow with `needs` dependencies mirroring the stage graph.
2. Make evaluation a hard gate via `if: success()` and an explicit failure step.
3. Keep production deployment on an `environment: production` approval gate.

### Solution Walkthrough
- Step 1: Mirror the lab's `generateGitHubActionsWorkflow` job chain: `data-validation` → `training` (needs data-validation) → `evaluation` (needs training) → `deploy-staging` → `integration-test` → `deploy-production`.
- Step 2: Add the gate step: evaluate, compare with the champion, and `exit 1` on regression so `needs` propagates failure.
- Step 3: Keep the `environment: production` block — GitHub's manual approval gate before the final promotion.

### Code
```yaml
evaluation:
  needs: training
  runs-on: ubuntu-latest
  steps:
    - name: Evaluate Against Champion
      run: |
        ACCURACY=0.947
        CHAMPION=0.935
        echo "Accuracy: $ACCURACY (champion: $CHAMPION)"
        if (( $(echo "$ACCURACY < $CHAMPION" | bc -l) )); then
          echo "Gate FAILED: accuracy below champion"
          exit 1
        fi
        echo "Gate PASSED ✓"

deploy-production:
  needs: integration-test
  runs-on: ubuntu-latest
  environment: production
  steps:
    - name: Promote via MLflow Registry
      run: echo "transition_stage(model, version, PRODUCTION)"
```
### Expected Output
```
Gate PASSED ✓
→ deploy-production runs only after integration-test succeeds and manual approval is given
```

---

## Problem 3: Drift-Triggered Retrain — Company: Netflix
### Interview Scenario
"You're at Netflix. The weekly cron retrains regardless of need. Replace the schedule trigger with a drift-driven trigger so training only runs when the data actually changed."

### The Problem
1. Compute a PSI-style drift signal on new data (the Lab 08 detector's contract).
2. Trigger the pipeline when PSI exceeds the threshold, skip it otherwise.
3. Print the trigger decision for two windows.

### Solution Walkthrough
- Step 1: Use Lab 08's `computePSI` formula: `Σ (a−e)·ln(a/e)` between reference and current distributions.
- Step 2: Classify with the lab's thresholds: `< 0.1` NONE, `< 0.25` WARNING, `≥ 0.25` CRITICAL.
- Step 3: Retrain on CRITICAL only; WARNING logs an alert for humans; NONE skips.

### Code
```java
double[] reference = {0.3, 0.25, 0.2, 0.15, 0.1};   // training-time distribution
double[] window1 = {0.29, 0.24, 0.21, 0.16, 0.10};  // PSI = 0.0019 → NONE
double[] window2 = {0.10, 0.10, 0.30, 0.30, 0.20};  // PSI = 0.5710 → CRITICAL

for (double[] window : List.of(window1, window2)) {
    double psi = 0.0;
    for (int i = 0; i < reference.length; i++) {
        double e = Math.max(reference[i], 1e-10);
        double a = Math.max(window[i], 1e-10);
        psi += (a - e) * Math.log(a / e);
    }
    String decision = psi >= 0.25 ? "TRIGGER retrain" :
            psi >= 0.1 ? "ALERT (no retrain)" : "SKIP retrain";
    System.out.printf("PSI=%.4f -> %s%n", psi, decision);
}
```
### Expected Output
```
PSI=0.0019 -> SKIP retrain
PSI=0.5710 -> TRIGGER retrain
```
