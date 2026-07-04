# Internals: HikariCP

## Key Classes

| Class | Role |
|---|---|
| `HikariDataSource` | `DataSource` implementation, holds `HikariPool` |
| `HikariConfig` | Configuration holder (copy-on-write) |
| `HikariPool` | Core pool logic, connection lifecycle |
| `ConcurrentBag` | Lock-free bag of `PoolEntry` objects |
| `PoolEntry` | Wraps `Connection` with state, statistics |
| `PoolEntryCreator` | Background connection creator |
| `HouseKeeper` | Scheduled maintenance task |
| `ProxyConnection` | JDBC proxy that intercepts `close()` |
| `FastList` | Optimized ArrayList (removes range checking) |

## ConcurrentBag Design
- Uses `ThreadLocal` cache for hot connections (fast path)
- Shared `CopyOnWriteArrayList` for cold connections
- CAS operations for state transitions
- No synchronized blocks in normal path

## Proxy Layer
HikariCP wraps every connection in a `ProxyConnection`:
- `close()` → returns connection to pool, doesn't close TCP socket
- `isClosed()` → checks pool state
- Statement/callable statement creation wrapped for leak tracking

## Default Configuration (Spring Boot)
```properties
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```
