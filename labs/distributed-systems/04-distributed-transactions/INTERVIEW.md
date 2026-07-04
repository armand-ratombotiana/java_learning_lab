# Distributed Transactions: Interview Questions

## Q1: What are the pros and cons of 2PC?
**A**: Pro: Strong atomicity guarantee. Con: Blocking (coordinator failure blocks participants), slow (2 RTT), not suitable for long transactions.

## Q2: When would you choose SAGA over 2PC?
**A**: When: (1) transactions are long-lived, (2) you need high availability, (3) eventual consistency is acceptable, (4) microservices architecture.

## Q3: How do you implement compensation?
**A**: Each step has a corresponding undo action. Compensations are called in reverse order on failure. Must be idempotent.

## Q4: What is the outbox pattern?
**A**: Write events to a local database table (outbox) within the same transaction as the business operation. A separate process reads and publishes events. Ensures exactly-once delivery.

## Q5: How does Seata implement distributed transactions?
**A**: Seata uses AT (Automatic Transaction) mode with before/after image recording, or TCC (Try-Confirm-Cancel) mode similar to SAGA.
