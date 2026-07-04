# Debugging Distributed Transactions

## Common Issues

### Hanging Transactions
```java
public class TransactionDebugger {
    public static List<Transaction> findHanging(Coordinator coord) {
        return coord.getTransactions().stream()
            .filter(tx -> tx.getState() == Transaction.State.PREPARED)
            .filter(tx -> tx.getAge() > TimeUnit.MINUTES.toMillis(5))
            .toList();
    }
}
```

### Compensation Failures
Log all compensation attempts and retry with exponential backoff.

### Inconsistent State
Compare transaction logs across participants to find mismatches.
