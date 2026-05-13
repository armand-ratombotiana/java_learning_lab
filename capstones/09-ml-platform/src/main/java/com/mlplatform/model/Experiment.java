package com.mlplatform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Document(collection = "experiments")
public class Experiment {

    @Id
    private String id;
    private String name;
    private String description;
    private String status;
    private Map<String, String> hyperparameters;
    private Map<String, Double> metrics;
    private String modelArtifactUri;
    private String trainingDataset;
    private Instant createdAt;
    private Instant completedAt;
    private long durationMs;
    private String userId;

    public Experiment() {
        this.createdAt = Instant.now();
        this.status = "RUNNING";
    }

    public void complete(Map<String, Double> finalMetrics) {
        this.status = "COMPLETED";
        this.metrics = finalMetrics;
        this.completedAt = Instant.now();
        this.durationMs = completedAt.toEpochMilli() - createdAt.toEpochMilli();
    }

    public void fail(String reason) {
        this.status = "FAILED";
        this.completedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, String> getHyperparameters() { return hyperparameters; }
    public void setHyperparameters(Map<String, String> hyperparameters) { this.hyperparameters = hyperparameters; }
    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }
    public String getModelArtifactUri() { return modelArtifactUri; }
    public void setModelArtifactUri(String modelArtifactUri) { this.modelArtifactUri = modelArtifactUri; }
    public String getTrainingDataset() { return trainingDataset; }
    public void setTrainingDataset(String trainingDataset) { this.trainingDataset = trainingDataset; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}