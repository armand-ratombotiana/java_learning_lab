package com.learning.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.function.*;

@SpringBootApplication
class HexagonalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HexagonalApplication.class, args);
    }
}

// Domain (Core)
class Account {
    private final String id;
    private String ownerName;
    private double balance;
    private AccountStatus status;

    Account(String id, String ownerName, double initialBalance) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.status = AccountStatus.ACTIVE;
    }

    void deposit(double amount) {
        if (status != AccountStatus.ACTIVE) throw new IllegalStateException("Account is " + status);
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    void withdraw(double amount) {
        if (status != AccountStatus.ACTIVE) throw new IllegalStateException("Account is " + status);
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
    }

    String id() { return id; }
    String ownerName() { return ownerName; }
    double balance() { return balance; }
    AccountStatus status() { return status; }
    void close() { status = AccountStatus.CLOSED; }
}

enum AccountStatus { ACTIVE, CLOSED }

// Ports (Interfaces)
interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(String id);
}

interface NotificationPort {
    void notifyDeposit(String accountId, double amount);
    void notifyWithdrawal(String accountId, double amount);
}

// Use Case (Application Service)
class AccountService {
    private final AccountRepository repository;
    private final NotificationPort notifications;

    AccountService(AccountRepository repository, NotificationPort notifications) {
        this.repository = repository;
        this.notifications = notifications;
    }

    String createAccount(String ownerName, double initialBalance) {
        Account account = new Account("ACC-" + System.currentTimeMillis(), ownerName, initialBalance);
        repository.save(account);
        return account.id();
    }

    void deposit(String id, double amount) {
        Account account = repository.findById(id).orElseThrow();
        account.deposit(amount);
        repository.save(account);
        notifications.notifyDeposit(id, amount);
    }

    void withdraw(String id, double amount) {
        Account account = repository.findById(id).orElseThrow();
        account.withdraw(amount);
        repository.save(account);
        notifications.notifyWithdrawal(id, amount);
    }

    Account getAccount(String id) { return repository.findById(id).orElseThrow(); }
}

// Adapters (Infrastructure)
class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> store = new HashMap<>();
    public void save(Account account) { store.put(account.id(), account); }
    public Optional<Account> findById(String id) { return Optional.ofNullable(store.get(id)); }
}

class ConsoleNotification implements NotificationPort {
    public void notifyDeposit(String accountId, double amount) {
        System.out.println("Notification: Deposited $" + amount + " to " + accountId);
    }
    public void notifyWithdrawal(String accountId, double amount) {
        System.out.println("Notification: Withdrew $" + amount + " from " + accountId);
    }
}

// REST Adapter (Primary)
@RestController
@RequestMapping("/hexagonal/accounts")
class AccountController {

    private final AccountService service = new AccountService(
            new InMemoryAccountRepository(), new ConsoleNotification());

    @PostMapping
    public String create(@RequestParam String owner, @RequestParam double balance) {
        return "Account created: " + service.createAccount(owner, balance);
    }

    @PostMapping("/{id}/deposit")
    public String deposit(@PathVariable String id, @RequestParam double amount) {
        service.deposit(id, amount);
        return "Deposited $" + amount + " to " + id;
    }

    @PostMapping("/{id}/withdraw")
    public String withdraw(@PathVariable String id, @RequestParam double amount) {
        service.withdraw(id, amount);
        return "Withdrew $" + amount + " from " + id;
    }

    @GetMapping("/{id}")
    public AccountInfo get(@PathVariable String id) {
        Account account = service.getAccount(id);
        return new AccountInfo(account.id(), account.ownerName(), account.balance(), account.status());
    }

    record AccountInfo(String id, String owner, double balance, AccountStatus status) {}
}
