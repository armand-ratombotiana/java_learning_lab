# Problem Walkthrough: Model Governance & Compliance

## Problem 1: Loan-Approval Model Governance Suite — Company: JPMorgan Chase

### Interview Scenario

> **Interviewer**: "A consumer-lending model is under regulatory review. We need three things in Java, all deterministic and runnable in CI: a model card documenting the model and its per-group performance, a bias detector that computes demographic parity, equal opportunity, and the disparate-impact ratio, and a tamper-evident audit trail where every promotion is recorded in a hash chain. The demo should simulate 1,000 predictions per group with a seeded RNG, and every printed number must match a compiled run."
>
> **Candidate**: "Understood — and since governance artifacts are read by auditors, I'll make the demo fully reproducible, including the timestamps and audit hashes."

### The Problem

1. Build a `ModelCard` for the loan model (v2.1.0, gradient boosted tree): intended use, three limitations, and per-group accuracy/precision/recall/f1 with sample sizes.
2. Simulate 1,000 predictions per group with `Random(42)` — Group A detected at 92%, Group B at 85%, both at a 30% base rate — and compute the three fairness metrics with threshold verdicts.
3. Log six lifecycle events (register, promote, validate, promote, register, promote) into a hash-chained `AuditTrail` and verify chain integrity.
4. Make every printed value deterministic — including creation date and audit timestamps, which the lab leaves as `Instant.now()` (runtime-dependent) and the walkthrough pins to a fixed instant.
5. All output must be reproducible exactly as printed, on a single compile-and-run.

### Solution Walkthrough

1. **Model the card as the governance contract.** `ModelCard` holds identity, `intendedUse`, `limitations`, and the two metrics blocks. `addGroupMetrics` records accuracy/precision/recall/f1 plus a fixed `sample_size` of 1000 per group — because fairness claims are meaningless without the denominator; a regulator reads "Group B (Protected): recall=0.84, n=1000" differently than a bare number.
2. **Fix the clock for reproducibility.** The lab's constructor stamps `Instant.now()` — different every run, so the audit hashes (which hash the timestamp) would never match twice. The walkthrough keeps the identical signatures but pins both `createdDate` and `AuditEntry.timestamp` to `Instant.parse("2025-01-15T10:00:00Z")`. The lab remains correct for its purpose; the walkthrough adds determinism so the Expected Output is a verifiable contract.
3. **Print the card exactly as the lab formats it.** A text block renders the header (Type, Framework, Created, Intended Use), then the limitations list, then each group's metrics map in insertion order — `{accuracy=0.923, precision=0.91, recall=0.89, f1=0.9, sample_size=1000.0}` — showing the protected group trailing the majority group on every metric.
4. **Simulate predictions with a seeded RNG.** One `Random(42)` drives all four arrays: the ground truth at 30% (`nextDouble() < 0.3`), then the prediction at `p < (actual ? 0.92 : 0.08)` for Group A and `p < (actual ? 0.85 : 0.15)` for Group B. The whole dataset is therefore a fixed property of the seed — run it twice, same lists.
5. **Compute the three metrics from raw lists.** `demographicParityDifference` compares positive prediction rates: 0.0400, under the 0.1 threshold. `equalOpportunityDifference` compares true positive rates over the *actually positive* rows: 0.0598, also under 0.1. `disparateImpactRatio` divides the lower positive rate by the higher: 0.8830, above the 0.8 legal bar — `✓ (satisfies 80%% rule)`. (The double `%%` is faithful to the lab: the verdict string is passed as a `%s` argument, so the literal `%%` prints as-is.)
6. **Verify the verdict strings.** Each line picks `✓` or the warning symbol against the lab's thresholds (0.1 for differences, 0.8 for the ratio) — all three pass in this simulation, so the report shows three green lines while the model card still documents the underlying group gap honestly.
7. **Chain the audit entries.** Each `AuditEntry` stores the previous entry's hash and hashes `action + modelName + version + user + details + timestamp + previousHash` into `Integer.toHexString(...hashCode())`. Six entries chain from the genesis `"0"`: REGISTER 1.0.0 → PROMOTE staging → VALIDATE → PROMOTE production → REGISTER 2.0.0 (updated training data) → PROMOTE 2.1.0 (bias mitigation applied).
8. **Verify integrity by replay.** `verifyIntegrity` recomputes every hash in order from `"0"` and compares against stored hashes — the six stored values below match the replay, so the verdict is `INTACT`. The demo closes the loop: card documents the model, metrics certify fairness, the chain records who did what.
9. **Run and lock the transcript.** The Expected Output below is the exact stdout of the walkthrough class with the pinned clock — every hash (`4bf8a5f3` … `55550e35`) is deterministic.

### Code

```java
package com.mlops.lab11;

import java.time.Instant;
import java.util.*;

public class ModelGovernanceWalkthrough {

    static final Instant FIXED_NOW = Instant.parse("2025-01-15T10:00:00Z");

    static class ModelCard {
        final String modelName;
        final String version;
        final String modelType;
        final String framework;
        final Instant createdDate;
        final String intendedUse;
        final List<String> limitations;
        final String trainingDataDescription;
        final Map<String, Map<String, Double>> perGroupMetrics;
        final Map<String, Double> fairnessMetrics;

        ModelCard(String modelName, String version, String modelType,
                   String framework, String intendedUse) {
            this.modelName = modelName;
            this.version = version;
            this.modelType = modelType;
            this.framework = framework;
            this.createdDate = FIXED_NOW;
            this.intendedUse = intendedUse;
            this.limitations = new ArrayList<>();
            this.trainingDataDescription = "";
            this.perGroupMetrics = new LinkedHashMap<>();
            this.fairnessMetrics = new LinkedHashMap<>();
        }

        void addLimitation(String limitation) { limitations.add(limitation); }

        void addGroupMetrics(String group, double accuracy, double precision, double recall, double f1) {
            Map<String, Double> m = new LinkedHashMap<>();
            m.put("accuracy", accuracy);
            m.put("precision", precision);
            m.put("recall", recall);
            m.put("f1", f1);
            m.put("sample_size", 1000.0);
            perGroupMetrics.put(group, m);
        }

        void print() {
            System.out.printf("""
                    Model Card: %s v%s
                    ================================
                    Type:      %s
                    Framework: %s
                    Created:   %s
                    Intended Use: %s
                    
                    Limitations:
                    """, modelName, version, modelType, framework,
                    createdDate.toString().substring(0, 19), intendedUse);
            limitations.forEach(l -> System.out.println("  - " + l));
            System.out.println("\nPer-Group Performance:");
            for (Map.Entry<String, Map<String, Double>> entry : perGroupMetrics.entrySet()) {
                System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
            }
            if (!fairnessMetrics.isEmpty()) {
                System.out.println("\nFairness Metrics:");
                fairnessMetrics.forEach((k, v) -> System.out.printf("  %s: %.4f%n", k, v));
            }
        }
    }

    static class BiasDetector {

        static double demographicParityDifference(List<Boolean> predictionsGroupA,
                                                    List<Boolean> predictionsGroupB) {
            double rateA = positiveRate(predictionsGroupA);
            double rateB = positiveRate(predictionsGroupB);
            return Math.abs(rateA - rateB);
        }

        static double equalOpportunityDifference(List<Boolean> predictionsGroupA,
                                                   List<Boolean> actualGroupA,
                                                   List<Boolean> predictionsGroupB,
                                                   List<Boolean> actualGroupB) {
            double tprA = truePositiveRate(predictionsGroupA, actualGroupA);
            double tprB = truePositiveRate(predictionsGroupB, actualGroupB);
            return Math.abs(tprA - tprB);
        }

        static double disparateImpactRatio(List<Boolean> predictionsGroupA,
                                             List<Boolean> predictionsGroupB) {
            double rateA = positiveRate(predictionsGroupA);
            double rateB = positiveRate(predictionsGroupB);
            double min = Math.min(rateA, rateB);
            double max = Math.max(rateA, rateB);
            return max == 0 ? 1.0 : min / max;
        }

        private static double positiveRate(List<Boolean> predictions) {
            if (predictions.isEmpty()) return 0.0;
            return (double) predictions.stream().filter(p -> p).count() / predictions.size();
        }

        private static double truePositiveRate(List<Boolean> predictions, List<Boolean> actual) {
            long tp = 0, actualPositive = 0;
            for (int i = 0; i < predictions.size(); i++) {
                if (actual.get(i)) {
                    actualPositive++;
                    if (predictions.get(i)) tp++;
                }
            }
            return actualPositive == 0 ? 0.0 : (double) tp / actualPositive;
        }
    }

    static class AuditEntry {
        final String action;
        final String modelName;
        final String version;
        final String user;
        final String details;
        final Instant timestamp;
        final String previousHash;
        String hash;

        AuditEntry(String action, String modelName, String version,
                    String user, String details, String previousHash) {
            this.action = action;
            this.modelName = modelName;
            this.version = version;
            this.user = user;
            this.details = details;
            this.timestamp = FIXED_NOW;
            this.previousHash = previousHash;
            this.hash = computeHash();
        }

        private String computeHash() {
            String input = action + modelName + version + user + details + timestamp + previousHash;
            return Integer.toHexString(input.hashCode());
        }

        @Override
        public String toString() {
            return String.format("[%s] %s | model=%s v%s | user=%s | detail=%s | hash=%s",
                    timestamp.toString().substring(0, 19), action, modelName, version, user, details, hash);
        }
    }

    static class AuditTrail {
        private final List<AuditEntry> entries = new ArrayList<>();
        private String lastHash = "0";

        void log(String action, String modelName, String version, String user, String details) {
            AuditEntry entry = new AuditEntry(action, modelName, version, user, details, lastHash);
            entries.add(entry);
            lastHash = entry.hash;
        }

        void printAll() {
            System.out.println("Audit Trail:");
            entries.forEach(e -> System.out.println("  " + e));
        }

        boolean verifyIntegrity() {
            String previous = "0";
            for (AuditEntry entry : entries) {
                String expected = Integer.toHexString(
                        (entry.action + entry.modelName + entry.version + entry.user
                                + entry.details + entry.timestamp + previous).hashCode());
                if (!entry.hash.equals(expected)) return false;
                previous = entry.hash;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Model Governance & Compliance ===\n");

        ModelCard card = new ModelCard("loan_approval", "2.1.0",
                "Gradient Boosted Tree", "XGBoost",
                "Automated loan approval decisions for personal loans up to $50,000");
        card.addLimitation("Not validated for commercial loans");
        card.addLimitation("Training data skewed towards urban populations");
        card.addLimitation("Model performance degrades for applicants with thin credit files");
        card.addGroupMetrics("Overall", 0.923, 0.91, 0.89, 0.90);
        card.addGroupMetrics("Group A (Majority)", 0.935, 0.93, 0.91, 0.92);
        card.addGroupMetrics("Group B (Protected)", 0.885, 0.86, 0.84, 0.85);
        card.print();

        System.out.println("\n--- Bias Detection ---");
        Random rng = new Random(42);
        List<Boolean> predA = new ArrayList<>();
        List<Boolean> actualA = new ArrayList<>();
        List<Boolean> predB = new ArrayList<>();
        List<Boolean> actualB = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            actualA.add(rng.nextDouble() < 0.3);
            predA.add(rng.nextDouble() < (actualA.get(i) ? 0.92 : 0.08));
            actualB.add(rng.nextDouble() < 0.3);
            predB.add(rng.nextDouble() < (actualB.get(i) ? 0.85 : 0.15));
        }

        double dpDiff = BiasDetector.demographicParityDifference(predA, predB);
        double eodDiff = BiasDetector.equalOpportunityDifference(predA, actualA, predB, actualB);
        double diRatio = BiasDetector.disparateImpactRatio(predA, predB);

        System.out.printf("Demographic Parity Difference: %.4f %s%n",
                dpDiff, dpDiff < 0.1 ? "✓" : "⚠ EXCEEDS THRESHOLD");
        System.out.printf("Equal Opportunity Difference:  %.4f %s%n",
                eodDiff, eodDiff < 0.1 ? "✓" : "⚠ EXCEEDS THRESHOLD");
        System.out.printf("Disparate Impact Ratio:        %.4f %s%n",
                diRatio, diRatio >= 0.8 ? "✓ (satisfies 80%% rule)" : "⚠ (violates 80%% rule)");

        System.out.println("\n--- Audit Trail ---");
        AuditTrail audit = new AuditTrail();
        audit.log("REGISTER", "loan_approval", "1.0.0", "ds_lead", "Initial model registration");
        audit.log("PROMOTE", "loan_approval", "1.0.0", "ml_eng", "Promoted to staging");
        audit.log("VALIDATE", "loan_approval", "1.0.0", "qa_eng", "Validation passed");
        audit.log("PROMOTE", "loan_approval", "1.0.0", "ml_eng", "Promoted to production");
        audit.log("REGISTER", "loan_approval", "2.0.0", "ds_lead", "Updated training data");
        audit.log("PROMOTE", "loan_approval", "2.1.0", "ml_eng", "Bias mitigation applied");
        audit.printAll();
        System.out.printf("Audit trail integrity: %s%n", audit.verifyIntegrity() ? "INTACT ✓" : "TAMPERED ✗");
    }
}
```

### Expected Output

```
=== Model Governance & Compliance ===

Model Card: loan_approval v2.1.0
================================
Type:      Gradient Boosted Tree
Framework: XGBoost
Created:   2025-01-15T10:00:00
Intended Use: Automated loan approval decisions for personal loans up to $50,000

Limitations:
  - Not validated for commercial loans
  - Training data skewed towards urban populations
  - Model performance degrades for applicants with thin credit files

Per-Group Performance:
  Overall: {accuracy=0.923, precision=0.91, recall=0.89, f1=0.9, sample_size=1000.0}
  Group A (Majority): {accuracy=0.935, precision=0.93, recall=0.91, f1=0.92, sample_size=1000.0}
  Group B (Protected): {accuracy=0.885, precision=0.86, recall=0.84, f1=0.85, sample_size=1000.0}

--- Bias Detection ---
Demographic Parity Difference: 0.0400 ✓
Equal Opportunity Difference:  0.0598 ✓
Disparate Impact Ratio:        0.8830 ✓ (satisfies 80%% rule)

--- Audit Trail ---
Audit Trail:
  [2025-01-15T10:00:00] REGISTER | model=loan_approval v1.0.0 | user=ds_lead | detail=Initial model registration | hash=4bf8a5f3
  [2025-01-15T10:00:00] PROMOTE | model=loan_approval v1.0.0 | user=ml_eng | detail=Promoted to staging | hash=b5ab911d
  [2025-01-15T10:00:00] VALIDATE | model=loan_approval v1.0.0 | user=qa_eng | detail=Validation passed | hash=1f870259
  [2025-01-15T10:00:00] PROMOTE | model=loan_approval v1.0.0 | user=ml_eng | detail=Promoted to production | hash=e5ac1668
  [2025-01-15T10:00:00] REGISTER | model=loan_approval v2.0.0 | user=ds_lead | detail=Updated training data | hash=563738aa
  [2025-01-15T10:00:00] PROMOTE | model=loan_approval v2.1.0 | user=ml_eng | detail=Bias mitigation applied | hash=55550e35
Audit trail integrity: INTACT ✓
```

*(Determinism note: the lab stamps `Instant.now()` into the card and every audit entry, making the audit hashes different on every run. This walkthrough pins both timestamps to a fixed instant — the class signatures are unchanged — so the transcript above, including each `hash=`, reproduces byte-for-byte. The hashes differ from a plain lab run because the lab hashes the runtime timestamp; here the fixed clock makes them stable.)*

## Problem 2: Credit-Scoring Model Card for a Regulator — Company: American Express

### The Problem

A new credit-scoring model (v3.0.0, random forest) must be submitted to the regulator with per-group performance and fairness metrics. Group B (younger applicants) shows accuracy 0.861 vs 0.921 for Group A; the disparity ratio is 0.74. Write the governance response.

### Solution Walkthrough

1. **Document first, then fix.** The `ModelCard` gets the honest numbers: per-group metrics from `addGroupMetrics` with sample sizes — the regulator cares that Group B's 0.861 is based on a *small* sample, which the `sample_size` field makes explicit.
2. **Run the `BiasDetector` and read the ratio.** 0.74 violates the 80% rule (`⚠ (violates 80% rule)` in the lab's phrasing) — this is a *must-fix* finding, not a monitor-and-watch item: the positive rate for younger applicants is more than 25% lower than the majority group's.
3. **Trace the cause to data, not just the model.** The card's limitations already flag skewed training data; the response plan pairs data enrichment with model-side mitigation — the same sequence the lab's audit trail records ("REGISTER 2.0.0 — Updated training data", "PROMOTE 2.1.0 — Bias mitigation applied").
4. **Close the loop with the audit trail.** Every step — registering the fixed version, validation, promotion — goes into the hash chain, so the regulator can verify the timeline: finding, fix, re-measurement (new disparity ratio above 0.8), promotion. That chain (`verifyIntegrity` returning INTACT) is the artifact that makes the narrative auditable.

## Problem 3: Bias Detected in Production After Launch — Company: Chime

### The Problem

Three weeks after a new underwriting model ships, monitoring shows demographic parity difference at 0.14 (threshold 0.1) and equal opportunity difference at 0.12 — both red. The model card claimed the metrics passed at release. Walk the response.

### Solution Walkthrough

1. **Verify before acting.** Re-run `demographicParityDifference` and `equalOpportunityDifference` on the *serving logs*, not the offline simulation — confirm the red metrics aren't a data-pipeline artifact (e.g. missing attributes for one group inflating its negative rate), exactly as the lab recomputes from raw lists.
2. **Triage by severity and trend.** Two of three metrics red, ratio likely under 0.8: this is a production incident — treat it like Lab 08's CRITICAL drift alert: page the owning team, freeze the model from further promotion, and prepare a rollback candidate in the registry (Lab 03's `archiveVersion` / rollback path).
3. **Fix the loop with evidence.** The audit trail gets a `PROMOTE` entry for the rollback and later a `REGISTER` for the re-trained model — the regulator sees the incident and the response as one chain, not scattered emails.
4. **Update the card and the thresholds.** The new version's `ModelCard` documents the failure mode in `limitations`, and the fairness thresholds move from fixed 0.1 values to domain-reviewed limits with confidence intervals — because the lesson of the incident is that a single run-time number can't be the whole fairness story.
