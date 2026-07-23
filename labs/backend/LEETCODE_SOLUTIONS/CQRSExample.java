// CQRSExample.java — Axon Framework CQRS and Event Sourcing
// Demonstrates Command Query Responsibility Segregation pattern

package com.backend.academy.leetcode;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

// ============================================================
// Command Side (Write Model)
// ============================================================

// --- Commands ---
record CreateAccountCommand(String accountId, String ownerName, BigDecimal initialBalance) {}
record DepositMoneyCommand(String accountId, BigDecimal amount) {}
record WithdrawMoneyCommand(String accountId, BigDecimal amount) {}
record CloseAccountCommand(String accountId) {}

// --- Events ---
record AccountCreatedEvent(String accountId, String ownerName, BigDecimal initialBalance, Instant createdOn) {}
record MoneyDepositedEvent(String accountId, BigDecimal amount, BigDecimal newBalance, Instant timestamp) {}
record MoneyWithdrawnEvent(String accountId, BigDecimal amount, BigDecimal newBalance, Instant timestamp) {}
record AccountClosedEvent(String accountId, Instant closedOn) {}

// --- Aggregate ---
@Aggregate
class BankAccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private String ownerName;
    private BigDecimal balance;
    private boolean active;

    protected BankAccountAggregate() {} // Axon requires empty constructor

    @CommandHandler
    public BankAccountAggregate(CreateAccountCommand cmd) {
        apply(new AccountCreatedEvent(
            cmd.accountId(), cmd.ownerName(), cmd.initialBalance(), Instant.now()));
    }

    @CommandHandler
    public void handle(DepositMoneyCommand cmd) {
        if (!active) throw new IllegalStateException("Account is closed");
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");
        apply(new MoneyDepositedEvent(cmd.accountId(), cmd.amount(),
            balance.add(cmd.amount()), Instant.now()));
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand cmd) {
        if (!active) throw new IllegalStateException("Account is closed");
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (balance.compareTo(cmd.amount()) < 0)
            throw new IllegalStateException("Insufficient funds");
        apply(new MoneyWithdrawnEvent(cmd.accountId(), cmd.amount(),
            balance.subtract(cmd.amount()), Instant.now()));
    }

    @CommandHandler
    public void handle(CloseAccountCommand cmd) {
        if (!active) throw new IllegalStateException("Account is already closed");
        apply(new AccountClosedEvent(cmd.accountId(), Instant.now()));
    }

    // --- Event Sourcing Handlers ---
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.accountId();
        this.ownerName = event.ownerName();
        this.balance = event.initialBalance();
        this.active = true;
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = event.newBalance();
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = event.newBalance();
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.active = false;
    }
}

// ============================================================
// Query Side (Read Model)
// ============================================================

// --- Query Models ---
record AccountSummary(String accountId, String ownerName, BigDecimal balance, boolean active, Instant createdOn) {}
record TransactionHistory(String accountId, String transactionType, BigDecimal amount, BigDecimal balanceAfter, Instant timestamp) {}

// --- Queries ---
record GetAccountQuery(String accountId) {}
record GetAllAccountsQuery() {}
record GetTransactionsQuery(String accountId, int limit) {}

// --- Query Repository (in-memory) ---
@Service
class AccountQueryRepository {
    private final Map<String, AccountSummary> accountStore = new ConcurrentHashMap<>();
    private final List<TransactionHistory> transactionStore = Collections.synchronizedList(new ArrayList<>());

    @QueryHandler
    public AccountSummary handle(GetAccountQuery query) {
        AccountSummary account = accountStore.get(query.accountId());
        if (account == null) throw new NoSuchElementException("Account not found: " + query.accountId());
        return account;
    }

    @QueryHandler
    public List<AccountSummary> handle(GetAllAccountsQuery query) {
        return List.copyOf(accountStore.values());
    }

    @QueryHandler
    public List<TransactionHistory> handle(GetTransactionsQuery query) {
        return transactionStore.stream()
            .filter(t -> t.accountId().equals(query.accountId()))
            .sorted(Comparator.comparing(TransactionHistory::timestamp).reversed())
            .limit(query.limit())
            .collect(Collectors.toList());
    }

    // Event handlers to update read model
    public void on(AccountCreatedEvent event) {
        accountStore.put(event.accountId(), new AccountSummary(
            event.accountId(), event.ownerName(), event.initialBalance(), true, event.createdOn()));
    }

    public void on(MoneyDepositedEvent event) {
        accountStore.computeIfPresent(event.accountId(), (id, summary) ->
            new AccountSummary(summary.accountId(), summary.ownerName(), event.newBalance(), true, summary.createdOn()));
        transactionStore.add(new TransactionHistory(event.accountId(), "DEPOSIT", event.amount(), event.newBalance(), event.timestamp()));
    }

    public void on(MoneyWithdrawnEvent event) {
        accountStore.computeIfPresent(event.accountId(), (id, summary) ->
            new AccountSummary(summary.accountId(), summary.ownerName(), event.newBalance(), true, summary.createdOn()));
        transactionStore.add(new TransactionHistory(event.accountId(), "WITHDRAWAL", event.amount(), event.newBalance(), event.timestamp()));
    }

    public void on(AccountClosedEvent event) {
        accountStore.computeIfPresent(event.accountId(), (id, summary) ->
            new AccountSummary(summary.accountId(), summary.ownerName(), summary.balance(), false, summary.createdOn()));
    }
}

// ============================================================
// CQRS Service (Orchestration)
// ============================================================

@Service
class CQRSService {
    private final CommandGateway commandGateway;
    private final AccountQueryRepository queryRepository;

    public CQRSService(CommandGateway commandGateway, AccountQueryRepository queryRepository) {
        this.commandGateway = commandGateway;
        this.queryRepository = queryRepository;
    }

    // Command operations
    public String createAccount(String ownerName, BigDecimal initialBalance) {
        String accountId = UUID.randomUUID().toString();
        commandGateway.send(new CreateAccountCommand(accountId, ownerName, initialBalance));
        return accountId;
    }

    public void deposit(String accountId, BigDecimal amount) {
        commandGateway.send(new DepositMoneyCommand(accountId, amount));
    }

    public void withdraw(String accountId, BigDecimal amount) {
        commandGateway.send(new WithdrawMoneyCommand(accountId, amount));
    }

    public void closeAccount(String accountId) {
        commandGateway.send(new CloseAccountCommand(accountId));
    }

    // Query operations
    public AccountSummary getAccount(String accountId) {
        return queryRepository.handle(new GetAccountQuery(accountId));
    }

    public List<AccountSummary> getAllAccounts() {
        return queryRepository.handle(new GetAllAccountsQuery());
    }

    public List<TransactionHistory> getTransactions(String accountId, int limit) {
        return queryRepository.handle(new GetTransactionsQuery(accountId, limit));
    }
}

// --- Command Gateway (simplified) ---
interface CommandGateway {
    void send(Object command);
}

// === LeetCode-Style Problems ===

/*
 * LeetCode 706: Design HashMap
 * Analogy: CQRS uses separate read/write models
 */
class MyHashMap {
    private static final int SIZE = 10000;
    private java.util.LinkedList<Entry>[] buckets;

    record Entry(int key, int value) {}

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = new java.util.LinkedList[SIZE];
    }

    public void put(int key, int value) {
        int idx = hash(key);
        if (buckets[idx] == null) {
            buckets[idx] = new java.util.LinkedList<>();
        }
        for (Entry e : buckets[idx]) {
            if (e.key() == key) {
                buckets[idx].remove(e);
                break;
            }
        }
        buckets[idx].add(new Entry(key, value));
    }

    public int get(int key) {
        int idx = hash(key);
        if (buckets[idx] == null) return -1;
        for (Entry e : buckets[idx]) {
            if (e.key() == key) return e.value();
        }
        return -1;
    }

    public void remove(int key) {
        int idx = hash(key);
        if (buckets[idx] == null) return;
        buckets[idx].removeIf(e -> e.key() == key);
    }

    private int hash(int key) { return Integer.hashCode(key) % SIZE; }
}

/*
 * LeetCode 208: Implement Trie (Prefix Tree)
 * Analogy: Event store index for fast lookup
 */
class Trie {
    private TrieNode root;

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    public Trie() { root = new TrieNode(); }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private TrieNode searchPrefix(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}

/*
 * LeetCode 380: Insert Delete GetRandom O(1)
 */
class RandomizedSet {
    private Map<Integer, Integer> valToIndex;
    private List<Integer> values;
    private Random rand;

    public RandomizedSet() {
        valToIndex = new HashMap<>();
        values = new ArrayList<>();
        rand = new Random();
    }

    public boolean insert(int val) {
        if (valToIndex.containsKey(val)) return false;
        valToIndex.put(val, values.size());
        values.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!valToIndex.containsKey(val)) return false;
        int idx = valToIndex.get(val);
        int lastVal = values.get(values.size() - 1);
        values.set(idx, lastVal);
        valToIndex.put(lastVal, idx);
        values.remove(values.size() - 1);
        valToIndex.remove(val);
        return true;
    }

    public int getRandom() {
        return values.get(rand.nextInt(values.size()));
    }
}
