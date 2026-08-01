# Lab 03: Problem Walkthrough — Hexagonal Application with Ports and Adapters

## Problem Statement

Implement a money-transfer application using hexagonal architecture (ports and adapters). The application must:

1. Keep the **domain core** completely free of framework and infrastructure dependencies.
2. Define **inbound ports** (use-case interfaces) that driving adapters call.
3. Define **outbound ports** (repository/notification interfaces) that the core depends on.
4. Provide **adapters** that plug into the ports: an in-memory repository, a console notification, and a console controller.
5. Wire everything in a composition root, proving the core can be exercised with zero frameworks.

## Constraints

- Java 21+ only; the core package must compile with nothing but the JDK.
- Adapters are replaceable without touching the core (swap `InMemoryAccountRepository` for `JdbcAccountRepository` without recompiling the application layer).
- All dependencies point inward: adapters -> ports <- application core.

## Approach

Hexagonal architecture (Alistair Cockburn) visualizes the application as a hexagon: the **inside** is the domain + application logic, the **outside** is the world (UI, DB, messaging). Communication happens through **ports**:

- **Inbound (driving) ports**: interfaces the outside world calls — the use cases.
- **Outbound (driven) ports**: interfaces the core calls — persistence, notifications, external services.
- **Adapters** translate between the world's protocol (REST, console, JDBC) and the port interfaces.

The dependency rule is the same one Clean Architecture later formalized: dependencies always point inward.

```
[Console Controller] -> (TransferMoneyUseCase) -> [TransferMoneyService] -> (AccountRepository) -> [InMemoryAdapter]
                         inbound port                                    outbound port            [EmailAdapter]
```

## Step-by-Step Solution

### Step 1: Domain Model

The domain is a plain `BankAccount` — an entity with identity, owner, and balance. No annotations, no framework types.

```java
class BankAccount {
    private final UUID id;
    private final String owner;
    private long balance;

    BankAccount(UUID id, String owner, long initialBalance) {
        this.id = id;
        this.owner = owner;
        this.balance = initialBalance;
    }

    UUID id() { return id; }
    String owner() { return owner; }
    long balance() { return balance; }

    void deposit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
    }

    void withdraw(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive");
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amount;
    }
}
```

### Step 2: Inbound Ports (Use Cases)

The driving adapters only know these interfaces. Note the port uses domain types only — no HTTP types, no JSON.

```java
interface TransferMoneyUseCase {
    TransferResult transfer(UUID fromAccountId, UUID toAccountId, long amount);
}

interface OpenAccountUseCase {
    UUID openAccount(String owner, long initialBalance);
}

record TransferResult(UUID fromAccountId, UUID toAccountId, long amount, long fromBalanceAfter, long toBalanceAfter) {}
```

### Step 3: Outbound Ports

The core depends on these interfaces — the direction of dependency is inverted: the adapter implements the interface, the core defines it.

```java
interface AccountRepository {
    Optional<BankAccount> findById(UUID accountId);
    void save(BankAccount account);
}

interface NotificationPort {
    void notify(String recipient, String message);
}
```

### Step 4: The Application Service (inside the hexagon)

The service implements the use case and depends only on ports. It contains the business flow: load both accounts, validate, debit, credit, save, notify.

```java
class AccountTransferService implements TransferMoneyUseCase, OpenAccountUseCase {
    private final AccountRepository repository;
    private final NotificationPort notifications;

    AccountTransferService(AccountRepository repository, NotificationPort notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    @Override
    public UUID openAccount(String owner, long initialBalance) {
        var account = new BankAccount(UUID.randomUUID(), owner, initialBalance);
        repository.save(account);
        notifications.notify(owner, "Account opened with balance " + initialBalance);
        return account.id();
    }

    @Override
    public TransferResult transfer(UUID fromAccountId, UUID toAccountId, long amount) {
        var from = repository.findById(fromAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        var to = repository.findById(toAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        from.withdraw(amount);
        to.deposit(amount);

        repository.save(from);
        repository.save(to);

        notifications.notify(from.owner(), "Transferred " + amount + " to " + to.owner());
        notifications.notify(to.owner(), "Received " + amount + " from " + from.owner());

        return new TransferResult(fromAccountId, toAccountId, amount, from.balance(), to.balance());
    }
}
```

### Step 5: Driven Adapters

Adapters live outside the hexagon. Here: an in-memory repository and a console-based email notification.

```java
class InMemoryAccountRepository implements AccountRepository {
    private final Map<UUID, BankAccount> accounts = new ConcurrentHashMap<>();

    @Override
    public Optional<BankAccount> findById(UUID accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public void save(BankAccount account) {
        accounts.put(account.id(), account);
    }
}

class EmailNotificationAdapter implements NotificationPort {
    @Override
    public void notify(String recipient, String message) {
        System.out.println("[email to " + recipient + "] " + message);
    }
}
```

### Step 6: Driving Adapter (Console Controller)

The controller translates console input into use-case calls. In a real system this would be a REST controller with the same shape: parse request -> call port -> render response.

```java
class ConsoleAccountController {
    private final OpenAccountUseCase openAccount;
    private final TransferMoneyUseCase transfer;

    ConsoleAccountController(OpenAccountUseCase openAccount, TransferMoneyUseCase transfer) {
        this.openAccount = openAccount;
        this.transfer = transfer;
    }

    void openAccountCommand(String owner, long initialBalance) {
        var id = openAccount.openAccount(owner, initialBalance);
        System.out.println("Opened account " + id + " for " + owner);
    }

    void transferCommand(UUID from, UUID to, long amount) {
        var result = transfer.transfer(from, to, amount);
        System.out.println("Transferred " + result.amount()
            + " | from balance: " + result.fromBalanceAfter()
            + " | to balance: " + result.toBalanceAfter());
    }
}
```

### Step 7: Composition Root

The composition root is the only place that knows concrete classes. Swap `EmailNotificationAdapter` for an `SmsNotificationAdapter` and the core never changes.

```java
public class HexagonalLab {
    public static void main(String[] args) {
        var repository = new InMemoryAccountRepository();
        var notifications = new EmailNotificationAdapter();
        var service = new AccountTransferService(repository, notifications);
        var controller = new ConsoleAccountController(service, service);

        var aliceId = controller.openAccountCommand?;
        ...
    }
}
```

Wait — the console adapter returns `void` in this sketch; for the demo we let the composition root call the use case directly for full output. Final wiring:

```java
public class HexagonalLab {
    public static void main(String[] args) {
        var repository = new InMemoryAccountRepository();
        var notifications = new EmailNotificationAdapter();
        var service = new AccountTransferService(repository, notifications);

        var aliceId = service.openAccount("Alice", 1000);
        var bobId = service.openAccount("Bob", 500);

        var result = service.transfer(aliceId, bobId, 300);

        System.out.println("Alice balance: " + result.fromBalanceAfter());
        System.out.println("Bob balance: " + result.toBalanceAfter());
        System.out.println("Swap adapters and re-run: core code is untouched.");
    }
}
```

## Complete Solution

The full compilable file, `HexagonalLab.java` in package `com.architecture.deep.lab03`:

```java
package com.architecture.deep.lab03;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HexagonalLab {
    public static void main(String[] args) {
        var repository = new InMemoryAccountRepository();
        var notifications = new EmailNotificationAdapter();
        var service = new AccountTransferService(repository, notifications);

        var aliceId = service.openAccount("Alice", 1000);
        var bobId = service.openAccount("Bob", 500);

        var result = service.transfer(aliceId, bobId, 300);

        System.out.println("Alice balance: " + result.fromBalanceAfter());
        System.out.println("Bob balance: " + result.toBalanceAfter());
        System.out.println("Swap adapters and re-run: core code is untouched.");
    }
}

class BankAccount {
    private final UUID id;
    private final String owner;
    private long balance;

    BankAccount(UUID id, String owner, long initialBalance) {
        this.id = id;
        this.owner = owner;
        this.balance = initialBalance;
    }

    UUID id() { return id; }
    String owner() { return owner; }
    long balance() { return balance; }

    void deposit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
    }

    void withdraw(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive");
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amount;
    }
}

interface TransferMoneyUseCase {
    TransferResult transfer(UUID fromAccountId, UUID toAccountId, long amount);
}

interface OpenAccountUseCase {
    UUID openAccount(String owner, long initialBalance);
}

record TransferResult(UUID fromAccountId, UUID toAccountId, long amount, long fromBalanceAfter, long toBalanceAfter) {}

interface AccountRepository {
    Optional<BankAccount> findById(UUID accountId);
    void save(BankAccount account);
}

interface NotificationPort {
    void notify(String recipient, String message);
}

class AccountTransferService implements TransferMoneyUseCase, OpenAccountUseCase {
    private final AccountRepository repository;
    private final NotificationPort notifications;

    AccountTransferService(AccountRepository repository, NotificationPort notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    @Override
    public UUID openAccount(String owner, long initialBalance) {
        var account = new BankAccount(UUID.randomUUID(), owner, initialBalance);
        repository.save(account);
        notifications.notify(owner, "Account opened with balance " + initialBalance);
        return account.id();
    }

    @Override
    public TransferResult transfer(UUID fromAccountId, UUID toAccountId, long amount) {
        var from = repository.findById(fromAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        var to = repository.findById(toAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        from.withdraw(amount);
        to.deposit(amount);

        repository.save(from);
        repository.save(to);

        notifications.notify(from.owner(), "Transferred " + amount + " to " + to.owner());
        notifications.notify(to.owner(), "Received " + amount + " from " + from.owner());

        return new TransferResult(fromAccountId, toAccountId, amount, from.balance(), to.balance());
    }
}

class InMemoryAccountRepository implements AccountRepository {
    private final Map<UUID, BankAccount> accounts = new ConcurrentHashMap<>();

    @Override
    public Optional<BankAccount> findById(UUID accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public void save(BankAccount account) {
        accounts.put(account.id(), account);
    }
}

class EmailNotificationAdapter implements NotificationPort {
    @Override
    public void notify(String recipient, String message) {
        System.out.println("[email to " + recipient + "] " + message);
    }
}

class ConsoleAccountController {
    private final OpenAccountUseCase openAccount;
    private final TransferMoneyUseCase transfer;

    ConsoleAccountController(OpenAccountUseCase openAccount, TransferMoneyUseCase transfer) {
        this.openAccount = openAccount;
        this.transfer = transfer;
    }

    void openAccountCommand(String owner, long initialBalance) {
        var id = openAccount.openAccount(owner, initialBalance);
        System.out.println("Opened account " + id + " for " + owner);
    }

    void transferCommand(UUID from, UUID to, long amount) {
        var result = transfer.transfer(from, to, amount);
        System.out.println("Transferred " + result.amount()
            + " | from balance: " + result.fromBalanceAfter()
            + " | to balance: " + result.toBalanceAfter());
    }
}
```

## Complexity Analysis

- **Transfer**: O(1) for repository lookups (in-memory map), O(1) for the balance mutations, O(1) for notifications.
- **Memory**: O(N) where N is the number of accounts.
- **Architectural cost**: one extra interface layer per boundary; in return, infrastructure can be swapped, the core is unit-testable with in-memory fakes, and framework upgrades don't touch domain code.

## Test Cases

| Scenario | Expected |
|---|---|
| Transfer 300 from Alice(1000) to Bob(500) | Alice 700, Bob 800, two notifications sent |
| Transfer from unknown account | `IllegalArgumentException("Source account not found")` |
| Transfer with negative amount | `IllegalArgumentException("Transfer must be positive")` — guarded by `withdraw` |
| Transfer more than balance | `IllegalStateException("Insufficient funds")` — no partial mutation |
| Open account | Notification "Account opened", repository contains account |

Example run:

```
[email to Alice] Account opened with balance 1000
[email to Bob] Account opened with balance 500
[email to Alice] Transferred 300 to Bob
[email to Bob] Received 300 from Alice
Alice balance: 700
Bob balance: 800
Swap adapters and re-run: core code is untouched.
```

## Follow-Up Questions

1. **Hexagonal vs layered (3-tier)?** Layered architecture allows layers to depend on concrete infrastructure (e.g., service -> JdbcRepository class). Hexagonal inverts every boundary through ports, so the core references only interfaces.
2. **Where does the transaction boundary live?** The application service — one use case = one transaction. The repository port exposes `save`; a real adapter (JDBC) would open a transaction in `save` or the service would call a `UnitOfWork` port.
3. **How do you keep the port from leaking framework types?** Ports use domain types and primitives only; adapters translate. If a REST controller needs a JSON response, the adapter builds it.
4. **How does this help testing?** The core is tested against fake ports — no DB, no HTTP, no Spring context. Adapters get their own thin tests.
5. **Is a `NotificationPort` really necessary?** It's an outbound side effect — exactly what a port should hide. Swapping email for SMS/Kafka notification becomes a one-class change.
6. **How do you prevent ports proliferation?** Group by role: one repository interface per aggregate, one use case per user story or per feature. If a port grows too many methods, split by responsibility.
7. **What does this mean for the REST adapter in a framework like Spring?** The controller is a thin Spring bean that calls the port — the core stays framework-free and can be tested without Spring, which is the entire point.
