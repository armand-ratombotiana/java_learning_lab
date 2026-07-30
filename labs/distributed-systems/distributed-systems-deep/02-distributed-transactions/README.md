# 02 - Distributed Transactions

## Topics Covered
- Two-Phase Commit (2PC) — protocol, failure modes, blocking problem
- Three-Phase Commit (3PC) — non-blocking, timeout-based
- Saga Pattern — choreography vs orchestration
- Compensating transactions
- TCC (Try-Confirm/Cancel)
- XA Transactions (JTA interface)

## Goal
Understand the landscape of distributed transaction protocols and when to use each.

## Exercises

1. Implement a 2PC coordinator with prepare/commit/abort and simulate coordinator failure.
2. Implement a Saga orchestration with compensation handlers.
3. Implement a TCC service with Try/Confirm/Cancel phases.
4. Compare blocking behavior of 2PC vs 3PC under network partitions.