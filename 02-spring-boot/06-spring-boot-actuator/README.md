# Spring Boot Actuator

Production monitoring and management with Spring Boot Actuator.

## Overview

- Production-ready monitoring endpoints
- Health checks and metrics
- Application introspection
- Integration with monitoring tools

## Key Endpoints

| Endpoint | Description |
|----------|-------------|
| /actuator/health | Application health status |
| /actuator/info | Build information |
| /actuator/metrics | JVM and app metrics |
| /actuator/env | Environment properties |
| /actuator/beans | Spring beans |
| /actuator/threaddump | Thread information |

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-actuator
- spring-boot-starter-security (optional)

## Configuration

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

## Custom Health Indicators

Implement `HealthIndicator` interface for custom health checks.

## Version

Spring Boot 3.3.0