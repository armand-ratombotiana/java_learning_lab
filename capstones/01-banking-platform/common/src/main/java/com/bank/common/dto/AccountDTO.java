package com.bank.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDTO(
    UUID id,
    UUID userId,
    String accountNumber,
    String accountType,
    BigDecimal balance,
    String currency,
    String status
) {}