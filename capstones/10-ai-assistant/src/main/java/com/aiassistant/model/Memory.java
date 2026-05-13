package com.aiassistant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Document(collection = "memories")
public class Memory {

    @Id
    private String id;
    private String userId;
    private String content;
    private String memoryType;
    private List<Float> embedding;
    private double importance;
    private Instant createdAt;
    private Instant accessedAt;
    private int accessCount;

    public Memory() {
        this.createdAt = Instant.now();
        this.accessedAt = Instant.now();
        this.accessCount = 0;
    }

    public void access() {
        this.accessedAt = Instant.now();
        this.accessCount++;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMemoryType() { return memoryType; }
    public void setMemoryType(String memoryType) { this.memoryType = memoryType; }
    public List<Float> getEmbedding() { return embedding; }
    public void setEmbedding(List<Float> embedding) { this.embedding = embedding; }
    public double getImportance() { return importance; }
    public void setImportance(double importance) { this.importance = importance; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getAccessedAt() { return accessedAt; }
    public void setAccessedAt(Instant accessedAt) { this.accessedAt = accessedAt; }
    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }
}