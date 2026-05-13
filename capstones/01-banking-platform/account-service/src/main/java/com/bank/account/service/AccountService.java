package com.bank.account.service;

import com.bank.account.entity.Account;
import com.bank.account.repository.AccountRepository;
import com.bank.common.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Account createAccount(UUID userId, String accountType, BigDecimal initialBalance) {
        log.info("Creating account for user: {} type: {} balance: {}", userId, accountType, initialBalance);
        Account account = new Account(userId, accountType, initialBalance);
        account = accountRepository.save(account);
        
        AccountCreatedEvent event = AccountCreatedEvent.create(
            account.getId(), userId, accountType, initialBalance
        );
        kafkaTemplate.send("account.created", account.getId().toString(), event);
        
        log.info("Account created: {}", account.getId());
        return account;
    }

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    @Transactional
    public Account deposit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
        account.deposit(amount);
        log.info("Deposited {} to account {}", amount, accountId);
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
        account.withdraw(amount);
        log.info("Withdrew {} from account {}", amount, accountId);
        return accountRepository.save(account);
    }

    public List<Account> getUserAccounts(UUID userId) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getUserId().equals(userId))
                .toList();
    }
}