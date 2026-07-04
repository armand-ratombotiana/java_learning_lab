# Distributed Transactions: Flashcards

## Front: What is 2PC?
**Back**: Two-Phase Commit - coordinator asks all participants to prepare, then commits.

## Front: What is the blocking problem?
**Back**: In 2PC, if coordinator fails after prepare phase, participants block until recovery.

## Front: What is a SAGA?
**Back**: Sequence of local transactions with compensating actions for rollback.

## Front: What is a compensating transaction?
**Back**: An action that semantically undoes a previous transaction.

## Front: What does XA provide?
**Back**: Standardized interface for distributed transaction coordination.

## Front: When to use SAGA over 2PC?
**Back**: Long-running transactions, microservices, when availability > strong consistency.

## Front: What is idempotency?
**Back**: Multiple identical requests produce same result as one request.
