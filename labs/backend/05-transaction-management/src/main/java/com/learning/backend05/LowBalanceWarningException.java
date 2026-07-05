package com.learning.backend05;

/**
 * A non-critical warning exception that should NOT trigger a transaction rollback.
 * Used in the noRollbackFor attribute of @Transactional.
 */
public class LowBalanceWarningException extends RuntimeException {
    public LowBalanceWarningException(String message) {
        super(message);
    }
}
