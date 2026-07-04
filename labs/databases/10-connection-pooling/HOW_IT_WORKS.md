# How Connection Pooling Works (HikariCP)

## Pool Initialization
1. `HikariDataSource` constructor initializes `HikariPool`
2. `HikariPool` creates `ConcurrentBag` for connection storage
3. Fills to `minimumIdle` connections via `PoolEntryCreator`
4. Each connection is wrapped in `PoolEntry` with state tracking
5. A `HouseKeeper` thread starts for pool maintenance

## Borrowing a Connection
1. `HikariPool.getConnection()` → `ConcurrentBag.borrow()`
2. Check idle connections (fast path via `ThreadLocal` cache)
3. If none idle and pool not full: create new connection
4. If none idle and pool full: wait on `handoffQueue` (poll with timeout)
5. On timeout: throw `SQLTransactionTimeoutException`

## Returning a Connection
1. `PoolEntry.recycle()` marks connection as idle
2. `ConcurrentBag.requite()` adds back to idle bag
3. Signal `handoffQueue` to wake waiting consumers

## Maintenance
- `HouseKeeper` runs periodically (default: 30s)
- Evict idle connections exceeding `idleTimeout`
- Evict connections exceeding `maxLifetime`
- Fill pool back to `minimumIdle`
