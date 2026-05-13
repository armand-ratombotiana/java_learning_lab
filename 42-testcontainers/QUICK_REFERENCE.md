# 42 - Testcontainers Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Container Lifecycle | Automatic start/stop per test class |
| Docker Compose | Multi-container orchestration |
| Generic Container | Custom image wrapper |
| Reusable Container | Fixed port container sharing |

## Supported Databases

| Database | Container Class |
|----------|----------------|
| PostgreSQL | `PostgreSQLContainer` |
| MySQL | `MySQLContainer` |
| MariaDB | `MariaDBContainer` |
| MongoDB | `MongoDBContainer` |
| Oracle | `OracleContainer` |
| Redis | `RedisContainer` |

## Commands

```bash
# Run tests
cd testcontainers-framework
mvn clean test

# Test with specific database
mvn test -Ddatabase=postgresql

# With Docker Compose
docker-compose up -d
mvn test
```

## Common Patterns

```java
// Basic container setup
PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test");

@BeforeAll
static void startContainer() {
    postgres.start();
}

@AfterAll
static void stopContainer() {
    postgres.stop();
}

// Service container
KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");
kafka.start();

// Get connection info
String jdbcUrl = postgres.getJdbcUrl();
String host = postgres.getHost();
int port = postgres.getFirstMappedPort();
```

## Configuration

```properties
# testcontainers.properties
testcontainers.reuse.enable=true
docker.client.strategy=org.testcontainers.dockerclient.UnixSocketClientProviderStrategy