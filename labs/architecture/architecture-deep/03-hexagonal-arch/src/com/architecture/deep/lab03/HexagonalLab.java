package com.architecture.deep.lab03;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HexagonalLab {
    public static void main(String[] args) {
        AccountRepository repo = new InMemoryAccountRepository();
        AccountService service = new AccountServiceImpl(repo);
        ConsoleAdapter adapter = new ConsoleAdapter(service);
        adapter.run();
    }
}

record Account(String id, String owner, long balance) {}

interface AccountRepository {
    Optional<Account> findById(String id);
    void save(Account account);
    List<Account> findAll();
}

interface AccountService {
    Account createAccount(String owner, long initialDeposit);
    Account deposit(String accountId, long amount);
    Account withdraw(String accountId, long amount);
    void transfer(String fromId, String toId, long amount);
    List<Account> listAccounts();
}

class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;

    AccountServiceImpl(AccountRepository repository) { this.repository = repository; }

    public Account createAccount(String owner, long initialDeposit) {
        var account = new Account(UUID.randomUUID().toString().substring(0, 8), owner, initialDeposit);
        repository.save(account);
        return account;
    }

    public Account deposit(String accountId, long amount) {
        var account = repository.findById(accountId).orElseThrow();
        var updated = new Account(account.id(), account.owner(), account.balance() + amount);
        repository.save(updated);
        return updated;
    }

    public Account withdraw(String accountId, long amount) {
        var account = repository.findById(accountId).orElseThrow();
        if (account.balance() < amount) throw new IllegalArgumentException("Insufficient funds");
        var updated = new Account(account.id(), account.owner(), account.balance() - amount);
        repository.save(updated);
        return updated;
    }

    public void transfer(String fromId, String toId, long amount) {
        withdraw(fromId, amount);
        deposit(toId, amount);
    }

    public List<Account> listAccounts() { return repository.findAll(); }
}

class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> store = new ConcurrentHashMap<>();

    public Optional<Account> findById(String id) { return Optional.ofNullable(store.get(id)); }
    public void save(Account account) { store.put(account.id(), account); }
    public List<Account> findAll() { return List.copyOf(store.values()); }
}

class ConsoleAdapter {
    private final AccountService service;

    ConsoleAdapter(AccountService service) { this.service = service; }

    void run() {
        var alice = service.createAccount("Alice", 1000);
        var bob = service.createAccount("Bob", 500);
        service.deposit(alice.id(), 200);
        service.withdraw(bob.id(), 100);
        service.transfer(alice.id(), bob.id(), 300);
        System.out.println("Accounts:");
        service.listAccounts().forEach(a -> System.out.println("  " + a.id() + " | " + a.owner() + " | $" + a.balance()));
    }
}
