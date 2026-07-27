# Mock Interview: Transactions

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Backend Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a transaction management system for a financial trading platform.

---

## Transcript

**Interviewer**: "We're building a stock trading platform. Every trade is a financial transaction: debit cash account, credit stock holding. Must be atomic, consistent, isolated, durable. Design the transaction system."

**Candidate**: "This is a textbook case for database transactions with ACID properties. The core operation is a transfer between two accounts within the same system. For single-region, I'd use a relational database with transactions. For multi-region scaling, I'd use distributed transactions."

**Interviewer**: "Start with single-region. Show me the schema."

**Candidate**: "Two tables: `accounts(account_id, user_id, account_type, balance, version, updated_at)` and `transactions(tx_id, from_account_id, to_account_id, amount, type, status, created_at)`. The trade operation: BEGIN TX → deduct from cash → credit to stock → INSERT transaction record → COMMIT. Use SELECT...FOR UPDATE to lock rows."

**Interviewer**: "How do you handle concurrent trades on the same account?"

**Candidate**: "Optimistic locking: each account has a version number. The UPDATE includes `WHERE version = X`. If the version changed (another transaction modified it), the UPDATE affects 0 rows and we retry. Pessimistic approach: SELECT...FOR UPDATE locks the rows until the transaction commits."

**Interviewer**: "Now scale to multi-region. How do you handle cross-region trades?"

**Candidate**: "This is hard. I'd use the Outbox pattern: the trade service writes the transaction to a local `outbox` table within the same DB transaction. A CDC (Change Data Capture) process picks up the outbox entry and publishes to a cross-region message queue. The target region's consumer processes the event and updates the remote account."

**Interviewer**: "What about consistency guarantees cross-region?"

**Candidate**: "This is eventually consistent cross-region. For strong consistency across regions, I'd need a distributed transaction coordinator (like XA or Saga with compensations). But for trading, the business typically accepts that balance reflects within seconds, not milliseconds. If a customer needs real-time balance, they query the primary region."

**Interviewer**: "How do you handle transaction failures?"

**Candidate**: "Retry with exponential backoff for transient failures. For permanent failures: dead letter queue for manual review. The system maintains a transaction state machine: pending → processing → completed/failed. Failed transactions have compensating transactions to reverse any partial effects."

---

## Key Takeaways

- **ACID transactions**: Account balances demand atomicity
- **Pessimistic vs optimistic locking**: SELECT FOR UPDATE vs version-based
- **Outbox pattern**: Reliable cross-service event publishing
- **Cross-region eventual consistency**: Trade consistency for availability
- **Transaction state machine**: Track lifecycle, enable compensation
