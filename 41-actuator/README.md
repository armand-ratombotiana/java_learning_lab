# 41 - Spring Actuator Learning Module

## Overview
Spring Boot Actuator provides production-ready features for monitoring and managing applications. This module covers health checks, metrics, and operational endpoints.

## Module Structure
- `spring-actuator/` - Actuator configuration and custom endpoints

## Technology Stack
- Spring Boot 3.x
- Spring Boot Actuator
- Micrometer
- Maven

## Prerequisites
- None (runs standalone)
- cURL or Postman for testing endpoints

## Key Features
- Health endpoint with indicators
- Metrics endpoint with Micrometer
- Info endpoint with git info
- Environment endpoint
- Logfile download
- Custom health indicators
- Scheduled tasks visibility

## Build & Run
```bash
cd spring-actuator
mvn clean install
mvn spring-boot:run
```

## Common Endpoints

| Endpoint | Path | Description |
|----------|------|-------------|
| Health | `/actuator/health` | Application status |
| Info | `/actuator/info` | Application info |
| Metrics | `/actuator/metrics` | Available metrics |
| Env | `/actuator/env` | Environment variables |
| Threaddump | `/actuator/threaddump` | Thread information |
| Heapdump | `/actuator/heapdump` | Heap dump file |

## Related Modules
- 38-prometheus (metrics export)
- 42-testcontainers (testing)