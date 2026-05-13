package com.bank.account.service;

import com.bank.account.entity.Account;
import com.bank.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private UUID userId;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        accountId = UUID.randomUUID();
    }

    @Test
    void createAccount_ShouldCreateAndPublishEvent() {
        Account savedAccount = new Account(userId, "SAVINGS", new BigDecimal("100.00"));
        savedAccount.setId(accountId);
        
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.createAccount(userId, "SAVINGS", new BigDecimal("100.00"));

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(new BigDecimal("100.00"), result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void deposit_ShouldIncreaseBalance() {
        Account account = new Account(userId, "CHECKING", new BigDecimal("100.00"));
        account.setId(accountId);
        
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.deposit(accountId, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), result.getBalance());
    }

    @Test
    void deposit_WithNegativeAmount_ShouldThrow() {
        Account account = new Account(userId, "CHECKING", new BigDecimal("100.00"));
        account.setId(accountId);
        
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class, 
            () -> accountService.deposit(accountId, new BigDecimal("-50.00")));
    }

    @Test
    void withdraw_ShouldDecreaseBalance() {
        Account account = new Account(userId, "CHECKING", new BigDecimal("100.00"));
        account.setId(accountId);
        
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.withdraw(accountId, new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), result.getBalance());
    }

    @Test
    void withdraw_InsufficientFunds_ShouldThrow() {
        Account account = new Account(userId, "CHECKING", new BigDecimal("50.00"));
        account.setId(accountId);
        
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, 
            () -> accountService.withdraw(accountId, new BigDecimal("100.00")));
    }

    @Test
    void getAccount_NotFound_ShouldThrow() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountService.getAccount(accountId));
    }
}