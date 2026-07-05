# Common Mistakes: Banking Platform

- **Not handling idempotency correctly**: Same payment request submitted twice causes duplicate charges. Always check idempotency key on the write path, not just the read path.
- **Race conditions on balance**: Reading balance, checking sufficiency, and deducting must be atomic. Use database-level locks or optimistic versioning; never read-then-write without protection.
- **Missing compensating transactions**: When a saga fails mid-way, you must emit compensating events. A failed credit still needs a debit reversal.
- **Synchronous fraud checks**: Blocking the payment thread for ML inference adds latency. Use async pattern with timeout and fallback to rule-only evaluation.
- **Leaking PII in logs**: Never log full PAN, CVV, or raw card numbers. Mask or tokenize before any log output.
- **Insufficient audit trail**: Every balance change must record who, what, when, why. Missing audit data leads to regulatory non-compliance.
