package com.mlplatform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Document(collection = "models")
public class ModelVersion {

    @Id
    private String id;
    private String name;
    private int version;
    private String experimentId;
    private String modelType;
    private String artifactUri;
    private List<String> inputFeatures;
    private String outputType;
    private String status;
    private Instant createdAt;
    private String deployedAt;
    private long predictionCount;
    private Map<String, Object> metadata;

    public ModelVersion() {
        this.createdAt = Instant.now();
        this.status = "REGISTERED";
        this.version = 1;
    }

    public void deploy() {
        this.status = "DEPLOYED";
        this.deployedAt = Instant.now().toString();
    }

    public void archive() {
        this.status = "ARCHIVED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getExperimentId() { return experimentId; }
    public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public String getArtifactUri() { return artifactUri; }
    public void setArtifactUri(String artifactUri) { this.artifactUri = artifactUri; }
    public List<String> getInputFeatures() { return inputFeatures; }
    public void setInputFeatures(List<String> inputFeatures) { this.inputFeatures = inputFeatures; }
    public String getOutputType() { return outputType; }
    public void setOutputType(String outputType) { this.outputType = outputType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getDeployedAt() { return deployedAt; }
    public void setDeployedAt(String deployedAt) { this.deployedAt = deployedAt; }
    public long getPredictionCount() { return predictionCount; }
    public void setPredictionCount(long predictionCount) { this.predictionCount = predictionCount; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    private Map<String, Object> metadata;
}