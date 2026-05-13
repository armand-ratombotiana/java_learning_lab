package com.fraud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "fraud_alerts")
public class FraudAlert {

    @Id
    private String id;
    private UUID transactionId;
    private UUID accountId;
    private double riskScore;
    private String riskLevel;
    private List<String> detectedPatterns;
    private String reason;
    private String status;
    private Instant createdAt;
    private Instant resolvedAt;
    private String resolvedBy;
    private String resolution;

    public FraudAlert() {
        this.createdAt = Instant.now();
        this.status = "OPEN";
    }

    public FraudAlert(UUID transactionId, UUID accountId, double riskScore, String riskLevel, List<String> patterns, String reason) {
        this();
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.detectedPatterns = patterns;
        this.reason = reason;
    }

    public void resolve(String resolvedBy, String resolution) {
        this.status = "RESOLVED";
        this.resolvedBy = resolvedBy;
        this.resolution = resolution;
        this.resolvedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public List<String> getDetectedPatterns() { return detectedPatterns; }
    public void setDetectedPatterns(List<String> detectedPatterns) { this.detectedPatterns = detectedPatterns; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}