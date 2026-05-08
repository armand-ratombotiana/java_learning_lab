# Pedagogic Guide - Spring Actuator

## Learning Path

### Phase 1: Core Endpoints
1. Health endpoint and indicators
2. Metrics exposure
3. Info endpoint configuration
4. Environment introspection

### Phase 2: Custom Indicators
1. Implementing HealthIndicator
2. Composite health aggregation
3. Database health checks
4. External service checks

### Phase 3: Custom Endpoints
1. @Endpoint annotation
2. @ReadOperation, @WriteOperation
3. Parameter handling with @Selector
4. Technology-specific endpoints

### Phase 4: Security
1. Endpoint exposure configuration
2. Sensitive data masking
3. Role-based access
4. Custom security rules

### Phase 5: Production Integration
1. Prometheus endpoint integration
2. Health + load balancer integration
3. Custom metrics with Micrometer
4. Info contributor extensions

## Endpoint Summary

| Endpoint | Use Case |
|----------|----------|
| `/health` | Is app healthy? |
| `/metrics` | What are current metrics? |
| `/info` | What is the app? |
| `/env` | Show configuration |
| `/logfile` | Download logs |
| `/heapdump` | Analyze memory |

## Health Indicators

| Indicator | Check |
|-----------|-------|
| DataSourceHealthIndicator | Database connectivity |
| DiskSpaceHealthIndicator | Disk space |
| RabbitHealthIndicator | RabbitMQ connection |
| RedisHealthIndicator | Redis connection |
| MongoHealthIndicator | MongoDB connection |

## Interview Topics
- What makes a good health indicator?
- How to secure sensitive endpoints?
- Health vs. liveness vs. readiness probes
- How actuator integrates with Kubernetes

## Next Steps
- Explore Spring Boot Admin for centralized monitoring
- Learn about custom metrics with Micrometer
- Study actuator in Kubernetes deployments