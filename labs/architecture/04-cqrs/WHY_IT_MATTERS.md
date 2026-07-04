# Why CQRS Matters

## Benefits
- **Independent optimization** - Scale reads and writes separately
- **Flexible query models** - Multiple read models for different use cases
- **Improved security** - Different access controls for commands vs queries
- **Better maintainability** - Simpler models each focused on their concern
- **Event sourcing friendly** - Natural fit with event-driven architectures

## Example: Read/Write Separation
```java
// Write side - validates business rules
@Service
public class AccountCommandService {
    public void transferMoney(TransferCommand cmd) {
        Account source = accountRepository.findById(cmd.fromId()).get();
        Account target = accountRepository.findById(cmd.toId()).get();
        source.withdraw(cmd.amount());
        target.deposit(cmd.amount());
        // Both consistency and business rules enforced
    }
}

// Read side - optimized for display
@Service
public class AccountQueryService {
    public AccountSummary getAccountSummary(String accountId) {
        // Denormalized, pre-computed view
        return viewRepository.findSummaryById(accountId);
    }

    public List<TransactionView> getRecentTransactions(String accountId) {
        // Optimized for the specific UI view
        return transactionViewRepository
            .findTop10ByAccountIdOrderByDateDesc(accountId);
    }
}
```

## When the Trade-off Pays Off
- Complex systems with different read/write needs
- High-traffic systems requiring independent scaling
- Systems with complex security requirements
- Domains benefiting from event sourcing

## Real-World Usage
- Banking systems (separate transaction processing from balance queries)
- E-commerce (order placement vs product browsing)
- Social media (post creation vs feed reading)
- IoT (device commands vs telemetry queries)
