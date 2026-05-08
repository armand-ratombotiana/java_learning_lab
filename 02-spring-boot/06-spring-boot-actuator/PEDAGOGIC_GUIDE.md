# Spring Boot Actuator - Pedagogic Guide

## Learning Path

### Phase 1: Actuator Basics (Day 1)
1. **What is Actuator?** - Production-ready monitoring
2. **Built-in Endpoints** - Health, Info, Metrics
3. **Endpoint Exposure** - HTTP or JMX
4. **Security Integration** - With Spring Security

### Phase 2: Monitoring (Day 2)
1. **Health Endpoint** - Application health status
2. **Health Indicators** - Custom health checks
3. **Metrics Endpoint** - JVM, HTTP metrics
4. **Info Endpoint** - Build, git info

### Phase 3: Customization (Day 3)
1. **Custom HealthIndicator** - Business health checks
2. **Custom Metrics** - Micrometer API
3. **Endpoint Configuration** - Exposure, base path
4. **Events** - ApplicationStartup

## Key Endpoints

| Endpoint | Description |
|----------|-------------|
| /actuator/health | Application health |
| /actuator/info | Build information |
| /actuator/metrics | JVM metrics |
| /actuator/env | Environment properties |
| /actuator/beans | Bean listing |
| /actuator/threaddump | Thread information |

## Configuration

```properties
# Expose these endpoints
management.endpoints.web.exposure.include=*

# Show details
management.endpoint.health.show-details=always

# Base path
management.endpoints.web.base-path=/actuator
```

## Custom HealthIndicator

```java
@Component
public class MyHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check resource availability
        if (isAvailable()) return Health.up().build();
        return Health.down().build();
    }
}
```

## Best Practices
- Always expose health endpoint
- Add custom health indicators
- Secure sensitive endpoints
- Use with monitoring tools (Prometheus, Grafana)