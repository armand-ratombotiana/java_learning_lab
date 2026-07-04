# Security: R2DBC

## SQL Injection Prevention
R2DBC's parameter binding prevents SQL injection when used correctly:
```java
// SAFE - parameterized
client.sql("SELECT * FROM users WHERE email = :email")
    .bind("email", userInput)
    .fetch().all();

// SAFE - positional
client.sql("SELECT * FROM users WHERE email = $1")
    .bind("$1", userInput)
    .fetch().all();

// DANGEROUS - string concatenation
client.sql("SELECT * FROM users WHERE email = '" + userInput + "'")
    .fetch().all();
```

## Credential Management
- Store `spring.r2dbc.url`, username, password in environment variables or vault
- Never hardcode credentials in source code
- Use `spring.cloud.config` or Kubernetes secrets for credential injection

## Connection Encryption
```properties
# R2DBC Postgres SSL
spring.r2dbc.url=r2dbc:postgresql://host/db?sslMode=verify-full
spring.r2dbc.properties.sslRootCert=/path/to/ca.pem
```

## Reactive Context Security
Pass security context through reactive streams:
```java
return client.sql("SELECT * FROM users WHERE tenant_id = :tenant")
    .bind("tenant", ReactiveSecurityContextHolder.getContext()
        .map(ctx -> ctx.getAuthentication().getName()))
    .fetch().all();
```
