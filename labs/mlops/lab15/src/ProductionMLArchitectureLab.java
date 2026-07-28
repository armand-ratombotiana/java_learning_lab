package com.mlops.lab15;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Production ML Architecture — Lab 15.
 * <p>
 * Models an end-to-end production ML platform architecture integrating concepts
 * from all previous labs: orchestration, tracking, registry, serving, monitoring,
 * and governance. Includes case study simulations for fraud detection and
 * recommendation systems.
 */
public class ProductionMLArchitectureLab {

    // ── Core Architecture Components ──

    /** Data ingestion component. */
    static class DataIngestion {
        final String source;
        final String format;
        DataIngestion(String source, String format) { this.source = source; this.format = format; }
        String ingest() { return String.format("Ingested from %s (%s)", source, format); }
    }

    /** Feature store component (online and offline). */
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

    /** Model registry component. */
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
                    .forEach(r -> { /* promote logic here */ });
        }
        Optional<ModelRecord> getProductionModel(String name) {
            return records.stream().filter(r -> r.name.equals(name) && "Production".equals(r.stage)).findFirst();
        }
    }

    /** Serving infrastructure component. */
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

    /** Monitoring component. */
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

    /** CI/CD pipeline component. */
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

    // ── Architecture Blueprint ──

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

            // Simulate predictions
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

        // Case Study 1: Real-time Fraud Detection
        MLPlatformBlueprint fraudPlatform = new MLPlatformBlueprint(
                "Real-time Fraud Detection (10K tps, P99<50ms)",
                new DataIngestion("Kafka: transactions", "Avro"),
                new FeatureStore(),
                new ModelRegistry(),
                new ServingInfrastructure(5, 20, 60),
                new MonitoringSystem(),
                new CiCdPipeline("github.com/company/fraud-ml", "main")
        );

        // Case Study 2: Batch Recommendation
        MLPlatformBlueprint recPlatform = new MLPlatformBlueprint(
                "Batch Recommendation Engine (100M users, daily)",
                new DataIngestion("S3: user_events", "Parquet"),
                new FeatureStore(),
                new ModelRegistry(),
                new ServingInfrastructure(3, 10, 70),
                new MonitoringSystem(),
                new CiCdPipeline("github.com/company/rec-ml", "main")
        );

        // Case Study 3: Multi-tenant ML Platform
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

        // Architecture Summary
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

        System.out.println("MLOps Academy completed! 🎉");
    }
}
