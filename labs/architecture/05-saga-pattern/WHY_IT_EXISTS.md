# Why Saga Pattern Exists

## Historical Problems
- **Distributed Transactions (2PC)** - Slow, blocking, not suitable for microservices
- **Data inconsistency** - Without coordination, partial failures leave data in invalid states
- **Lack of recovery** - No mechanism to undo partial operations
- **Tight coupling** - Direct service calls create temporal coupling

## Business Drivers
- Need for cross-service data consistency without 2PC
- Long-running business transactions
- Complex workflows spanning multiple services
- Requirement for audit trail of distributed operations
- Recovery from partial failures

## When Saga Makes Sense
- Transactions spanning multiple microservices
- Long-running business processes
- Need for compensating actions on failure
- Systems requiring audit trail of distributed operations
- Migration from monoliths with existing transactions

## Alternatives
- **Distributed Transactions (2PC/XA)** - Too slow, blocking
- **BASE (Basically Available, Soft state, Eventually consistent)** - No recovery mechanism
- **TCC (Try-Confirm/Cancel)** - Specialized for resources
