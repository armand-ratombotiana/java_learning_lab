# Problem Walkthrough: Production ML Architecture

## Problem 1: End-to-End Platform Simulation — Company: DoorDash

### Interview Scenario

> **Interviewer**: "We've built six components over the last year — ingestion, feature store, registry, serving, monitoring, CI/CD — and now we need one Java simulation that ties them together: three case-study platforms (fraud, recommendations, multi-tenant) where each component plays its role in one training-to-serving loop. The demo simulates 1,000 predictions per platform with a seeded RNG, prints architecture lines, alerts, and average metrics, and closes with the integrated component summary. Make it run clean and deterministic."
>
> **Candidate**: "The capstone walkthrough mirrors the lab's blueprint and every printed value below is captured from the compiled run — including the one 'alerts' line and the exact metric averages."

### The Problem

1. Assemble the six platform components: `DataIngestion` (source + format), `FeatureStore` (online/offline), `ModelRegistry` (versions, stages), `ServingInfrastructure` (replicas, max, target CPU), `MonitoringSystem` (latency/error/drift), and `CiCdPipeline` (repo + branch).
2. Build `MLPlatformBlueprint.simulate()`: print the architecture line, the data source, the scaling decision at 5,000 req/s, and the training-stage CI/CD line.
3. Simulate 1,000 predictions with `Random(42)`: latency `50 + |N(0,1)| * 30` recorded at 2.5× (capped at 500), errors at 2%, drift uniform in `[0.1, 0.3]` — then run alert checks and print the averages.
4. Run three case studies — real-time fraud (10K tps), batch recommendation (100M users), multi-tenant platform (50 teams) — each with its own component wiring.
5. Print the integrated architecture summary (seven components, end-to-end), and end the demo cleanly.
6. The transcript must be reproducible byte-for-byte.

### Solution Walkthrough

1. **Compose components, don't hardcode the platform.** Each case study is a `MLPlatformBlueprint` constructed from six independent components — `new DataIngestion("Kafka: transactions", "Avro")`, a `FeatureStore`, a `ModelRegistry`, `ServingInfrastructure(5, 20, 60)`, a `MonitoringSystem`, and `CiCdPipeline("github.com/company/fraud-ml", "main")`. The architecture is wiring, which is exactly how the real platform is assembled: teams bring their own sources and thresholds, the platform provides the primitives.
2. **Show the loop, not the code.** `simulate()` prints the five observable slices: `Architecture:` (the declared SLA, e.g. "Real-time Fraud Detection (10K tps, P99<50ms)"), `Data:` (ingestion verdict), `Serving:` (the scaling decision at 5,000 req/s — fraud and rec platforms differ in *starting* replicas, 5 vs 3, and the multi-tenant platform *scales down* from 20 to 5, proving the rule handles both directions), and `CI/CD:` (the pipeline's training stage line, `✓ Model training: accuracy=0.947`).
3. **Simulate predictions with one seeded stream.** All three platforms share the same `Random(42)` draws because each `simulate()` seeds its own RNG — the latency/error/drift sequences are identical per platform, so the three `Avg metrics` lines match. The recordings: `latency_p99` is the draw `50 + |gauss| * 30` scaled by 2.5 and capped at 500 (simulating p99 amplification), `error_rate` is a 2% Bernoulli, `drift_psi` is uniform in `[0.1, 0.3]` — deliberately under the 0.25 alert threshold on average.
4. **Alert on averages, not samples.** `checkAlerts()` computes per-metric means over the 1,000 recordings and compares against thresholds: latency > 200, error rate > 1%, drift PSI > 0.25. In the seeded run only the error rate trips — the observed average is 0.012 (1.2% errors, a bit under the 2% true rate), so the printed alert is `⚠ Alerts: High error rate`. The design point: alerts keyed on *aggregates* over a window, the same shape as Lab 08's monitor report.
5. **Read the averages honestly.** `Avg metrics: {error_rate=0.012, latency_p99=184.17, drift_psi=0.2012}` — the latency mean of 184ms is below the 200ms alert line (p99 would be higher), and the drift mean of 0.201 is below 0.25 — only error rate crosses. The map prints in `ConcurrentHashMap` iteration order (`error_rate` first); stable on this JDK for this key set, though not contractually guaranteed — which is why the walkthrough's transcript, not the map, is the golden artifact.
6. **Close with the integrated view.** The `=== Architecture Summary ===` block renders the seven-layer stack — ingestion ← Kafka/S3/Event Hub, feature store ← Redis + S3, registry ← versions/stages/lineage, serving ← K8s with HPA + Istio, monitoring ← Prometheus + drift detection, CI/CD ← GitHub Actions + ArgoCD, governance ← model cards + audit trails — the capstone's point: every earlier lab is one component of this stack.
7. **Verify against the compiled run.** The Expected Output below is the exact stdout of the walkthrough class, confirmed identical across two consecutive runs. (The lab's closing line prints a celebration emoji; the walkthrough ends with plain text, keeping the transcript emoji-free.)

### Code

```java
package com.mlops.lab15;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ProductionMLArchitectureWalkthrough {

    static class DataIngestion {
        final String source;
        final String format;
        DataIngestion(String source, String format) { this.source = source; this.format = format; }
        String ingest() { return String.format("Ingested from %s (%s)", source, format); }
    }

    static class FeatureStore {
        final Map<String, Map<String, Object>> online = new ConcurrentHashMap<>();
        final Map<String, List<Map<String, Object>>> offline = new ConcurrentHashMap<>();

        void writeOnline(String key, Map<String, Object> features) { online.put(key, features); }
        Map<String, Object> readOnline(String key) { return online.getOrDefault(key, Map.of()); }
        void writeOffline(String partition, Map<String, Object> features) {
            offline.computeIfAbsent(partition, k -> new ArrayList<>()).add(features);
        }
        List<Map<String, Object>> readOffline(String partition) {
            return offline.getOrDefault(partition, List.of());
        }
    }

    static class ModelRegistry {
        static class ModelRecord {
            final String name; final int version; final String stage; final double accuracy;
            final Instant deployedAt; final String artifactUri;
            ModelRecord(String name, int v, String stage, double acc, String uri) {
                this.name = name; this.version = v; this.stage = stage;
                this.accuracy = acc; this.deployedAt = Instant.now(); this.artifactUri = uri;
            }
        }
        final List<ModelRecord> records = new ArrayList<>();
        void register(String name, int version, double accuracy, String uri) {
            records.add(new ModelRecord(name, version, "Staging", accuracy, uri));
        }
        void promote(String name, int version) {
            records.stream().filter(r -> r.name.equals(name) && r.version == version)
                    .forEach(r -> { });
        }
        Optional<ModelRecord> getProductionModel(String name) {
            return records.stream().filter(r -> r.name.equals(name) && "Production".equals(r.stage)).findFirst();
        }
    }

    static class ServingInfrastructure {
        final int replicas; final int maxReplicas; final int targetCpuUtilization;
        ServingInfrastructure(int replicas, int maxReplicas, int targetCpu) {
            this.replicas = replicas; this.maxReplicas = maxReplicas; this.targetCpuUtilization = targetCpu;
        }
        String scale(int currentLoad) {
            int target = Math.min(maxReplicas, Math.max(1, currentLoad / 1000));
            return String.format("Scaling from %d to %d replicas (load=%d req/s)", replicas, target, currentLoad);
        }
    }

    static class MonitoringSystem {
        final Map<String, List<Double>> metrics = new ConcurrentHashMap<>();
        void record(String metric, double value) {
            metrics.computeIfAbsent(metric, k -> new CopyOnWriteArrayList<>()).add(value);
        }
        Map<String, Double> getAverages() {
            Map<String, Double> avgs = new LinkedHashMap<>();
            metrics.forEach((k, v) -> avgs.put(k, v.stream().mapToDouble(d -> d).average().orElse(0)));
            return avgs;
        }
        List<String> checkAlerts() {
            List<String> alerts = new ArrayList<>();
            Map<String, Double> avgs = getAverages();
            if (avgs.getOrDefault("latency_p99", 0.0) > 200) alerts.add("High latency");
            if (avgs.getOrDefault("error_rate", 0.0) > 0.01) alerts.add("High error rate");
            if (avgs.getOrDefault("drift_psi", 0.0) > 0.25) alerts.add("Data drift detected");
            return alerts;
        }
    }

    static class CiCdPipeline {
        final String repo; final String branch;
        CiCdPipeline(String repo, String branch) { this.repo = repo; this.branch = branch; }
        List<String> run() {
            return List.of(
                    "✓ Code checkout: " + repo,
                    "✓ Data validation: passed",
                    "✓ Model training: accuracy=0.947",
                    "✓ Model evaluation: passed (champion=0.935)",
                    "✓ Deploy to staging",
                    "✓ Integration tests: passed",
                    "⏸ Manual approval required for production"
            );
        }
    }

    static class MLPlatformBlueprint {
        final DataIngestion ingestion;
        final FeatureStore featureStore;
        final ModelRegistry registry;
        final ServingInfrastructure serving;
        final MonitoringSystem monitoring;
        final CiCdPipeline cicd;
        final String description;

        MLPlatformBlueprint(String desc, DataIngestion ingestion, FeatureStore fs,
                             ModelRegistry reg, ServingInfrastructure srv,
                             MonitoringSystem mon, CiCdPipeline ci) {
            this.description = desc;
            this.ingestion = ingestion;
            this.featureStore = fs;
            this.registry = reg;
            this.serving = srv;
            this.monitoring = mon;
            this.cicd = ci;
        }

        void simulate() {
            System.out.println("Architecture: " + description);
            System.out.println("  Data: " + ingestion.ingest());
            System.out.println("  Serving: " + serving.scale(5000));
            System.out.println("  CI/CD: " + cicd.run().get(2));

            Random rng = new Random(42);
            for (int i = 0; i < 1000; i++) {
                double latency = 50 + Math.abs(rng.nextGaussian()) * 30;
                boolean error = rng.nextDouble() < 0.02;
                double drift = 0.1 + rng.nextDouble() * 0.2;
                monitoring.record("latency_p99", Math.min(500, latency * 2.5));
                monitoring.record("error_rate", error ? 1.0 : 0.0);
                monitoring.record("drift_psi", drift);
            }
            var alerts = monitoring.checkAlerts();
            if (!alerts.isEmpty()) {
                System.out.println("  ⚠ Alerts: " + String.join(", ", alerts));
            }
            System.out.println("  Avg metrics: " + monitoring.getAverages());
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Production ML Architecture ===\n");

        MLPlatformBlueprint fraudPlatform = new MLPlatformBlueprint(
                "Real-time Fraud Detection (10K tps, P99<50ms)",
                new DataIngestion("Kafka: transactions", "Avro"),
                new FeatureStore(),
                new ModelRegistry(),
                new ServingInfrastructure(5, 20, 60),
                new MonitoringSystem(),
                new CiCdPipeline("github.com/company/fraud-ml", "main")
        );

        MLPlatformBlueprint recPlatform = new MLPlatformBlueprint(
                "Batch Recommendation Engine (100M users, daily)",
                new DataIngestion("S3: user_events", "Parquet"),
                new FeatureStore(),
                new ModelRegistry(),
                new ServingInfrastructure(3, 10, 70),
                new MonitoringSystem(),
                new CiCdPipeline("github.com/company/rec-ml", "main")
        );

        MLPlatformBlueprint multiPlatform = new MLPlatformBlueprint(
                "Multi-tenant ML Platform (50 teams, 500 models)",
                new DataIngestion("Event Hub + S3", "Delta Lake"),
                new FeatureStore(),
                new ModelRegistry(),
                new ServingInfrastructure(20, 100, 50),
                new MonitoringSystem(),
                new CiCdPipeline("github.com/company/ml-platform", "main")
        );

        System.out.println("=== Case Study 1 ===");
        fraudPlatform.simulate();

        System.out.println("=== Case Study 2 ===");
        recPlatform.simulate();

        System.out.println("=== Case Study 3 ===");
        multiPlatform.simulate();

        System.out.println("=== Architecture Summary ===");
        System.out.printf("""
                Components integrated:
                ┌──────────────────────┐
                │ 1. Data Ingestion    │ ← Kafka / S3 / Event Hub
                │ 2. Feature Store     │ ← Online (Redis) + Offline (S3)
                │ 3. Model Registry    │ ← Versions, Stages, Lineage
                │ 4. Serving Infra     │ ← K8s with HPA, Istio
                │ 5. Monitoring        │ ← Prometheus + Drift Detection
                │ 6. CI/CD             │ ← GitHub Actions + ArgoCD
                │ 7. Governance        │ ← Model Cards, Audit Trails
                └──────────────────────┘
                """);

        System.out.println("MLOps Academy completed.");
    }
}
```

### Expected Output

```
=== Production ML Architecture ===

=== Case Study 1 ===
Architecture: Real-time Fraud Detection (10K tps, P99<50ms)
  Data: Ingested from Kafka: transactions (Avro)
  Serving: Scaling from 5 to 5 replicas (load=5000 req/s)
  CI/CD: ✓ Model training: accuracy=0.947
  ⚠ Alerts: High error rate
  Avg metrics: {error_rate=0.012, latency_p99=184.16516737784085, drift_psi=0.20122142265534781}

=== Case Study 2 ===
Architecture: Batch Recommendation Engine (100M users, daily)
  Data: Ingested from S3: user_events (Parquet)
  Serving: Scaling from 3 to 5 replicas (load=5000 req/s)
  CI/CD: ✓ Model training: accuracy=0.947
  ⚠ Alerts: High error rate
  Avg metrics: {error_rate=0.012, latency_p99=184.16516737784085, drift_psi=0.20122142265534781}

=== Case Study 3 ===
Architecture: Multi-tenant ML Platform (50 teams, 500 models)
  Data: Ingested from Event Hub + S3 (Delta Lake)
  Serving: Scaling from 20 to 5 replicas (load=5000 req/s)
  CI/CD: ✓ Model training: accuracy=0.947
  ⚠ Alerts: High error rate
  Avg metrics: {error_rate=0.012, latency_p99=184.16516737784085, drift_psi=0.20122142265534781}

=== Architecture Summary ===
Components integrated:
┌──────────────────────┐
│ 1. Data Ingestion    │ ← Kafka / S3 / Event Hub
│ 2. Feature Store     │ ← Online (Redis) + Offline (S3)
│ 3. Model Registry    │ ← Versions, Stages, Lineage
│ 4. Serving Infra     │ ← K8s with HPA, Istio
│ 5. Monitoring        │ ← Prometheus + Drift Detection
│ 6. CI/CD             │ ← GitHub Actions + ArgoCD
│ 7. Governance        │ ← Model Cards, Audit Trails
└──────────────────────┘
MLOps Academy completed.
```

*(Notes: the `Avg metrics` map prints in `ConcurrentHashMap` iteration order — stable on the JDK used here, not contractually guaranteed; the three case studies share identical metric lines because each `simulate()` seeds its own `Random(42)`; and the multi-tenant platform scales *down* from 20 to 5 replicas at the same load — scaling logic handles both directions.)*

## Problem 2: From Fraud Replay to Production — Company: Robinhood

### The Problem

The fraud platform's `simulate()` runs with a model that never changes — the registry registers models as "Staging" but `promote()` is a stub. Design the promotion path so the blueprint's serving layer actually serves what the registry says.

### Solution Walkthrough

1. **Implement the registry contract.** `ModelRegistry.register` creates the record in `"Staging"`; `promote` must find the matching record and set its stage to `"Production"` — the stub's `forEach(r -> { })` is the gap. `getProductionModel` then returns the promoted version, and `Optional` handles the no-production-model case explicitly.
2. **Wire the loop end-to-end.** The blueprint's simulation should mirror Lab 03's flow: `register("fraud_model", 2, 0.961, "s3://models/fraud/2/")` → evaluation gate passes → `promote("fraud_model", 2)` → `getProductionModel` returns version 2 → the serving layer logs which version it serves. Version 1 stays in the registry as archived history — the audit trail (Lab 11) records the promotion.
3. **Gate promotion on the numbers.** The CI/CD line `✓ Model evaluation: passed (champion=0.935)` is the gate narrative: a candidate at 0.961 beats the champion at 0.935, so promotion is allowed; the walkthrough's `CiCdPipeline.run()` list is the seven-stage gate checklist — validation (Lab 09), training, evaluation, staging, integration, manual approval.
4. **Make the platform observable.** After promotion, `MonitoringSystem` records per-version metrics (`drift_psi` and `error_rate` keyed by version), so the alert line becomes version-aware — `High error rate` on version 2 triggers the rollback path: `promote("fraud_model", 1)` restores the champion, and the registry's record history answers "what was serving at 14:02?" for the incident review.

## Problem 3: The Multi-Tenant Contract — Company: Klarna

### The Problem

The multi-tenant platform serves 50 teams and 500 models from one blueprint, but `FeatureStore.readOnline` returns `Map.of()` for a missing key and `ServingInfrastructure.scale` ignores the declared `targetCpuUtilization`. Two tenants hit missing-feature errors in production. What does the platform contract need?

### Solution Walkthrough

1. **Make missing features explicit.** `readOnline` silently returning an empty map is the bug — a model trained on `avg_transaction_amount` silently gets a default 0, which changes its behavior (a null-ratio check in Lab 09 would have caught the missing feature at training; at serving it surfaces as wrong predictions). The contract: `readOnline` returns an `Optional` — missing features fail the request with a 400, or fall back through an explicit policy, never a silent zero.
2. **Honor the declared scale target.** The blueprint declares `ServingInfrastructure(20, 100, 50)` — 50% CPU target — but `scale()` computes replicas from raw load (`load / 1000`) and ignores the target. Real autoscaling (Lab 06's HPA) uses `targetCpuUtilization` as the divisor: `replicas = ceil(load / (capacityPerReplica * targetCpu/100))`; the blueprint's naive rule is the teaching gap — the config the platform advertises must be the config the platform honors.
3. **Isolate tenants, share primitives.** The feature store's `ConcurrentHashMap` gives every team the same namespace — tenant A reading tenant B's `user_embeddings` key is a data-leak class of bug. The platform contract prefixes keys per tenant (the registry already scopes by model name) and enforces it in `readOnline`/`writeOnline` rather than by convention.
4. **Test the contract like the stack it is.** The capstone's golden transcript covers one happy path; the multi-tenant contract needs the negative tests every component lab used: missing key returns empty `Optional` and a 400; promotion without a production record returns empty and the serving layer refuses to serve; scale(0) returns 1 replica, never 0. Those assertions are the platform's real acceptance criteria.
