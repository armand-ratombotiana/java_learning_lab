# Distributed Transactions: Theory

## ACID in Distributed Systems

### Atomicity
All or nothing across multiple nodes.

### Consistency
Transaction preserves database invariants globally.

### Isolation
Concurrent transactions don't interfere.

### Durability
Committed changes survive failures.

## Transaction Protocols

### 2PC (Two-Phase Commit)
- **Phase 1 (Prepare)**: Coordinator asks all participants to prepare
- **Phase 2 (Commit)**: If all prepare succeeds, coordinator sends commit

### 3PC (Three-Phase Commit)
- Adds pre-commit phase to avoid blocking
- Can tolerate coordinator failure more gracefully

### SAGA
- Long-running transactions as sequence of local transactions
- Compensating transactions for rollback
- No distributed locking
