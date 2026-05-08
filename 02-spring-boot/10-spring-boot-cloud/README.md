# Spring Boot Cloud

Spring Cloud module for cloud-native applications.

## Overview

- Cloud-native patterns
- Service discovery
- Distributed configuration
- Circuit breaker patterns

## Key Concepts

- **Service Discovery** - Dynamic service registration
- **Config Server** - Centralized configuration
- **Circuit Breaker** - Fault tolerance
- **API Gateway** - Request routing

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-cloud-starter (varies by config)
- spring-boot-starter-web

## Cloud Patterns

| Pattern | Description |
|--------|-------------|
| Service Discovery | Dynamic service registration |
| Config Server | External config management |
| Circuit Breaker | Handle service failures |
| Load Balancing | Distribute requests |

## 12-Factor App

Cloud-native apps follow 12-factor methodology:
1. Single codebase
2. Explicit dependencies
3. Externalized config
4. Backing services as attached
5. Build/Release/Run separation
6. Stateless processes
7. Port binding
8. Concurrency
9. Disposability
10. Dev/Prod parity
11. Logs as event streams
12. Admin processes

## Cloud Platforms

Compatible with:
- Kubernetes
- Cloud Foundry
- AWS
- Azure
- GCP

## Version

Spring Boot 3.3.0