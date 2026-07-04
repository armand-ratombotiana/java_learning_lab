# How Relational Databases Work

## Storage Layer
- **Pages**: Fixed-size blocks (typically 8KB or 16KB)
- **Heap files**: Unordered row storage
- **Index-organized tables**: Rows stored in B-tree order
- **WAL (Write-Ahead Log)**: Every change logged before data page write

## Query Processing Pipeline
```
SQL Text → Parser → Rewriter → Planner → Optimizer → Executor → Result
```

1. **Parser**: Tokenize and build AST
2. **Rewriter**: Expand views, apply rules
3. **Planner**: Generate possible query plans
4. **Optimizer**: Estimate costs (CBO), pick cheapest plan
5. **Executor**: Run plan, fetch rows from storage

## Transaction Management
- **MVCC** (Multi-Version Concurrency Control): Each transaction sees a snapshot
- **Locking**: Row-level, page-level, table-level locks
- **Deadlock detection**: Cycle detection, abort one transaction

## JDBC Driver Types
| Type | Description | Example |
|---|---|---|
| 1 | JDBC-ODBC bridge | Deprecated |
| 2 | Native API (part Java, part native) | Oracle OCI |
| 3 | Network protocol (all Java, middleware) | Postgres via pgpool |
| 4 | Direct-to-database (pure Java) | PostgreSQL, MySQL Connector/J |

## JPA / Hibernate Lifecycle
```
Transient → Managed → Detached → Removed
```
- **Transient**: new object, not tracked
- **Managed**: tracked by EntityManager, changes flushed automatically
- **Detached**: previously managed, session closed
- **Removed**: scheduled for deletion on flush
