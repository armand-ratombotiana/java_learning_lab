# Mock Interview: Records Deep Dive

**Interviewer:** "Walk me through how you would model a banking transaction using records."

**Candidate:** "I'd start with a record `Transaction` as the core — it's immutable and transparent:

```java
record Transaction(String id, BigDecimal amount, Instant timestamp, Type type) {
    enum Type { DEPOSIT, WITHDRAWAL, TRANSFER }
    Transaction {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");
    }
}
```

The compact constructor validates that amounts are positive. For grouping transactions, I'd use a local record within the service method:

```java
List<DailySummary> summarize(List<Transaction> txns) {
    record DailySummary(LocalDate date, BigDecimal total) {}
    ...
}
```

This keeps the code self-contained and avoids polluting the package namespace."

**Interviewer:** "How would you use this with JPA?"

**Candidate:** "I would NOT use `Transaction` as an entity — records are immutable and JPA requires mutable entities with no-arg constructors. Instead, I'd create a record DTO for projections:

```java
record TransactionSummary(String type, BigDecimal total) {}
```

And use it with Spring Data JPA's projection support. For embeddable value types like `Money(BigDecimal amount, String currency)`, Hibernate 6.2+ supports `@Embeddable` directly on records."
