# Step by Step: Banking Platform

## Making a Payment (End-to-End)

1. **POST /api/v1/payments** with `{fromAccount, toAccount, amount, currency}`
2. Gateway validates JWT token, applies rate limit check
3. Payment Service generates idempotency key `= HMAC(tx fingerprint)`
4. Payment Service checks idempotency cache — if exists, return cached result
5. Account Service validates account exists, balance >= amount + pending holds
6. Account Service places hold: `pendingHolds += amount`
7. Fraud Service runs rules: amount < $10,000; velocity < 5 tx/min; geo match
8. Fraud Service calls ML model: predict fraud probability
9. If fraud score < threshold:
   - Commit transaction in Payment Service (status = COMPLETED)
   - Account Service finalizes: `balance -= amount`, `pendingHolds -= amount`
   - Credit receiving account
   - Emit `payment.completed` event
10. If fraud score >= threshold:
    - Reject transaction (status = FAILED_FRAUD)
    - Account Service releases hold: `pendingHolds -= amount`
    - Emit `payment.rejected` event
11. Notification Service listens for completion/rejection, sends alert
