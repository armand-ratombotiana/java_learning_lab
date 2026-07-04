# Internals: JDBC & JPA

## JDBC Driver Types

| Type | Description | Example |
|---|---|---|
| 1 | JDBC-ODBC bridge | Deprecated |
| 2 | Native API (partial JNI) | Oracle OCI |
| 3 | Network middleware | Postgres via pgpool |
| 4 | Pure Java, direct socket | PostgreSQL, MySQL Connector/J |

## Hibernate Session Internals

### Persistence Context (first-level cache)
```
Map<EntityKey, Object> entitiesByKey          // Managed entities
Map<Object, EntityEntry> entityEntries         // Entity metadata (status, version)
List<ActionQueue> actionQueue                  // Ordered insert/update/delete
```

### Action Queue Ordering
```
1. Orphan deletion
2. Entity inserts (in order of persist)
3. Entity updates
4. Collection updates
5. Entity deletions
6. Collection deletions
```

## Hibernate SQL Generation

### INSERT
```sql
-- Before: flush dirty check detects new entity
INSERT INTO users (name, email, created_at)
VALUES (?, ?, ?)
```

### UPDATE (dirty check)
```sql
-- Only changed columns in SET clause
UPDATE users SET name = ? WHERE id = ? AND version = ?
```

### DELETE
```sql
DELETE FROM users WHERE id = ? AND version = ?
```

## Batch Writing

Hibernate combines multiple statements into JDBC batch:
```
INSERT INTO users VALUES (?, ?)      ← batch 1
INSERT INTO users VALUES (?, ?)      ← batch 1
INSERT INTO users VALUES (?, ?)      ← batch 1
-- executeBatch() → one network round-trip
```

## Optimistic Locking with @Version
```
UPDATE users SET name=?, version=2 WHERE id=1 AND version=1
-- If 0 rows updated → OptimisticLockException
-- Thread 1 wins, Thread 2 must retry
```

## HikariCP Internals

```
ConcurrentBag:
  - threadList: ThreadLocal cache for fast borrow
  - sharedList: CopyOnWriteArrayList for cross-thread borrow
  - handoffQueue: SynchronousQueue for thread handoff
  
PoolEntry:
  - connection: actual JDBC Connection
  - state: (0 = idle, 1 = borrowed, -1 = evicted)
  - lastAccess: timestamp for leak detection
```
