# Consul Solution

## Concepts Covered

### Key-Value Store
- Put/get operations
- Flags and metadata
- Recursive deletion
- Watch for changes

### Service Registration
- Register/deregister services
- Health checks
- Tags and metadata

### Service Discovery
- Get healthy service instances
- Catalog queries
- DNS interface

### Health Monitoring
- Service health checks
- Node health checks
- Health aggregation

### Session Management
- Create/destroy sessions
- Session behaviors (delete, release)
- Node-based sessions

### Distributed Locking
- Acquire/release locks
- Session-based locking

### CAS Operations
- Compare-and-swap for atomic updates
- Optimistic locking

### Advanced Features
- ACL tokens
- Prepared queries
- Catalog operations
- Multi-datacenter support

## Dependencies

```xml
<dependency>
    <groupId>com.orbitz.consul</groupId>
    <artifactId>consul-client</artifactId>
    <version>0.17.2</version>
</dependency>
```

## Configuration

```yaml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        health-check-path: /health
        health-check-interval: 10s
```

## Running Tests

```bash
mvn test -Dtest=ConsulSolutionTest
```