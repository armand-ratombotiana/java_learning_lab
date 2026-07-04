# Distributed Transactions: Quiz

## Questions
1. What problem does 2PC solve?
2. What is the blocking problem in 2PC?
3. How does 3PC improve over 2PC?
4. What is a SAGA transaction?
5. What is a compensating transaction?
6. What does XA stand for?
7. When should you use SAGA over 2PC?
8. What is the role of a transaction coordinator?
9. How do you handle coordinator failure in 2PC?
10. What makes compensations idempotent?

## Answers
1. Atomic commit across multiple participants
2. Participants block if coordinator fails after prepare
3. Adds pre-commit phase to avoid blocking
4. Long-lived transaction as sequence of local transactions with compensation
5. An action that undoes a previous transaction
6. X/Open XA (eXtended Architecture)
7. When availability is more important than strong consistency
8. Manage the commit/abort decision across participants
9. Write-ahead log for recovery
10. Multiple calls have same effect as one call
