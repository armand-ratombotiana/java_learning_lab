# Pedagogic Guide: TestContainers

## Learning Path

### Phase 1: Foundation (Day 1)
- Understand Docker fundamentals
- Learn why TestContainers replaces handcrafted mocks
- Study container lifecycle

### Phase 2: Core Skills (Day 2-3)
- Configure database containers (PostgreSQL, MySQL)
- Write integration tests with real databases
- Master `@Container` and service connections

### Phase 3: Advanced (Day 4-5)
- Generic containers for any service
- Custom images and initialization
- Performance optimization

## Key Concepts

| Concept | Description |
|---------|-------------|
| `@Container` | JUnit 5 extension for lifecycle management |
| GenericContainer | Start any Docker image |
| ContainerDatabase | Database-specific helpers |
| Ryuk | Cleanup container for clean test runs |

## Prerequisites
- Docker Desktop installed
- Basic JUnit 5 knowledge
- Understanding of database connections

## Common Pitfalls
- Port conflicts on reuse
- Slow startup; use reuse mode
- Missing Docker daemon