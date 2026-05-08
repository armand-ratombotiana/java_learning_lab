# Spring Boot Cloud - Pedagogic Guide

## Learning Path

### Phase 1: Cloud Native Basics (Day 1)
1. **Cloud Native Principles** - 12-Factor App
2. **Spring Cloud** - Building cloud-native apps
3. **Service Discovery** - Dynamic registration
4. **Configuration** - Externalized, centralized

### Phase 2: Spring Cloud Components (Day 2)
1. **Service Registry** - Eureka, Consul
2. **Config Server** - Centralized configuration
3. **Circuit Breaker** - Resilience4j
4. **API Gateway** - Spring Cloud Gateway

### Phase 3: Cloud Platforms (Day 3)
1. **Kubernetes** - Container orchestration
2. **Service Mesh** - Istio (advanced)
3. **Cloud Providers** - AWS, GCP, Azure
4. **Observability** - Distributed tracing

## Key Concepts

### 12-Factor App
1. Codebase - One repo, many deploys
2. Dependencies - Explicitly declared
3. Config - Environment-based
4. Backing Services - Treat as attached
5. Build, Release, Run - Strict separation
6. Processes - Stateless
7. Port Binding - Export HTTP
8. Concurrency - Scale via processes
9. Disposability - Fast startup, graceful shutdown
10. Dev/Prod Parity - Keep similar
11. Logs - Event streams
12. Admin Processes - One-off tasks

### Spring Cloud Features
- **Discovery** - Find services dynamically
- **Config** - Centralized external config
- **Resilience** - Circuit breaker, retry
- **Routing** - API Gateway
- **Observability** - Distributed tracing

## Best Practices
- Externalize all configuration
- Use service discovery
- Implement circuit breakers
- Keep services stateless
- Design for scaling