# Lab 05: Problem Walkthrough — DDD Aggregate with Invariant Enforcement
## Problem Statement

Implement a bank account aggregate using Domain-Driven Design. The aggregate must enforce hard invariants:

1. Balance can never go negative.
2. Deposits and withdrawals must be positive amounts.
3. All money on an account must be in the same currency.
4. A frozen account rejects all transactions.
5. Withdrawals respect a daily limit (configurable, per-day tracking).
6. Every state change emits a domain event; events are published *after* successful state changes.
7. Aggregate state is only reachable through the repository.

## Constraints

- Java 21+ only.
- Use DDD building blocks: **Aggregate** (`Account`), **Value Objects** (`Money`, `AccountId`), **Domain Events** (sealed records), **Repository** (port interface).
- Invariant violations raise a domain exception — not `IllegalArgumentException` scattered in services.
- The aggregate must be testable in isolation.

## Approach

**Why an aggregate?** `Account` is the consistency boundary: transactions and the daily-limit counter must change together atomically. Nobody outside the aggregate may mutate its internal state; all changes go through command methods that enforce invariants first.

Design decisions:

- **`Money` as a value object**: equality by amount+currency, and it owns the positivity/currency invariants — value objects validate themselves.
- **`AccountId` as a typed id**: UUIDs wrapped in a record so the type system prevents passing a payment id where an account id belongs.
- **`withdraw` enforces the daily limit** by tracking `todayWithdrawn` + the current day; the counter resets when the UTC day changes.
- **Domain events are returned, not passed to a publisher inside the aggregate** — the aggregate stays infrastructure-free; the application service publishes after saving.
- **`DomainException`** communicates business-rule violations distinctly from programming errors.

## Step-by-Step Solution

### Step 1: Value Objects

```java
record Money(long amount, String currency) {
    Money {
        if (amount < 0) throw new DomainException("Money amount cannot be negative");
        if (currency == null || currency.isBlank()) throw new DomainException("Currency required");
    }

    Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount + other.amount, currency);
    }

    Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount - other.amount, currency);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new DomainException("Currency mismatch: " + currency + " vs " + other.currency);
        }
    }
}

record AccountId(UUID value) {
    static AccountId random() {
        return new AccountId(UUID.randomUUID());
    }
}
```

### Step 2: Domain Events

A sealed hierarchy — exhaustive `switch` over events is compiler-checked everywhere.

```java
sealed interface AccountEvent permits AccountOpened, MoneyDeposited, MoneyWithdrawn, AccountFrozen, AccountUnfrozen {}

record AccountOpened(AccountId accountId, Money initialBalance) implements AccountEvent {}
record MoneyDeposited(AccountId accountId, Money amount) implements AccountEvent {}
record MoneyWithdrawn(AccountId accountId, Money amount, Money balanceAfter) implements AccountEvent {}
record AccountFrozen(AccountId accountId) implements AccountEvent {}
record AccountUnfrozen(AccountId accountId) implements AccountEvent {}
```

### Step 3: The Aggregate

Every mutating method follows the same shape: **check invariants -> mutate -> record event**. The daily-limit tracking resets when the local day changes.

```java
class Account {
    private final AccountId id;
    private final String owner;
    private final String currency;
    private Money balance;
    private boolean frozen;
    private long dailyLimit;
    private LocalDate limitWindowDay;
    private long todayWithdrawn;
    private final List<AccountEvent> events = new ArrayList<>();

    Account(AccountId id, String owner, Money initialBalance, long dailyLimit) {
        this.id = id;
        this.owner = owner;
        this.currency = initialBalance.currency();
        this.balance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.limitWindowDay = LocalDate.now();
        this.events.add(new AccountOpened(id, initialBalance));
    }

    AccountId id() { return id; }
    String owner() { return owner; }
    Money balance() { return balance; }
    boolean frozen() { return frozen; }
    List<AccountEvent> pendingEvents() { return List.copyOf(events); }

    void clearPendingEvents() {
        events.clear();
    }

    void deposit(Money amount) {
        ensureActive();
        balance = balance.add(amount);
        events.add(new MoneyDeposited(id, amount));
    }

    void withdraw(Money amount) {
        ensureActive();
        if (!limitWindowDay.equals(LocalDate.now())) {   // day rolled over: reset the window
            limitWindowDay = LocalDate.now();
            todayWithdrawn = 0;
        }
        long projected = todayWithdrawn + amount.amount();
        if (projected > dailyLimit) {
            throw new DomainException("Daily limit exceeded: " + dailyLimit + " (already withdrawn " + todayWithdrawn + ")");
        }
        balance = balance.subtract(amount);        // subtract throws if balance goes negative
        todayWithdrawn = projected;
        events.add(new MoneyWithdrawn(id, amount, balance));
    }

    void freeze() {
        if (frozen) throw new DomainException("Account already frozen");
        frozen = true;
        events.add(new AccountFrozen(id));
    }

    void unfreeze() {
        if (!frozen) throw new DomainException("Account is not frozen");
        frozen = false;
        events.add(new AccountUnfrozen(id));
    }

    private void ensureActive() {
        if (frozen) throw new DomainException("Account " + id.value() + " is frozen");
    }
}
```

Invariant checklist enforced here:

| Invariant | Enforced by |
|---|---|
| Balance never negative | `Money.subtract` throws on negative result |
| Positive amounts | `Money` constructor |
| Single currency | `Money.add/subtract` check currency equality |
| Frozen blocks transactions | `ensureActive()` |
| Daily withdrawal limit | `withdraw` projected check + window reset |
| No duplicate freeze/unfreeze | State checks in `freeze`/`unfreeze` |

### Step 4: Repository (Port)

The repository is the only way aggregates are loaded or saved — this keeps the aggregate boundary honest. It is a plain port with two methods, `findById` and `save`, shown in the Complete Solution below.

### Step 5: DomainException

`DomainException extends RuntimeException` — a single business-rule exception type so callers catch business violations distinctly from programming errors (full class in the Complete Solution below).

### Step 6: Application Service

The service orchestrates: load -> command -> save -> publish events. The aggregate returns pending events; the service clears them after publishing.

```java
class AccountService {
    private final AccountRepository repository;
    private final Consumer<AccountEvent> eventPublisher;

    AccountService(AccountRepository repository, Consumer<AccountEvent> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    AccountId openAccount(String owner, Money initialBalance, long dailyLimit) {
        var account = new Account(AccountId.random(), owner, initialBalance, dailyLimit);
        repository.save(account);
        publish(account);
        return account.id();
    }

    void deposit(AccountId accountId, Money amount) {
        var account = load(accountId);
        account.deposit(amount);
        repository.save(account);
        publish(account);
    }

    void withdraw(AccountId accountId, Money amount) {
        var account = load(accountId);
        account.withdraw(amount);
        repository.save(account);
        publish(account);
    }

    private Account load(AccountId accountId) {
        return repository.findById(accountId)
            .orElseThrow(() -> new DomainException("Account not found: " + accountId.value()));
    }

    private void publish(Account account) {
        for (var event : account.pendingEvents()) {
            eventPublisher.accept(event);
        }
        account.clearPendingEvents();
    }
}
```

### Step 7: In-Memory Repository + Main

The repository is a thin `ConcurrentHashMap` wrapper (see Complete Solution). The demo wires the service with a printing event publisher:

```java
public class DddLab {
    public static void main(String[] args) {
        var repository = new InMemoryAccountRepository();
        var service = new AccountService(repository, event ->
            System.out.println("[event] " + event.getClass().getSimpleName()));

        var accountId = service.openAccount("Alice", new Money(1000, "USD"), 800);

        service.deposit(accountId, new Money(200, "USD"));
        service.withdraw(accountId, new Money(300, "USD"));

        try {
            service.withdraw(accountId, new Money(9999, "USD")); // over daily limit
        } catch (DomainException e) {
            System.out.println("Invariant rejected: " + e.getMessage());
        }

        var account = repository.findById(accountId).orElseThrow();
        System.out.println("Final balance: " + account.balance().amount() + " " + account.balance().currency());
    }
}
```

## Complete Solution

The full compilable file, `DddLab.java` in package `com.architecture.deep.lab05`:

```java
package com.architecture.deep.lab05;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DddLab {
    public static void main(String[] args) {
        var repository = new InMemoryAccountRepository();
        var service = new AccountService(repository, event ->
            System.out.println("[event] " + event.getClass().getSimpleName()));

        var accountId = service.openAccount("Alice", new Money(1000, "USD"), 800);

        service.deposit(accountId, new Money(200, "USD"));
        service.withdraw(accountId, new Money(300, "USD"));

        try {
            service.withdraw(accountId, new Money(9999, "USD"));
        } catch (DomainException e) {
            System.out.println("Invariant rejected: " + e.getMessage());
        }

        var account = repository.findById(accountId).orElseThrow();
        System.out.println("Final balance: " + account.balance().amount() + " " + account.balance().currency());
    }
}

class DomainException extends RuntimeException {
    DomainException(String message) {
        super(message);
    }
}

record Money(long amount, String currency) {
    Money {
        if (amount < 0) throw new DomainException("Money amount cannot be negative");
        if (currency == null || currency.isBlank()) throw new DomainException("Currency required");
    }

    Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount + other.amount, currency);
    }

    Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount - other.amount, currency);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new DomainException("Currency mismatch: " + currency + " vs " + other.currency);
        }
    }
}

record AccountId(UUID value) {
    static AccountId random() {
        return new AccountId(UUID.randomUUID());
    }
}

sealed interface AccountEvent permits AccountOpened, MoneyDeposited, MoneyWithdrawn, AccountFrozen, AccountUnfrozen {}

record AccountOpened(AccountId accountId, Money initialBalance) implements AccountEvent {}
record MoneyDeposited(AccountId accountId, Money amount) implements AccountEvent {}
record MoneyWithdrawn(AccountId accountId, Money amount, Money balanceAfter) implements AccountEvent {}
record AccountFrozen(AccountId accountId) implements AccountEvent {}
record AccountUnfrozen(AccountId accountId) implements AccountEvent {}

class Account {
    private final AccountId id;
    private final String owner;
    private final String currency;
    private Money balance;
    private boolean frozen;
    private long dailyLimit;
    private LocalDate limitWindowDay;
    private long todayWithdrawn;
    private final List<AccountEvent> events = new ArrayList<>();

    Account(AccountId id, String owner, Money initialBalance, long dailyLimit) {
        this.id = id;
        this.owner = owner;
        this.currency = initialBalance.currency();
        this.balance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.limitWindowDay = LocalDate.now();
        this.events.add(new AccountOpened(id, initialBalance));
    }

    AccountId id() { return id; }
    String owner() { return owner; }
    Money balance() { return balance; }
    boolean frozen() { return frozen; }
    List<AccountEvent> pendingEvents() { return List.copyOf(events); }

    void clearPendingEvents() {
        events.clear();
    }

    void deposit(Money amount) {
        ensureActive();
        balance = balance.add(amount);
        events.add(new MoneyDeposited(id, amount));
    }

    void withdraw(Money amount) {
        ensureActive();
        if (!limitWindowDay.equals(LocalDate.now())) {
            limitWindowDay = LocalDate.now();
            todayWithdrawn = 0;
        }
        long projected = todayWithdrawn + amount.amount();
        if (projected > dailyLimit) {
            throw new DomainException("Daily limit exceeded: " + dailyLimit
                + " (already withdrawn " + todayWithdrawn + ")");
        }
        balance = balance.subtract(amount);
        todayWithdrawn = projected;
        events.add(new MoneyWithdrawn(id, amount, balance));
    }

    void freeze() {
        if (frozen) throw new DomainException("Account already frozen");
        frozen = true;
        events.add(new AccountFrozen(id));
    }

    void unfreeze() {
        if (!frozen) throw new DomainException("Account is not frozen");
        frozen = false;
        events.add(new AccountUnfrozen(id));
    }

    private void ensureActive() {
        if (frozen) throw new DomainException("Account " + id.value() + " is frozen");
    }
}

interface AccountRepository {
    Optional<Account> findById(AccountId accountId);
    void save(Account account);
}

class AccountService {
    private final AccountRepository repository;
    private final Consumer<AccountEvent> eventPublisher;

    AccountService(AccountRepository repository, Consumer<AccountEvent> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    AccountId openAccount(String owner, Money initialBalance, long dailyLimit) {
        var account = new Account(AccountId.random(), owner, initialBalance, dailyLimit);
        repository.save(account);
        publish(account);
        return account.id();
    }

    void deposit(AccountId accountId, Money amount) {
        var account = load(accountId);
        account.deposit(amount);
        repository.save(account);
        publish(account);
    }

    void withdraw(AccountId accountId, Money amount) {
        var account = load(accountId);
        account.withdraw(amount);
        repository.save(account);
        publish(account);
    }

    private Account load(AccountId accountId) {
        return repository.findById(accountId)
            .orElseThrow(() -> new DomainException("Account not found: " + accountId.value()));
    }

    private void publish(Account account) {
        for (var event : account.pendingEvents()) {
            eventPublisher.accept(event);
        }
        account.clearPendingEvents();
    }
}

class InMemoryAccountRepository implements AccountRepository {
    private final Map<AccountId, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public void save(Account account) {
        accounts.put(account.id(), account);
    }
}
```

## Complexity Analysis

- **openAccount / deposit / withdraw**: O(1) — a few arithmetic operations plus one map put/get.
- **pendingEvents**: O(E) when copying the event list; events accumulate per aggregate over its lifetime (in production, events are persisted and the aggregate is rebuilt from them or snapshotted). **Space**: O(E + 1) per aggregate.
- **Concurrency**: single-threaded here; production would add optimistic locking (version field) or the repository would serialize per-aggregate access.

## Test Cases

| Scenario | Expected |
|---|---|
| Open account with 1000 USD | `AccountOpened` event; balance 1000 |
| Deposit 200 | Balance 1200; `MoneyDeposited` |
| Withdraw 300 (limit 800) | Balance 900; `MoneyWithdrawn` |
| Withdraw 9999 in one day | `DomainException("Daily limit exceeded")` |
| Withdraw more than balance | `DomainException` from `Money.subtract` (negative amount) |
| Deposit negative amount | `DomainException("Money amount cannot be negative")` |

Example run:

```
[event] AccountOpened
[event] MoneyDeposited
[event] MoneyWithdrawn
Invariant rejected: Daily limit exceeded: 800 (already withdrawn 300)
Final balance: 900 USD
```

## Follow-Up Questions
1. **Where does the daily-limit window reset live?** The aggregate owns it (self-contained counter). A stateless design would pass `now` in — better for testing: `withdraw(amount, LocalDate today)` makes day-rollover tests trivial.
2. **Why are events returned by the aggregate rather than published inside it?** The aggregate stays infrastructure-free and testable; the application service decides delivery (in-process, outbox, Kafka).
3. **What if two withdrawals race concurrently?** The aggregate needs a version number; `save` uses optimistic locking (`WHERE version = ?`), or a pessimistic lock per account.
4. **How large should an aggregate be?** One aggregate = one consistency boundary. Small is better; if two aggregates need a 'transaction', use a saga or re-examine whether they are really one aggregate.
5. **Are domain events part of the persistent model?** Often yes — event sourcing keeps them as the record; otherwise they are published and possibly archived for audit.
6. **How do you model 'today' to avoid flaky tests?** Inject a `Clock` (java.time) into the aggregate or pass `LocalDate` explicitly per operation.
