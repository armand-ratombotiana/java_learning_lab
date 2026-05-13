package com.bank.fraud.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "fraud_alerts")
public class FraudAlert {

    @Id
    private String id;
    private UUID transactionId;
    private UUID accountId;
    private String riskLevel;
    private String reason;
    private String status;
    private Instant createdAt;
    private Instant resolvedAt;
    private String resolvedBy;

    public FraudAlert() {}

    public FraudAlert(UUID transactionId, UUID accountId, String riskLevel, String reason) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.riskLevel = riskLevel;
        this.reason = reason;
        this.status = "OPEN";
        this.createdAt = Instant.now();
    }

    public void resolve(String resolvedBy) {
        this.status = "RESOLVED";
        this.resolvedBy = resolvedBy;
        this.resolvedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
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
}