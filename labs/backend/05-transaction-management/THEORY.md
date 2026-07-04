# Theory: Transaction Management

## ACID Properties
Transactions guarantee ACID:
- **Atomicity**: All or nothing execution
- **Consistency**: Database remains valid after transaction
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed changes persist

## Transaction Propagation
Determines how transactions relate to each other:
- **REQUIRED** (default): Join current or create new
- **REQUIRES_NEW**: Suspend current, create new
- **NESTED**: Execute within nested transaction (savepoint)
- **MANDATORY**: Must be called within a transaction
- **NEVER**: Must not be in a transaction
- **SUPPORTS**: Join if exists, run without if not
- **NOT_SUPPORTED**: Suspend current, run without

## Isolation Levels
- **READ_UNCOMMITTED**: Lowest isolation, dirty reads possible
- **READ_COMMITTED**: No dirty reads (PostgreSQL default)
- **REPEATABLE_READ**: No dirty/non-repeatable reads
- **SERIALIZABLE**: Highest isolation, full protection

## @Transactional Behavior
```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.READ_COMMITTED,
    timeout = 30,
    rollbackFor = {DataAccessException.class},
    noRollbackFor = {BusinessException.class}
)
```
