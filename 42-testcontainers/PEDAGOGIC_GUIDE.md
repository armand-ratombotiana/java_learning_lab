# Pedagogic Guide - Testcontainers

## Learning Path

### Phase 1: Basic Setup
1. Docker installation and verification
2. Maven dependency configuration
3. First container test (PostgreSQL)
4. Container lifecycle understanding

### Phase 2: Database Testing
1. Container configuration options
2. Migrations on startup
3. Test isolation strategies
4. Performance considerations

### Phase 3: Advanced Containers
1. Generic containers for custom images
2. Environment variable configuration
3. File mapping and volume mounts
4. Port mapping for accessibility

### Phase 4: Integration Patterns
1. Multiple container setup
2. Docker Compose integration
3. Network configuration
4. Service dependency ordering

### Phase 5: Best Practices
1. Container reuse strategies
2. Initialization callbacks
3. Cleanup and resource management
4. CI/CD integration

## Container Lifecycle

| Phase | Description |
|-------|-------------|
| Start | Create and start container |
| Ready | Wait for health check |
| Test | Run tests against container |
| Stop | Dispose container |

## Interview Topics

| Topic | Question |
|-------|----------|
| Why Testcontainers? | Why not use embedded DBs? |
| Performance | How to speed up container startup? |
| Isolation | How to ensure test isolation? |
| CI/CD | How to run in CI without Docker? |
| Debugging | How to debug failing tests? |

## Configuration Options

| Setting | Purpose |
|---------|---------|
| `withExposedPorts` | Map container ports |
| `withEnv` | Set environment variables |
| `withClasspathResourceMapping` | Mount files |
| `waitingFor` | Readiness condition |

## Next Steps
- Explore Ryuk for automatic cleanup
- Learn about testcontainers-spock for Groovy
- Study cloud-native test approaches