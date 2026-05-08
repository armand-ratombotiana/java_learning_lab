# Pedagogic Guide - Micronaut

## Learning Path

### Phase 1: Fundamentals
1. Create Micronaut project
2. Understand compile-time DI
3. Build controllers and services

### Phase 2: Intermediate
1. Micronaut Data repositories
2. Reactive programming basics
3. Configuration and property injection

### Phase 3: Advanced
1. GraalVM native image building
2. Service discovery
3. Micronaut Security

## Key Concepts

| Concept | Description |
|---------|-------------|
| @Controller | HTTP endpoint definition |
| @Inject | Dependency injection |
| @Get/@Post | HTTP method annotation |
| @Introspected | Generate metadata |

## Comparison with Spring
- Micronaut: Compile-time DI, less reflection, faster startup
- Spring: Runtime DI, more mature ecosystem

## Best Practices
- Use constructor injection
- Leverage built-in validation
- Test with EmbeddedServer