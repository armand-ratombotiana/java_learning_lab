# 41 - Spring Actuator Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Actuator Endpoints | Production-ready monitoring and management endpoints |
| Health Indicator | Custom health status check components |
| Micrometer | Metrics collection facade |
| Info Endpoint | Application metadata exposure |

## Common Endpoints

| Endpoint | Path | Purpose |
|----------|------|---------|
| Health | `/actuator/health` | Application status |
| Info | `/actuator/info` | Build and git info |
| Metrics | `/actuator/metrics` | Performance metrics |
| Env | `/actuator/env` | Environment properties |
| Threaddump | `/actuator/threaddump` | Thread information |
| Heapdump | `/actuator/heapdump` | Heap dump file |
| Loggers | `/actuator/loggers` | Runtime log config |
| Refresh | `/actuator/refresh` | Reload config |
| ScheduledTasks | `/actuator/scheduledtasks` | Scheduled jobs |

## Commands

```bash
# Enable all endpoints
management.endpoints.web.exposure.include=*

# Enable specific endpoints
management.endpoints.web.exposure.include=health,info,metrics

# Custom endpoint path
management.endpoints.web.base-path=/manage

# Health indicators
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always

# Info endpoint git info
management.info.git.enabled=true

# Run the application
cd spring-actuator
mvn spring-boot:run

# Test health endpoint
curl http://localhost:8080/actuator/health

# Test metrics
curl http://localhost:8080/actuator/metrics
```

## Configuration

```properties
# application.properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
info.git.enabled=true