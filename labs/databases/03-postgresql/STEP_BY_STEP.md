# Step-by-Step: PostgreSQL

## Step 1: Install and Configure PostgreSQL
```bash
# Docker
docker run --name pg -e POSTGRES_PASSWORD=secret \
  -p 5432:5432 -d postgres:16

# Verify connection
psql -h localhost -U postgres -d postgres
```

## Step 2: Create Database and Schema
```sql
CREATE DATABASE myapp;
\c myapp

CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION app_user;
```

## Step 3: Create Tables with Data Types
```sql
CREATE TABLE app.users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    metadata JSONB DEFAULT '{}',
    preferences TEXT[] DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

## Step 4: Add Indexes
```sql
CREATE INDEX idx_users_email ON app.users (email);
CREATE INDEX idx_users_metadata ON app.users USING GIN (metadata);
CREATE UNIQUE INDEX idx_users_email_lower ON app.users (LOWER(email));
```

## Step 5: Connect with JDBC
```java
String url = "jdbc:postgresql://localhost:5432/myapp";
Properties props = new Properties();
props.setProperty("user", "app_user");
props.setProperty("password", "secret");
props.setProperty("ApplicationName", "myapp");
Connection conn = DriverManager.getConnection(url, props);
```

## Step 6: Configure HikariCP + Spring Boot
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: app_user
    password: secret
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 10000
      leak-detection-threshold: 60000
```

## Step 7: EXPLAIN and Analyze Queries
```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
SELECT * FROM app.users WHERE email = 'test@example.com';
```

## Step 8: Set Up Full-Text Search
```sql
ALTER TABLE app.products ADD COLUMN search_vector tsvector;
CREATE INDEX idx_products_search ON app.products USING GIN (search_vector);
```

## Step 9: Configure WAL Archiving for PITR
```conf
wal_level = replica
archive_mode = on
archive_command = 'copy %p /backups/%f'
```

## Step 10: Monitor with pg_stat_statements
```sql
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

SELECT query, calls, total_exec_time, rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;
```
