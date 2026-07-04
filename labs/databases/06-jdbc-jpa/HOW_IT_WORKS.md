# How JDBC & JPA Work

## JDBC Connection Lifecycle
```
DataSource → getConnection() → Connection → PreparedStatement → executeQuery()
                                                               → executeUpdate()
Connection.close() ← ResultSet ← executeQuery()
```

## Transaction Flow
```
conn.setAutoCommit(false)     → Start transaction
stmt.executeUpdate(...)       → DML operation (invisible to others)
stmt.executeUpdate(...)       → Another DML
conn.commit()                 → Make permanent, visible to others
// or conn.rollback()         → Undo all changes
conn.setAutoCommit(true)      → Reset
```

## Hibernate Flush Cycle
```
1. Begin transaction (or @Transactional)
2. entityManager.persist(user)   → entity becomes managed
3. user.setName("New")           → dirty checking marks entity
4. entityManager.createQuery(...) → triggers flush (implicit or explicit)
   └── Hibernate generates: UPDATE users SET name=? WHERE id=?
5. commit()                      → JDBC commit
```

## Dirty Checking Mechanism
```
On flush():
  For each managed entity:
    1. Get current snapshot (stored when entity was loaded)
    2. Compare all fields with current values
    3. Generate UPDATE for changed fields only
```

## First-Level Cache (Persistence Context)
```
em.find(User.class, 1) → Check persistence context
                           └── Miss → Database query → Store in context → Return
em.find(User.class, 1) → Check persistence context
                           └── Hit → Return cached (no SQL)
```

## Lazy Loading
```
em.find(Order.class, 1) → SQL: SELECT * FROM orders WHERE id=1
order.getCustomer()     → SQL: SELECT * FROM customers WHERE id=? (only if LAZY)
                           └── Requires open session / active transaction
```

## Connection Pooling (HikariCP)
```
Thread A: getConnection() → borrow from pool → use → return to pool
Thread B: getConnection() → pool has idle → borrow → use → return
Thread C: getConnection() → pool empty → wait (maxWait) → timeout or get
```
