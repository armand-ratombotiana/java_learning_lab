# 09 - Testing

Modern testing approaches in Java. Covers TestContainers for integration testing with Docker (database testing, module containers, custom containers), and integration testing patterns (mock vs real dependencies, test lifecycle, best practices).

## Prerequisites

- Java 11+
- Maven 3.x
- Docker
- JUnit 5

## Key Concepts

- TestContainers: throwaway Docker containers for tests
- Database testing: PostgreSQLContainer, MySQLContainer, JDBC URL with automatic lifecycle
- Module containers: Kafka, RabbitMQ, MongoDB, Redis, LocalStack, ToxiProxy
- Custom containers with GenericContainer
- Lifecycle management: `@Container`, `@Testcontainers`, static vs instance-level
- Integration testing strategies and best practices

## Module Structure

- `01-testcontainers/` - TestContainers library and patterns
- `02-integration-testing/` - Integration testing approaches

## Learning Objectives

- Write integration tests with TestContainers
- Configure and manage container lifecycles in tests
- Test against real dependencies (databases, message brokers)

## Estimated Time

- 3-5 hours across submodules

## How to Build

```bash
cd 09-testing
mvn clean package
```

Run tests (requires Docker):

```bash
cd 01-testcontainers
mvn test
```
