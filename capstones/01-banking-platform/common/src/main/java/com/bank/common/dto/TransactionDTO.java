package com.bank.common.dto;

import java.time.Instant;
import java.util.UUID;

public record TransactionDTO(
    UUID id,
    UUID fromAccountId,
    UUID toAccountId,
    java.math.BigDecimal amount,
    String currency,
    String status,
    Instant createdAt,
    Instant completedAt
) {}