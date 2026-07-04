# How PostgreSQL Works

## Architecture Overview

```
Client App → PostgreSQL Server
                ├── Parser
                ├── Rewriter
                ├── Planner/Optimizer
                ├── Executor
                ├── Storage Manager
                └── WAL (Write-Ahead Log)
```

## Process Model
- **Postmaster**: Parent process, listens for connections, forks children
- **Backend process**: One per client connection
- **Background workers**: Autovacuum, WAL writer, checkpointer, stats collector

## Memory Components
- **Shared Buffers**: Cache for data pages (typically 25% of RAM)
- **WAL Buffer**: Temporary storage for WAL records before flush
- **Work Mem**: Per-operation sort/hash memory
- **Maintenance Work Mem**: VACUUM, CREATE INDEX operations

## Storage Engine
- **Heap storage**: Rows stored in unordered heap pages using TIDs (page, offset)
- **TOAST**: Large values (>2KB) compressed and stored out-of-line
- **Index types**: B-tree, Hash, GiST, GIN, SP-GiST, BRIN

## JDBC Connection to PostgreSQL

```java
public class PgConnectionExample {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/mydb";
        Properties props = new Properties();
        props.setProperty("user", "app_user");
        props.setProperty("password", "secret");
        props.setProperty("ssl", "require");
        props.setProperty("ApplicationName", "MyApp");
        return DriverManager.getConnection(url, props);
    }
}
```

## JPA / Hibernate Dialect
```xml
<property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
<property name="hibernate.ddl-auto">validate</property>
```

## Key PostgreSQL-Specific Features
- **MVCC**: `xmin`/`xmax` tuple headers, snapshot isolation
- **VACUUM**: Dead tuple reclamation, free space map updates
- **Autovacuum**: Automated VACUUM with configurable thresholds
- **EXPLAIN**: Query plan visualization with `ANALYZE`, `BUFFERS`
