# Refactoring to Event Sourcing

## CRUD to Event Sourcing Migration

### Step 1: Identify Aggregate
```java
// BEFORE: CRUD entity
@Entity
public class Account {
    private Long id;
    private String owner;
    private BigDecimal balance;
}

// AFTER: Event-sourced aggregate
public class Account {
    public void apply(AccountCreatedEvent event) { }
    public void apply(MoneyDepositedEvent event) { }
    public void apply(MoneyWithdrawnEvent event) { }
}
```

### Step 2: Define Events
```java
// Create events that represent every state mutation
public record AccountCreated(String accountId, String owner, BigDecimal initialBalance) {}
public record MoneyDeposited(String accountId, BigDecimal amount) {}
public record MoneyWithdrawn(String accountId, BigDecimal amount) {}
```

### Step 3: Build Event Store
```java
// Create event store table
// Migrate existing data as an initial event
```

### Step 4: Dual Write (Migration Phase)
```java
@Service
public class AccountService {
    public void deposit(String accountId, BigDecimal amount) {
        // Old: CRUD approach
        accountRepository.updateBalance(accountId, amount);
        // New: Event sourcing approach
        eventStore.append(accountId, List.of(new MoneyDeposited(accountId, amount)));
    }
}
```

### Step 5: Verify and Cutover
```java
// Compare CRUD state vs event-sourced state
// Once verified, remove CRUD code
```
