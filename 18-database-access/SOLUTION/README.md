# Database Access Solution

Reference implementation for JDBC, connection pooling, and transactions.

## JDBC
- `JdbcConnection` for connection management
- `JdbcTemplate` for CRUD operations
- `RowMapper` for result mapping

## Connection Pooling
- `HikariDataSource` implementation
- Pool configuration (max pool size, min idle)
- Connection lifecycle management

## Transaction Management
- `JdbcTransactionManager` for transaction control
- Begin, commit, rollback operations
- Transaction isolation levels

## Batch Operations
- `BatchOperations` for bulk inserts/updates
- Callable statements for stored procedures

## Key Classes
- `PoolConfig`: Connection pool configuration
- `IsolationLevel`: Transaction isolation constants

## Test Coverage: 20+ tests