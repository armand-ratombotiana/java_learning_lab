package com.learning.backend05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer demonstrating @Transactional with various attributes.
 *
 * @Transactional key attributes:
 * - propagation:  defines transaction boundary (REQUIRED, REQUIRES_NEW, MANDATORY, etc.)
 * - isolation:    degree of isolation from concurrent transactions (READ_COMMITTED, etc.)
 * - rollbackFor:  specifies which exceptions trigger a rollback (by default only RuntimeException)
 * - noRollbackFor: exceptions that should NOT trigger rollback
 * - readOnly:     optimization hint for read-only transactions
 * - timeout:      maximum seconds before automatic rollback
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Transfers money between two accounts.
     *
     * propagation = Propagation.REQUIRED (default): reuses existing transaction or
     *   creates a new one.
     * isolation = Isolation.READ_COMMITTED: prevents dirty reads.
     * rollbackFor = InsufficientFundsException.class: ensures rollback on
     *   insufficient funds even though it extends RuntimeException (which already
     *   triggers rollback by default — shown here for clarity).
     * noRollbackFor = LowBalanceWarningException.class: does NOT rollback if
     *   this specific exception occurs.
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = InsufficientFundsException.class,
        noRollbackFor = LowBalanceWarningException.class
    )
    public TransferResult transfer(String fromAccount, String toAccount, double amount) {
        log.info("Transferring ${} from {} to {}", amount, fromAccount, toAccount);

        Account source = accountRepository.findByAccountNumber(fromAccount)
            .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + fromAccount));
        Account destination = accountRepository.findByAccountNumber(toAccount)
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + toAccount));

        // Debit source — may throw InsufficientFundsException (rollback)
        source.debit(amount);
        accountRepository.save(source);

        // Credit destination
        destination.credit(amount);
        accountRepository.save(destination);

        log.info("Transfer completed: ${} from {} to {}", amount, fromAccount, toAccount);

        // Check if the source balance is low after transfer
        if (source.getBalance() < 100) {
            throw new LowBalanceWarningException("Low balance warning for account " + fromAccount);
        }

        return new TransferResult(fromAccount, toAccount, amount, "SUCCESS");
    }

    /**
     * Nested transaction demonstration.
     * Propagation.REQUIRES_NEW suspends the outer transaction and creates a
     * new independent one. If the inner transaction commits but the outer
     * rolls back, the inner changes persist.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditLog(String fromAccount, String toAccount, double amount) {
        log.info("Audit log: ${} transferred from {} to {}", amount, fromAccount, toAccount);
    }

    /**
     * Read-only transaction — useful for optimizing database connections.
     */
    @Transactional(readOnly = true)
    public Account getAccountInfo(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    /**
     * Result record for transfer operations.
     */
    public record TransferResult(String fromAccount, String toAccount, double amount, String status) {}
}
