package com.bank.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionEvent(
    UUID eventId,
    UUID transactionId,
    UUID fromAccountId,
    UUID toAccountId,
    BigDecimal amount,
    String currency,
    Instant timestamp
) {
    public static TransactionEvent create(UUID transactionId, UUID from, UUID to, BigDecimal amount, String currency) {
        return new TransactionEvent(UUID.randomUUID(), transactionId, from, to, amount, currency, Instant.now());
    }
}