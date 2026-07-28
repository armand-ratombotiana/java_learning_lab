package com.mlops.lab03;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Model Registry & Versioning — Lab 03.
 * <p>
 * Demonstrates model version management, stage transitions (staging/production),
 * model lineage tracking, and champion/challenger patterns.
 */
public class ModelRegistryLab {

    /** Stages a model version can occupy. */
    enum Stage { NONE, STAGING, PRODUCTION, ARCHIVED }

    /** Represents a single model version with metadata and lineage. */
    static class ModelVersion {
        final int version;
        final String modelName;
        final String runId;
        final Map<String, Double> metrics;
        final Map<String, String> parameters;
        final String artifactPath;
        final Instant createdAt;
        Stage stage;
        String description;

        ModelVersion(int version, String modelName, String runId,
                      Map<String, Double> metrics, Map<String, String> parameters,
                      String artifactPath) {
            this.version = version;
            this.modelName = modelName;
            this.runId = runId;
            this.metrics = metrics;
            this.parameters = parameters;
            this.artifactPath = artifactPath;
            this.createdAt = Instant.now();
            this.stage = Stage.NONE;
        }

        void transitionTo(Stage newStage) {
            System.out.printf("  Model %s v%d: %s → %s%n",
                    modelName, version, stage, newStage);
            this.stage = newStage;
        }

        @Override
        public String toString() {
            return String.format("v%d [%s] acc=%.4f run=%s created=%s",
                    version, stage, metrics.getOrDefault("accuracy", 0.0),
                    runId.substring(0, 8), createdAt.toString().substring(0, 19));
        }
    }

    /** Registry that manages model versions and their lifecycle. */
    static class ModelRegistry {
        private final Map<String, List<ModelVersion>> models = new ConcurrentHashMap<>();
        private final AtomicInteger versionCounter = new AtomicInteger(0);

        ModelVersion registerVersion(String modelName, String runId,
                                      Map<String, Double> metrics,
                                      Map<String, String> parameters,
                                      String artifactPath) {
            int ver = versionCounter.incrementAndGet();
            ModelVersion mv = new ModelVersion(ver, modelName, runId,
                    metrics, parameters, artifactPath);
            models.computeIfAbsent(modelName, k -> new ArrayList<>()).add(mv);
            System.out.printf("Registered %s v%d (accuracy=%.4f)%n",
                    modelName, ver, metrics.getOrDefault("accuracy", 0.0));
            return mv;
        }

        ModelVersion getVersion(String modelName, int version) {
            List<ModelVersion> versions = models.get(modelName);
            if (versions == null) return null;
            return versions.stream()
                    .filter(v -> v.version == version)
                    .findFirst().orElse(null);
        }

        ModelVersion getProductionModel(String modelName) {
            List<ModelVersion> versions = models.get(modelName);
            if (versions == null) return null;
            return versions.stream()
                    .filter(v -> v.stage == Stage.PRODUCTION)
                    .findFirst().orElse(null);
        }

        ModelVersion getStagingModel(String modelName) {
            List<ModelVersion> versions = models.get(modelName);
            if (versions == null) return null;
            return versions.stream()
                    .filter(v -> v.stage == Stage.STAGING)
                    .findFirst().orElse(null);
        }

        void promoteToStaging(String modelName, int version) {
            ModelVersion mv = getVersion(modelName, version);
            if (mv == null) throw new IllegalArgumentException("Version not found");
            mv.transitionTo(Stage.STAGING);
        }

        void promoteToProduction(String modelName, int version) {
            ModelVersion mv = getVersion(modelName, version);
            if (mv == null) throw new IllegalArgumentException("Version not found");
            // Archive current production model
            ModelVersion current = getProductionModel(modelName);
            if (current != null && current.version != version) {
                current.transitionTo(Stage.ARCHIVED);
            }
            mv.transitionTo(Stage.PRODUCTION);
        }

        void archiveVersion(String modelName, int version) {
            ModelVersion mv = getVersion(modelName, version);
            if (mv != null) mv.transitionTo(Stage.ARCHIVED);
        }

        List<ModelVersion> listVersions(String modelName) {
            return models.getOrDefault(modelName, List.of());
        }

        void printRegistry(String modelName) {
            System.out.printf("\nModel Registry: %s%n", modelName);
            System.out.println("----------------------------");
            List<ModelVersion> versions = listVersions(modelName);
            for (ModelVersion mv : versions) {
                String tag = mv.stage == Stage.PRODUCTION ? " ★ CHAMPION" :
                        mv.stage == Stage.STAGING ? " ☆ CHALLENGER" : "";
                System.out.printf("  %s%s%n", mv, tag);
            }
        }
    }

    public static void main(String[] args) {
        ModelRegistry registry = new ModelRegistry();

        System.out.println("=== Model Registry & Versioning ===\n");

        // Simulate registering several model versions
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

        // Lifecycle: v1 → prod, v2 → staging, v3 → staging, v3 → prod (champion), v4 → challenger
        System.out.println("\n--- Lifecycle Transitions ---");
        registry.promoteToStaging("fraud_detector", 1);
        registry.promoteToProduction("fraud_detector", 1);
        registry.promoteToStaging("fraud_detector", 2);
        registry.promoteToStaging("fraud_detector", 3);
        registry.promoteToProduction("fraud_detector", 3);  // v1 auto-archived
        registry.promoteToStaging("fraud_detector", 4);      // new challenger
        registry.archiveVersion("fraud_detector", 2);        // manually archive v2

        registry.printRegistry("fraud_detector");

        ModelVersion champion = registry.getProductionModel("fraud_detector");
        System.out.printf("%nChampion: %s%n", champion);
        System.out.printf("Challenger: %s%n", registry.getStagingModel("fraud_detector"));
    }
}
