# Security: Connection Pooling

## Credential Management
```java
// BAD: hardcoded credentials
HikariConfig config = new HikariConfig();
config.setUsername("sa");
config.setPassword("password123");

// GOOD: externalized
config.setUsername(System.getenv("DB_USERNAME"));
config.setPassword(System.getenv("DB_PASSWORD"));

// BETTER: vault-backed
config.setUsername(vaultService.getSecret("db/username"));
config.setPassword(vaultService.getSecret("db/password"));
```

## Connection Encryption
```properties
# PostgreSQL with SSL
spring.datasource.url=jdbc:postgresql://host:5432/db?ssl=true&sslmode=verify-full
spring.datasource.hikari.connection-init-sql=SET enable_seqscan=off
```

## Least Privilege
- Application connections should have minimal required permissions
- Use separate DB users for different services
- `GRANT SELECT, INSERT, UPDATE, DELETE` – not `ALL PRIVILEGES`

## Connection Validation
```properties
spring.datasource.hikari.connection-test-query=SELECT 1
# Prevents serving compromised/closed connections
```

## Firewall & Network Security
- Database should only accept connections from application servers' IPs
- Use private subnets/VPCs for database traffic
- Never expose database ports (5432, 3306) to the public internet
