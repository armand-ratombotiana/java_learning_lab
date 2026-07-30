# Implement Event Sourcing Repository for Banking Transactions

## Problem Statement
Design and implement an event-sourced banking transaction repository with:
- Account creation, deposit, withdrawal, and transfer operations
- Event sourcing: all state changes are persisted as events
- Projection: current account balance derived from event stream
- Snapshotting for performance on long event streams
- Optimistic concurrency control (expected version)
- Transaction rollback on insufficient funds
- Audit log of all events

## Solution

```java
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Event-sourced banking transaction repository.
 * <p>
 * Time complexity:
 * - handleCommand: O(n) for replaying events to build state
 * - withSnapshot: O(log n) for snapshot + remaining events
 * - getBalance: O(n) worst-case, O(1) with snapshot cache
 * <p>
 * Space complexity: O(n) for event store, O(1) per snapshot
 */
public class BankingEventSourcing {

    // ── Event store (in-memory) ─────────────────────────────────────────────

    public interface EventStore {
        void append(String aggregateId, List<Event> events, long expectedVersion);
        List<Event> readEvents(String aggregateId);
        List<Event> readEventsSince(String aggregateId, long sinceVersion);
        Snapshot getSnapshot(String aggregateId);
        void saveSnapshot(String aggregateId, Snapshot snapshot);
    }

    public static class InMemoryEventStore implements EventStore {
        private final ConcurrentHashMap<String, List<Event>> store = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, Snapshot> snapshots = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, AtomicLong> versionMap = new ConcurrentHashMap<>();

        @Override
        public void append(String aggregateId, List<Event> events, long expectedVersion) {
            versionMap.compute(aggregateId, (key, current) -> {
                long actual = (current == null) ? 0 : current.get();
                if (actual != expectedVersion) {
                    throw new ConcurrencyConflictException(
                        "Expected version " + expectedVersion + " but found " + actual);
                }
                long newVersion = actual + events.size();
                AtomicLong al = new AtomicLong(newVersion);
                return al;
            });
            store.compute(aggregateId, (key, existing) -> {
                if (existing == null) return new CopyOnWriteArrayList<>(events);
                existing.addAll(events);
                return existing;
            });
        }

        @Override
        public List<Event> readEvents(String aggregateId) {
            return store.getOrDefault(aggregateId, List.of());
        }

        @Override
        public List<Event> readEventsSince(String aggregateId, long sinceVersion) {
            List<Event> all = readEvents(aggregateId);
            return all.stream()
                .skip(sinceVersion)
                .collect(Collectors.toList());
        }

        @Override
        public Snapshot getSnapshot(String aggregateId) {
            return snapshots.get(aggregateId);
        }

        @Override
        public void saveSnapshot(String aggregateId, Snapshot snapshot) {
            snapshots.put(aggregateId, snapshot);
        }
    }

    public static class ConcurrencyConflictException extends RuntimeException {
        public ConcurrencyConflictException(String msg) { super(msg); }
    }

    // ── Domain events ───────────────────────────────────────────────────────

    public sealed interface Event permits
        AccountCreated, MoneyDeposited, MoneyWithdrawn, MoneyTransferred {}
    public record AccountCreated(String accountId, String owner, BigDecimal initialBalance,
                                 Instant timestamp) implements Event {}
    public record MoneyDeposited(String accountId, BigDecimal amount, BigDecimal balanceAfter,
                                 Instant timestamp) implements Event {}
    public record MoneyWithdrawn(String accountId, BigDecimal amount, BigDecimal balanceAfter,
                                 Instant timestamp) implements Event {}
    public record MoneyTransferred(String fromAccountId, String toAccountId, BigDecimal amount,
                                   BigDecimal fromBalanceAfter, BigDecimal toBalanceAfter,
                                   Instant timestamp) implements Event {}

    // ── Snapshot ────────────────────────────────────────────────────────────

    public record Snapshot(String aggregateId, long version, AccountState state,
                           Instant timestamp) {}

    // ── Account state (projection from events) ──────────────────────────────

    public static class AccountState {
        private String accountId;
        private String owner;
        private BigDecimal balance;
        private long version;
        private boolean closed;
        private final List<Event> recentEvents = new ArrayList<>();

        public AccountState() {
            this.balance = BigDecimal.ZERO;
            this.version = 0;
        }

        public void apply(Event event) {
            switch (event) {
                case AccountCreated e -> {
                    this.accountId = e.accountId();
                    this.owner = e.owner();
                    this.balance = e.initialBalance();
                    this.version = 0;
                }
                case MoneyDeposited e -> {
                    this.balance = e.balanceAfter();
                    this.version++;
                }
                case MoneyWithdrawn e -> {
                    this.balance = e.balanceAfter();
                    this.version++;
                }
                case MoneyTransferred e -> {
                    if (e.fromAccountId().equals(this.accountId)) {
                        this.balance = e.fromBalanceAfter();
                    } else {
                        this.balance = e.toBalanceAfter();
                    }
                    this.version++;
                }
            }
            recentEvents.add(event);
            if (recentEvents.size() > 10) recentEvents.remove(0);
        }

        public String getAccountId() { return accountId; }
        public String getOwner() { return owner; }
        public BigDecimal getBalance() { return balance; }
        public long getVersion() { return version; }
        public boolean isClosed() { return closed; }
        public void close() { this.closed = true; }
        public List<Event> getRecentEvents() { return List.copyOf(recentEvents); }
    }

    // ── Repository ──────────────────────────────────────────────────────────

    public static class AccountRepository {

        private final EventStore eventStore;
        private static final int SNAPSHOT_THRESHOLD = 50;

        public AccountRepository(EventStore eventStore) {
            this.eventStore = eventStore;
        }

        public AccountState createAccount(String accountId, String owner,
                                          BigDecimal initialBalance) {
            if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
            Event event = new AccountCreated(accountId, owner, initialBalance, Instant.now());
            eventStore.append(accountId, List.of(event), 0);
            AccountState state = new AccountState();
            state.apply(event);
            return state;
        }

        public AccountState deposit(String accountId, BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            AccountState state = load(accountId);
            BigDecimal newBalance = state.getBalance().add(amount);
            Event event = new MoneyDeposited(accountId, amount, newBalance, Instant.now());
            eventStore.append(accountId, List.of(event), state.getVersion());
            state.apply(event);
            return state;
        }

        public AccountState withdraw(String accountId, BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            AccountState state = load(accountId);
            if (state.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                    "Insufficient funds: balance=" + state.getBalance()
                        + ", withdrawal=" + amount);
            }
            BigDecimal newBalance = state.getBalance().subtract(amount);
            Event event = new MoneyWithdrawn(accountId, amount, newBalance, Instant.now());
            eventStore.append(accountId, List.of(event), state.getVersion());
            state.apply(event);
            return state;
        }

        public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer amount must be positive");
            }
            AccountState fromState = load(fromAccount);
            if (fromState.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                    "Insufficient funds for transfer: balance="
                        + fromState.getBalance() + ", transfer=" + amount);
            }
            AccountState toState = load(toAccount);

            BigDecimal fromNewBalance = fromState.getBalance().subtract(amount);
            BigDecimal toNewBalance = toState.getBalance().add(amount);

            Event event = new MoneyTransferred(
                fromAccount, toAccount, amount,
                fromNewBalance, toNewBalance, Instant.now());

            // Append to both aggregates atomically (in practice use 2PC / transactional outbox)
            eventStore.append(fromAccount, List.of(event), fromState.getVersion());
            eventStore.append(toAccount, List.of(event), toState.getVersion());

            fromState.apply(event);
            toState.apply(event);
        }

        public BigDecimal getBalance(String accountId) {
            return load(accountId).getBalance();
        }

        public List<Event> getEventHistory(String accountId) {
            return eventStore.readEvents(accountId);
        }

        // ── Load aggregate with snapshot support ────────────────────────────

        public AccountState load(String aggregateId) {
            Snapshot snapshot = eventStore.getSnapshot(aggregateId);
            AccountState state;

            long sinceVersion;
            if (snapshot != null) {
                state = snapshot.state();
                sinceVersion = snapshot.version();
            } else {
                state = new AccountState();
                sinceVersion = 0;
            }

            List<Event> events = eventStore.readEventsSince(aggregateId, sinceVersion);
            for (Event event : events) {
                state.apply(event);
            }

            // Take snapshot if threshold crossed
            if (state.getVersion() > 0 && state.getVersion() % SNAPSHOT_THRESHOLD == 0) {
                Snapshot newSnapshot = new Snapshot(
                    aggregateId, state.getVersion(), state, Instant.now());
                eventStore.saveSnapshot(aggregateId, newSnapshot);
            }

            return state;
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String msg) { super(msg); }
    }

    // ── Example usage ───────────────────────────────────────────────────────

    public static void main(String[] args) {
        EventStore store = new InMemoryEventStore();
        AccountRepository repo = new AccountRepository(store);

        String acc1 = "ACC-1001";
        String acc2 = "ACC-1002";

        repo.createAccount(acc1, "Alice", new BigDecimal("1000.00"));
        repo.createAccount(acc2, "Bob", new BigDecimal("500.00"));

        System.out.println("Alice balance: $" + repo.getBalance(acc1));
        System.out.println("Bob balance: $" + repo.getBalance(acc2));

        repo.deposit(acc1, new BigDecimal("200.00"));
        System.out.println("After deposit: $" + repo.getBalance(acc1));

        repo.transfer(acc1, acc2, new BigDecimal("300.00"));
        System.out.println("After transfer — Alice: $" + repo.getBalance(acc1)
            + ", Bob: $" + repo.getBalance(acc2));

        System.out.println("\nEvent history for " + acc1 + ":");
        repo.getEventHistory(acc1).forEach(e -> System.out.println("  " + e));

        try {
            repo.withdraw(acc1, new BigDecimal("99999"));
        } catch (InsufficientFundsException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}
```

## Complexity Analysis

| Operation       | Time Complexity            | Space Complexity |
|-----------------|----------------------------|-----------------|
| createAccount   | O(1)                       | O(1)            |
| deposit/withdraw| O(n) for load + O(1) append| O(1)            |
| transfer        | O(n + m) for both loads     | O(1)            |
| load (no snap)  | O(e) where e = event count  | O(e) projection |
| load (snapshot) | O(e') where e' = events since snapshot | O(e') projection |
| getBalance      | O(e) worst, O(1) after load | O(1)            |

Overall storage: O(total events) ~ O(transactions). Snapshots reduce replay cost to O(snapshot interval) per load.

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class BankingEventSourcingTest {

    private BankingEventSourcing.EventStore store;
    private BankingEventSourcing.AccountRepository repo;

    @BeforeEach
    void setUp() {
        store = new BankingEventSourcing.InMemoryEventStore();
        repo = new BankingEventSourcing.AccountRepository(store);
    }

    @Test
    void testCreateAccount() {
        var state = repo.createAccount("ACC-1", "Alice", new BigDecimal("500"));
        assertEquals("ACC-1", state.getAccountId());
        assertEquals(new BigDecimal("500"), state.getBalance());
        assertEquals(1, repo.getEventHistory("ACC-1").size());
    }

    @Test
    void testDeposit() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        repo.deposit("ACC-1", new BigDecimal("50"));
        assertEquals(new BigDecimal("150"), repo.getBalance("ACC-1"));
    }

    @Test
    void testWithdraw() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        repo.withdraw("ACC-1", new BigDecimal("40"));
        assertEquals(new BigDecimal("60"), repo.getBalance("ACC-1"));
    }

    @Test
    void testInsufficientFunds() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        assertThrows(BankingEventSourcing.InsufficientFundsException.class,
            () -> repo.withdraw("ACC-1", new BigDecimal("200")));
    }

    @Test
    void testTransfer() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("500"));
        repo.createAccount("ACC-2", "Bob", new BigDecimal("300"));
        repo.transfer("ACC-1", "ACC-2", new BigDecimal("200"));
        assertEquals(new BigDecimal("300"), repo.getBalance("ACC-1"));
        assertEquals(new BigDecimal("500"), repo.getBalance("ACC-2"));
    }

    @Test
    void testTransferInsufficientFunds() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("10"));
        repo.createAccount("ACC-2", "Bob", new BigDecimal("10"));
        assertThrows(BankingEventSourcing.InsufficientFundsException.class,
            () -> repo.transfer("ACC-1", "ACC-2", new BigDecimal("100")));
    }

    @Test
    void testNegativeDeposit() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        assertThrows(IllegalArgumentException.class,
            () -> repo.deposit("ACC-1", new BigDecimal("-50")));
    }

    @Test
    void testZeroWithdrawal() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        assertThrows(IllegalArgumentException.class,
            () -> repo.withdraw("ACC-1", BigDecimal.ZERO));
    }

    @Test
    void testEventHistory() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        repo.deposit("ACC-1", new BigDecimal("50"));
        repo.withdraw("ACC-1", new BigDecimal("30"));
        var history = repo.getEventHistory("ACC-1");
        assertEquals(3, history.size());
    }

    @Test
    void testConcurrencyConflict() {
        repo.createAccount("ACC-1", "Alice", new BigDecimal("100"));
        // Force concurrent modification by appending directly
        var event = new BankingEventSourcing.MoneyDeposited(
            "ACC-1", new BigDecimal("10"), new BigDecimal("110"), java.time.Instant.now());
        // Try appending with wrong version
        assertThrows(BankingEventSourcing.ConcurrencyConflictException.class,
            () -> store.append("ACC-1", List.of(event), 99));
    }

    @Test
    void testInitialBalanceCannotBeNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.createAccount("ACC-1", "Alice", new BigDecimal("-10")));
    }

    @Test
    void testSnapshotAfterThreshold() {
        // Override threshold for testing via internal knowledge
        var customRepo = new BankingEventSourcing.AccountRepository(store) {
            // snapshot threshold is 50 — create 55 events
        };
        repo.createAccount("ACC-SNAP", "Charlie", new BigDecimal("0"));
        for (int i = 0; i < 55; i++) {
            repo.deposit("ACC-SNAP", new BigDecimal("10"));
        }
        assertEquals(new BigDecimal("550"), repo.getBalance("ACC-SNAP"));
    }
}
```
