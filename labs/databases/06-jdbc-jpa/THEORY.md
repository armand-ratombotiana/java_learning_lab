# Theory: JDBC & JPA

## JDBC Architecture
```
Java Application
    ↓
JDBC API (java.sql, javax.sql)
    ↓
JDBC Driver Manager
    ↓
Database Driver (PostgreSQL, MySQL, Oracle, etc.)
    ↓
Database
```

## JDBC Core Interfaces
- **DriverManager**: Manages database drivers, creates connections
- **Connection**: Session with a database (auto-commit, transaction isolation)
- **Statement**: SQL execution (Statement, PreparedStatement, CallableStatement)
- **ResultSet**: Tabular results with cursor navigation
- **DatabaseMetaData**: Database schema information

## JPA Architecture
```
Java Application
    ↓
JPA API (jakarta.persistence)
    ↓
Persistence Provider (Hibernate, EclipseLink, OpenJPA)
    ↓
JDBC Driver
    ↓
Database
```

## Entity States
```
new (transient) → persist() → managed → remove() → removed
                      ↓
                 detach() → detached → merge() → managed
```

## Entity Manager Operations
- **persist()**: New entity becomes managed
- **find()**: Load by primary key
- **merge()**: Attach detached entity
- **remove()**: Schedule deletion
- **flush()**: Synchronize with database
- **refresh()**: Re-read from database

## Relationship Types
- `@OneToOne`: Single related entity
- `@OneToMany`: Collection of related entities
- `@ManyToOne`: Many entities reference one
- `@ManyToMany`: Many-to-many with junction table

## Fetch Strategies
- **EAGER**: Load immediately (join query or subsequent select)
- **LAZY**: Load on first access (proxy or collection wrapper)

## Locking
- **Optimistic**: `@Version` field, check on update
- **Pessimistic**: `LockModeType.PESSIMISTIC_WRITE` (SELECT FOR UPDATE)
