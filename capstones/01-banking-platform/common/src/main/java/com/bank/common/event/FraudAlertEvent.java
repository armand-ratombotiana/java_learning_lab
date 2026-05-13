package com.bank.common.event;

import java.time.Instant;
import java.util.UUID;

public record FraudAlertEvent(
    UUID eventId,
    UUID transactionId,
    UUID accountId,
    String riskLevel,
    String reason,
    Instant timestamp
) {
    public static FraudAlertEvent create(UUID transactionId, UUID accountId, String riskLevel, String reason) {
        return new FraudAlertEvent(UUID.randomUUID(), transactionId, accountId, riskLevel, reason, Instant.now());
    }
}