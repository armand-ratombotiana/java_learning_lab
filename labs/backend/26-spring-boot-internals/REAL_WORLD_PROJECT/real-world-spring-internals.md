# Real-World Project: Enterprise Monitoring System

## Scenario

A financial services company needs a monitoring system that:
1. Starts up in under 3 seconds for rapid deployment to container orchestrators
2. Uses custom auto-configuration for environment-specific settings
3. Exposes detailed health checks for each microservice dependency
4. Supports both Prometheus and Datadog metrics backends

## Implementation Plan

### Phase 1: Startup Optimization
- Exclude unnecessary auto-configuration (batch, security, etc.)
- Use lazy initialization for non-critical beans
- Benchmark Tomcat vs Jetty vs Undertow startup

### Phase 2: Custom Auto-Configuration
- Create `monitoring-starter` with `@ConditionalOnProperty`
- Support multiple environments via profiles
- Register custom `EnvironmentPostProcessor` for default settings

### Phase 3: Health & Metrics
- Composite health indicator showing: DB, Redis, Kafka, external API
- Custom metrics for transaction volume and latency
- Prometheus + Datadog registration via Micrometer

### Phase 4: Custom Endpoints
- `@Endpoint` for feature flags
- `@Endpoint` for environment info
- `@Endpoint` for dependency dependency graph

## Deliverables

- Spring Boot starter JAR for monitoring
- Docker Compose with Prometheus + Grafana
- Custom FailureAnalyzer for common startup issues