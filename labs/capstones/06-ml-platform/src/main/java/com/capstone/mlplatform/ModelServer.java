package com.capstone.mlplatform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModelServer {
    private final ModelRegistry registry;
    private final Map<String, ServingInstance> instances = new ConcurrentHashMap<>();

    public record ServingInstance(String modelId, String endpoint, int port, boolean active, long startedAt) {}
    public record PredictionResult(String modelId, String prediction, double confidence, long latencyMs) {}

    public ModelServer(ModelRegistry registry) { this.registry = registry; }

    public ServingInstance deploy(String modelName) {
        var modelOpt = registry.getLatestProductionModel(modelName);
        if (modelOpt.isEmpty()) throw new IllegalStateException("No production model for: " + modelName);
        ModelRegistry.ModelVersion mv = modelOpt.get();
        int port = 9000 + instances.size();
        ServingInstance instance = new ServingInstance(mv.modelId(),
            "/predict/" + mv.modelName(), port, true, System.currentTimeMillis());
        instances.put(mv.modelId(), instance);
        return instance;
    }

    public void undeploy(String modelId) {
        ServingInstance inst = instances.get(modelId);
        if (inst != null) instances.put(modelId, new ServingInstance(modelId, inst.endpoint(),
            inst.port(), false, inst.startedAt()));
    }

    public PredictionResult predict(String modelId, Map<String, Object> features) {
        ServingInstance inst = instances.get(modelId);
        if (inst == null || !inst.active()) throw new IllegalStateException("Model not deployed: " + modelId);
        long start = System.nanoTime();
        String prediction = mockPredict(features);
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        return new PredictionResult(modelId, prediction, 0.85 + Math.random() * 0.15, elapsed);
    }

    public Optional<ServingInstance> getInstance(String modelId) {
        return Optional.ofNullable(instances.get(modelId));
    }

    public List<ServingInstance> getActiveInstances() {
        return instances.values().stream().filter(ServingInstance::active).toList();
    }

    public int activeCount() { return (int) instances.values().stream().filter(ServingInstance::active).count(); }

    public void clear() { instances.clear(); }

    private String mockPredict(Map<String, Object> features) {
        return "class_" + Math.abs(features.hashCode() % 3);
    }
}
