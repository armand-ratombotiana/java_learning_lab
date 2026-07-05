# Theory: Banking Platform

## Microservice Architecture
The banking platform follows a microservice architecture pattern where each bounded context (accounts, payments, fraud, notifications) runs as an independent service. This enables independent deployability, scaling, and fault isolation.

## CQRS (Command Query Responsibility Segregation)
Commands (writes) and queries (reads) use different models. Deposit/withdrawal commands go through a write-optimized path, while balance inquiries and transaction history use read-optimized projections.

## Event Sourcing
State changes are stored as an append-only log of events. The current account balance is derived by replaying all events for that account. This provides a complete audit trail and allows time-travel queries.

## Saga Pattern
Distributed transactions across services (e.g., transfer between accounts) use the Saga pattern with compensating actions. If a debit succeeds but credit fails, a compensating credit-back event is emitted.

## CAP Theorem
The platform prioritizes availability and partition tolerance (AP) over strict consistency for non-critical reads. Account balances use eventual consistency with conflict resolution via last-write-wins.

## Two-Phase Commit (2PC)
For critical operations like inter-account transfers, a lightweight 2PC coordinator ensures atomicity across account-service and payment-service.
