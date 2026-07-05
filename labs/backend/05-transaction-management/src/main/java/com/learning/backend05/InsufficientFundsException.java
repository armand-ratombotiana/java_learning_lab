package com.learning.backend05;

/**
 * Thrown when an account has insufficient funds for a transaction.
 * Used to demonstrate the rollbackFor attribute of @Transactional.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
