# Databases Interview Questions

## Section 1: JDBC

### Q1: Explain the JDBC architecture
**Answer:** JDBC has layers:
1. **Application** - Uses JDBC API
2. **JDBC Manager** - Loads drivers, connects
3. **Driver** - Vendor-specific implementation
4. **Database** - Actual data store

Key classes: DriverManager, Connection, Statement, PreparedStatement, ResultSet

---

### Q2: What is the difference between Statement, PreparedStatement, and CallableStatement?
**Answer:**
- **Statement:** Simple SQL, recreated each use (risk of SQL injection)
- **PreparedStatement:** Pre-compiled, supports parameters, cached, prevents injection
- **CallableStatement:** Calls stored procedures

---

### Q3: How do you handle transactions in JDBC?
**Answer:**
```java
conn.setAutoCommit(false);
try {
    // SQL operations
    conn.commit();
} catch (Exception e) {
    conn.rollback();
} finally {
    conn.setAutoCommit(true);
}
```

---

### Q4: What is the purpose of try-with-resources in JDBC?
**Answer:** Automatically closes Connection, Statement, and ResultSet after use, ensuring resources are freed even on exceptions.

---

### Q5: How do you execute batch operations in JDBC?
**Answer:**
```java
PreparedStatement stmt = conn.prepareStatement("INSERT INTO t VALUES (?)");
for (Value v : values) {
    stmt.setString(1, v);
    stmt.addBatch();
}
stmt.executeBatch();
```

---

## Section 2: JPA

### Q6: What is JPA? How does it differ from JDBC?
**Answer:** JPA is an ORM specification; JDBC is low-level database access. JPA maps Java objects to database tables automatically; JDBC requires manual SQL.

---

### Q7: Explain JPA entity lifecycle states
**Answer:**
- **New:** New object, not managed
- **Managed:** Synced with database
- **Removed:** Scheduled for deletion
- **Detached:** No longer synchronized

---

### Q8: What is the difference between @OneToOne, @OneToMany, and @ManyToMany?
**Answer:**
- **@OneToOne:** Single related entity (one row per row)
- **@OneToMany:** Multiple related entities (parent -> children)
- **@ManyToMany:** Multiple in both directions (students <-> courses)

---

### Q9: What is fetch type? When would you use EAGER vs LAZY?
**Answer:** Fetch type determines when related entities load:
- **LAZY:** On demand (default for @OneToMany, @ManyToMany) - better performance
- **EAGER:** Immediately (default for @OneToOne, @ManyToOne) - ensures data available

Use EAGER when you always need related data; use LAZY otherwise.

---

### Q10: How do you write custom queries in JPA?
**Answer:**
1. Method naming: `findByEmail(String email)`
2. @Query with JPQL: `@Query("SELECT u FROM User u WHERE u.email = :email")`
3. @Query with native SQL: `@Query(value="...", nativeQuery=true)`

---

## Section 3: Transactions

### Q11: What is transaction propagation?
**Answer:** Defines how transactions interact when one method calls another:
- **REQUIRED:** Use existing or create new (default)
- **REQUIRES_NEW:** Always create new transaction
- **SUPPORTS:** Use if exists, otherwise not
- **NOT_SUPPORTED:** Run without transaction

---

### Q12: What is isolation level?
**Answer:** Controls how concurrent transactions see each other:
- **READ_UNCOMMITTED:** Dirty reads possible
- **READ_COMMITTED:** No dirty reads
- **REPEATABLE_READ:** Consistent reads
- **SERIALIZABLE:** Full isolation

Higher isolation = less concurrency.

---

### Q13: Explain optimistic vs pessimistic locking
**Answer:**
- **Optimistic:** Use @Version field; check on update; throw exception if changed
- **Pessimistic:** SELECT FOR UPDATE; lock immediately; prevents concurrent changes

Use optimistic for mostly reads; pessimistic for mostly writes.

---

### Q14: What is @Transactional(readOnly = true)?
**Answer:** Hint to database and JPA that transaction only reads data:
- May use read-only connection
- Skip dirty checking
- Enable optimizations

---

### Q15: What happens if @Transactional is not used?
**Answer:** Each statement auto-commits individually; no atomic operations; partial changes possible on errors.

---

## Section 4: Connection Pooling

### Q16: Why use connection pooling?
**Answer:** Creating connections is expensive; pooling reuses connections:
- Faster response
- Limited connections to database
- Resource management

---

### Q17: What is HikariCP? Why is it preferred?
**Answer:** HikariCP is a fast, lightweight connection pool:
- Faster than C3P0, DBCP
- Default in Spring Boot
- Fewer connections, better performance

---

### Q18: What do pool size settings control?
**Answer:**
- **maximumPoolSize:** Max connections in pool
- **minimumIdle:** Min connections to keep
- **connectionTimeout:** Max wait for connection
- **idleTimeout:** Max idle time before removal

---

### Q19: What is connection leak?
**Answer:** Connection obtained but not returned to pool (forgotten close). HikariCP detects via `leakDetectionThreshold`.

---

### Q20: How do you configure HikariCP?
**Answer:**
```java
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(10);
config.setMinimumIdle(2);
config.setConnectionTimeout(30000);
// ... more settings
DataSource ds = new HikariDataSource(config);
```

---

## Section 5: Performance

### Q21: What is N+1 problem?
**Answer:** Fetching parent and N children causes N+1 queries:
- 1 query for parent
- N queries for each child

Solution: JOIN FETCH, EntityGraph, batch fetching.

---

### Q22: How do you optimize JPA queries?
**Answer:**
- Use JOIN FETCH for required relationships
- Use EntityGraph to control what loads
- Use projections instead of full entities for read-only
- Use pagination instead of fetching all
- Add database indexes

---

### Q23: What is Entity Graph?
**Answer:** Defines which related entities to fetch:
```java
@EntityGraph(attributePaths = {"customer", "items"})
@Query("SELECT o FROM Order o")
List<Order> findAllWithDetails();
```

---

### Q24: What is the difference between getReference() and find()?
**Answer:**
- **find():** Hits database, returns entity (or null)
- **getReference():** Returns proxy, only queries when accessed (throws if not exists)

Use getReference() when you know entity exists.

---

### Q25: How do you monitor database performance?
**Answer:**
- Enable SQL logging (show_sql)
- Use Hibernate statistics
- Monitor connection pool metrics
- Analyze slow query logs
- Use Explain plan for queries