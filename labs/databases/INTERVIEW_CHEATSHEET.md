# Databases Academy — Interview Cheatsheet

<div align="center">

![Database](https://img.shields.io/badge/Database_Interview-CC2927?style=for-the-badge)
![Cheatsheet](https://img.shields.io/badge/Cheatsheet-4285F4?style=for-the-badge)

**Quick reference for database engineering interviews — SQL, normalization, indexing, optimization, ACID/BASE**

</div>

---

## SQL Commands Cheat Sheet

### DDL (Data Definition Language)
| Command | Purpose | Example |
|---------|---------|---------|
| CREATE | Create database objects | `CREATE TABLE users (id NUMBER PRIMARY KEY, name VARCHAR2(100));` |
| ALTER | Modify database objects | `ALTER TABLE users ADD email VARCHAR2(255);` |
| DROP | Remove database objects | `DROP TABLE users;` |
| TRUNCATE | Remove all rows (DDL) | `TRUNCATE TABLE users;` |
| RENAME | Rename an object | `RENAME users TO old_users;` |

### DML (Data Manipulation Language)
| Command | Purpose | Example |
|---------|---------|---------|
| SELECT | Retrieve data | `SELECT * FROM users WHERE id = 1;` |
| INSERT | Add new rows | `INSERT INTO users (id, name) VALUES (1, 'Alice');` |
| UPDATE | Modify existing rows | `UPDATE users SET name = 'Bob' WHERE id = 1;` |
| DELETE | Remove rows | `DELETE FROM users WHERE id = 1;` |
| MERGE | UPSERT (insert or update) | `MERGE INTO users USING ...` |

### DCL (Data Control Language)
| Command | Purpose | Example |
|---------|---------|---------|
| GRANT | Give privileges | `GRANT SELECT, INSERT ON users TO app_user;` |
| REVOKE | Remove privileges | `REVOKE DELETE ON users FROM app_user;` |

### TCL (Transaction Control Language)
| Command | Purpose | Example |
|---------|---------|---------|
| COMMIT | Save transaction | `COMMIT;` |
| ROLLBACK | Undo transaction | `ROLLBACK;` |
| SAVEPOINT | Set savepoint | `SAVEPOINT sp1;` |
| SET TRANSACTION | Configure transaction | `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;` |

### Common Oracle-Specific SQL
```sql
-- ROWNUM (classic Oracle pagination)
SELECT * FROM emp WHERE ROWNUM <= 10;

-- FETCH FIRST (12c+ pagination)
SELECT * FROM emp ORDER BY empno OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY;

-- Hierarchical query
SELECT * FROM emp START WITH mgr IS NULL CONNECT BY PRIOR empno = mgr;

-- MERGE (upsert)
MERGE INTO dept d USING (SELECT 50 AS deptno, 'IT' AS dname FROM DUAL) s
ON (d.deptno = s.deptno)
WHEN MATCHED THEN UPDATE SET d.dname = s.dname
WHEN NOT MATCHED THEN INSERT (deptno, dname) VALUES (s.deptno, s.dname);

-- PIVOT
SELECT * FROM (SELECT job, sal FROM emp)
PIVOT (AVG(sal) FOR job IN ('CLERK' AS clerk, 'MANAGER' AS mgr, 'ANALYST' AS analyst));
```

---

## Normalization Reference

### Normal Forms
| Normal Form | Rule | Violation Example |
|-------------|------|-------------------|
| 1NF | Atomic columns (no repeating groups) | Column with comma-separated values: `skills: "Java,Python,SQL"` |
| 2NF | 1NF + all non-key columns depend on whole PK | Table with composite PK, column depends on only part of PK |
| 3NF | 2NF + no transitive dependencies | `emp → dept_id → dept_name` (dept_name depends on dept_id, not emp) |
| BCNF | 3NF + every determinant is a candidate key | Overlapping candidate keys with functional dependencies |
| 4NF | BCNF + no multi-valued dependencies | Independent multi-valued facts in same table |
| 5NF | 4NF + join dependency | Table cannot be decomposed without loss |

### Denormalization Patterns
| Pattern | Description | When to Use |
|---------|-------------|-------------|
| Pre-joined tables | Store denormalized data for read performance | Reporting, analytical queries |
| Duplicated columns | Copy column across tables | Avoid expensive joins |
| Computed columns | Pre-calculate aggregations | Dashboards, summary data |
| Array/JSON columns | Store multi-valued data in single column | Schema-less requirements |

---

## Index Types

### Oracle Index Types
| Index Type | Description | Best For |
|------------|-------------|----------|
| B-Tree (default) | Balanced tree structure | Equality + range queries, high cardinality |
| Unique | Enforces uniqueness | Primary keys, unique constraints |
| Bitmap | Bitmap representation per distinct value | Low cardinality columns (gender, status) |
| Composite | Multi-column index | Queries filtering on multiple columns |
| Function-Based | Index on function result | `UPPER(ename)`, date truncations |
| Reverse Key | Reverses key bytes | Even distribution for sequence-based keys |
| Bitmap Join | Pre-joins bitmap indexes | Star schema, data warehouse |
| Domain | Application-specific index | Spatial, text, XML |
| Invisible | Not used by optimizer until made visible | Safe index testing |
| Compressed | Compresses duplicate key values | Composite indexes with leading column low cardinality |

### PostgreSQL Index Types
| Index Type | Description | Best For |
|------------|-------------|----------|
| B-tree (default) | Balanced tree | General purpose |
| Hash | Hash value lookup | Equality only |
| GiST | Generalized search tree | Full-text, geometric, range |
| GIN | Generalized inverted index | Array, JSON, full-text search |
| BRIN | Block range index | Large tables, correlated data (e.g., timestamps) |

### Index Selection Guidelines
| Query Pattern | Index Strategy |
|---------------|----------------|
| `WHERE col = :val` | Single-column index on col |
| `WHERE col1 = :v1 AND col2 = :v2` | Composite index (col1, col2) |
| `WHERE col1 = :v1 ORDER BY col2` | Composite index (col1, col2) |
| `WHERE col IN (...) ORDER BY col` | Index on col (IN list can be slow with many values) |
| `WHERE col LIKE 'prefix%'` | B-tree on col |
| `WHERE col LIKE '%suffix%'` | Full-text index (Oracle Text, PG GIN) |
| `WHERE UPPER(col) = :v` | Function-based index on `UPPER(col)` |

---

## Query Optimization Tips

### Execution Plan Signs of Trouble
| Operation | Problem | Fix |
|-----------|---------|-----|
| `TABLE ACCESS FULL` on large table | Missing index | Add index on filtered columns |
| `NESTED LOOPS` with large outer | Wrong join method | Use hash join hint or optimize statistics |
| `SORT (ORDER BY STOPKEY)` | Missing index for ORDER BY | Add composite index covering ORDER BY |
| `BUFFER SORT` | Hash join memory spill | Increase sort area or PGA |
| `FILTER` with subquery | Uncorrelated subquery | Convert to JOIN or CTE |

### Optimization Techniques
```sql
-- 1. Use specific columns instead of SELECT *
SELECT id, name FROM users;  -- VS SELECT * FROM users

-- 2. Use bind variables
SELECT * FROM emp WHERE deptno = :dept;  -- VS literal values

-- 3. Use EXISTS instead of IN (for large subqueries)
SELECT * FROM dept d WHERE EXISTS (
    SELECT 1 FROM emp e WHERE e.deptno = d.deptno
);

-- 4. Use UNION ALL instead of UNION (when duplicates are OK)
SELECT name FROM employees UNION ALL SELECT name FROM contractors;

-- 5. Avoid functions in WHERE clause on indexed columns
SELECT * FROM emp WHERE hiredate >= TRUNC(SYSDATE) - 30;
-- VS Bad: WHERE TRUNC(hiredate) >= TRUNC(SYSDATE) - 30

-- 6. Use analysis functions for window operations (avoid self-joins)
-- 7. Use materialized views for complex aggregations
-- 8. Use partitioning for large table management
```

---

## ACID vs BASE

### ACID (Relational Databases)
| Property | Description | Guarantee |
|----------|-------------|-----------|
| **A**tomicity | All or nothing execution | Transaction either fully completes or fully rolls back |
| **C**onsistency | Data remains valid after transaction | Integrity constraints, triggers, cascades |
| **I**solation | Concurrent transactions don't interfere | Read phenomena prevention (dirty read, non-repeatable, phantom) |
| **D**urability | Committed changes survive failures | Write-ahead log (redo log), checkpoints |

### Isolation Levels
| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------|-----------|---------------------|--------------|
| READ UNCOMMITTED | Possible | Possible | Possible |
| READ COMMITTED | Prevented | Possible | Possible |
| REPEATABLE READ | Prevented | Prevented | Possible |
| SERIALIZABLE | Prevented | Prevented | Prevented |

### BASE (NoSQL Databases)
| Property | Description |
|----------|-------------|
| **B**asically **A**vailable | System guarantees availability (CP/AP trade-off) |
| **S**oft State | State may change without input (eventual consistency) |
| **E**ventual Consistency | System becomes consistent over time (no writes) |

### When to Use ACID vs BASE
| Aspect | ACID | BASE |
|--------|------|------|
| Consistency | Strong | Eventual |
| Scalability | Vertical (scale-up) | Horizontal (scale-out) |
| Schema | Strict, enforced | Flexible |
| Use Case | Financial transactions, ERP | Social media, IoT, analytics |
| Examples | PostgreSQL, Oracle, SQL Server | Cassandra, MongoDB, DynamoDB |

---

## Replication Strategies

| Strategy | Mode | Pros | Cons | Data Loss on Failure |
|----------|------|------|------|---------------------|
| Synchronous | Primary sends to replica, waits for ack | Zero data loss | Higher write latency | No |
| Asynchronous | Primary sends, doesn't wait | Low latency, high throughput | Possible data loss | Yes |
| Semi-Synchronous | Primary waits for at least one replica ack | Balance of safety and performance | Higher than async latency | Minimal (one replica) |

### Replication Topologies
```
Single Leader (Master-Slave):
  [Primary] → [Replica 1], [Replica 2], [Replica 3]
  Writes go to primary, reads can go to any

Multi Leader (Active-Active):
  [Primary A] ↔ [Primary B] ↔ [Primary C]
  Writes to any, conflict resolution needed

Leaderless:
  [Node 1] — [Node 2]
      \         /
       [Node 3]
  W + R > N for consistency
```

### Database-Specific Replication
| Database | Synchronous | Asynchronous | Multi-Master |
|----------|-------------|--------------|--------------|
| Oracle | Data Guard (SYNC) | Data Guard (ASYNC, ARCH) | GoldenGate |
| PostgreSQL | Synchronous Replication | Streaming Replication | BDR, pglogical |
| MySQL | Group Replication | Asynch Replication | Clustrix, Galera |
| SQL Server | AG (Synchronous) | AG (Asynchronous) | Peer-to-Peer |
| Cassandra | — | Hinted Handoff | Native (all nodes) |
| DynamoDB | — | Global Tables | Multi-region writes |

---

<div align="center">

**"Know your fundamentals — data structures, algorithms, and system design principles apply to all databases."**

---

[Back to Top](#databases-academy--interview-cheatsheet)

</div>
