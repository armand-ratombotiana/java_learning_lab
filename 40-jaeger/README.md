# 40 - Jaeger Learning Module

## Overview
Jaeger is an open-source distributed tracing system. This module covers distributed tracing with OpenTelemetry and Jaeger.

## Module Structure
- `jaeger-tracing/` - OpenTelemetry tracing implementation

## Technology Stack
- Spring Boot 3.x
- OpenTelemetry SDK
- OpenTelemetry Java Agent
- Jaeger client
- Maven

## Prerequisites
- Jaeger UI running on `localhost:16686`
- Jaeger collector on `localhost:4317` (gRPC)

## Key Features
- Distributed context propagation
- Span hierarchy visualization
- Service dependency analysis
- Trace sampling strategies
- Performance bottleneck identification
- OpenTelemetry standard compliance

## Build & Run
```bash
cd jaeger-tracing
mvn clean install
mvn spring-boot:run
# Ensure Jaeger agent/collector is running
```

## Default Configuration
- Collector endpoint: `http://localhost:4317`
- Service name: `jaeger-tracing-service`

## Core Concepts
- Trace: End-to-end request flow
- Span: Individual operation within trace
- Parent-child relationships
- Baggage for cross-service context

## Related Modules
- 35-consul (service discovery)
- 38-prometheus (metrics)