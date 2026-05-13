package com.bank.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
    UUID eventId,
    UUID accountId,
    UUID userId,
    String accountType,
    BigDecimal initialBalance,
    Instant timestamp
) {
    public static AccountCreatedEvent create(UUID accountId, UUID userId, String accountType, BigDecimal balance) {
        return new AccountCreatedEvent(UUID.randomUUID(), accountId, userId, accountType, balance, Instant.now());
    }
}