# Connection Pooling (HikariCP)

## Overview
Connection pooling maintains a cache of database connections that can be reused across requests. Creating a new TCP connection for every database operation is expensive (2-5ms handshake + SSL + auth). Connection pools eliminate this overhead by reusing established connections.

## Key Concepts
- **HikariCP**: Default connection pool in Spring Boot, high-performance, lightweight
- **Pool Sizing**: Maximum/minimum connections in the pool
- **Connection Timeout**: Max wait time for a connection from the pool
- **Idle Timeout**: Time before an idle connection is removed
- **Max Lifetime**: Maximum lifetime of a connection in the pool
- **Leak Detection**: Track connections not returned to the pool

## Java Example
```java
// HikariCP Programmatic Configuration
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
config.setUsername("user");
config.setPassword("password");
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(30000);      // 30s
config.setIdleTimeout(600000);           // 10min
config.setMaxLifetime(1800000);          // 30min
config.setConnectionTestQuery("SELECT 1");

HikariDataSource dataSource = new HikariDataSource(config);
```
