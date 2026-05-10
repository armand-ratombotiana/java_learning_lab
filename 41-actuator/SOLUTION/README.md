# Actuator Solution

## Overview
This module covers Spring Boot Actuator endpoints, health checks, and metrics.

## Key Features

### Health Endpoints
- Built-in health indicators
- Custom health indicators
- Health aggregators

### Metrics
- System metrics
- Custom metrics
- Metric exporters

### Custom Endpoints
- @Endpoint annotation
- @ReadOperation
- @WriteOperation

## Usage

```java
ActuatorSolution solution = new ActuatorSolution();

// Create health status
Health health = solution.createHealthStatus("UP", Map.of("db", "ok"));

// Create custom health indicator
CustomHealthIndicator indicator = new CustomHealthIndicator();
Health status = indicator.health();

// Create custom endpoint
CustomEndpoint endpoint = new CustomEndpoint();
```

## Dependencies
- Spring Boot Actuator
- Spring Boot Web
- JUnit 5 for testing