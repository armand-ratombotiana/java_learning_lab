package com.capstone.mlplatform;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ModelRegistry {
    private final Map<String, ModelVersion> models = new ConcurrentHashMap<>();
    private final Map<String, List<ModelVersion>> modelHistory = new ConcurrentHashMap<>();
    private final AtomicLong versionSeq = new AtomicLong(0);

    public record ModelVersion(String modelId, String modelName, String version,
                                String artifactPath, ModelStatus status,
                                Map<String, Double> metrics, Instant createdAt) {
        public ModelVersion {
            metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
        }
    }

    public enum ModelStatus { STAGING, PRODUCTION, ARCHIVED, FAILED }

    public ModelVersion register(String modelName, String artifactPath, Map<String, Double> metrics) {
        String modelId = "model-" + modelName.toLowerCase().replaceAll("\\s+", "-") + "-" + versionSeq.incrementAndGet();
        String version = "v" + versionSeq.incrementAndGet();
        ModelVersion mv = new ModelVersion(modelId, modelName, version, artifactPath,
            ModelStatus.STAGING, metrics, Instant.now());
        models.put(modelId, mv);
        modelHistory.computeIfAbsent(modelName, k -> new ArrayList<>()).add(mv);
        return mv;
    }

    public ModelVersion promoteToProduction(String modelId) {
        ModelVersion mv = models.get(modelId);
        if (mv == null) throw new IllegalArgumentException("Model not found: " + modelId);
        ModelVersion promoted = new ModelVersion(modelId, mv.modelName(), mv.version(),
            mv.artifactPath(), ModelStatus.PRODUCTION, mv.metrics(), mv.createdAt());
        models.put(modelId, promoted);
        return promoted;
    }

    public ModelVersion archiveModel(String modelId) {
        ModelVersion mv = models.get(modelId);
        if (mv == null) throw new IllegalArgumentException("Model not found: " + modelId);
        ModelVersion archived = new ModelVersion(modelId, mv.modelName(), mv.version(),
            mv.artifactPath(), ModelStatus.ARCHIVED, mv.metrics(), mv.createdAt());
        models.put(modelId, archived);
        return archived;
    }

    public Optional<ModelVersion> getModel(String modelId) {
        return Optional.ofNullable(models.get(modelId));
    }

    public Optional<ModelVersion> getLatestProductionModel(String modelName) {
        return modelHistory.getOrDefault(modelName, List.of()).stream()
            .filter(m -> m.status() == ModelStatus.PRODUCTION)
            .max(Comparator.comparing(ModelVersion::createdAt));
    }

    public List<ModelVersion> getModelHistory(String modelName) {
        return List.copyOf(modelHistory.getOrDefault(modelName, List.of()));
    }

    public List<ModelVersion> getAllModels() { return List.copyOf(models.values()); }
    public int size() { return models.size(); }
    public void clear() { models.clear(); modelHistory.clear(); versionSeq.set(0); }
}
