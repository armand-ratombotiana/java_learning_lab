package com.learning.backend05;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AccountRepository {
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public Account save(Account account) {
        if (account.getId() == null) {
            account.setId(idSequence.getAndIncrement());
        }
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }
}