# Observability - WHY IT EXISTS

## Problem Statement
In a monolith, you can attach a debugger or read logs from one process. In a distributed system with 100+ services, a single user request may traverse 10+ services. Without observability, understanding what happened is impossible.

## Origin
Observability originated in control theory (1970s, Kalman filters) and was adapted to software. The three pillars (logging, metrics, tracing) were formalized in the 2010s as microservices adoption grew.

## Core Drivers
- **Debugging distributed systems**: Find the service causing a failure across a chain of calls
- **Performance optimization**: Identify bottlenecks across services
- **Capacity planning**: Predict when to scale based on trends
- **Incident response**: Rapidly detect, diagnose, and resolve issues
- **Business insights**: Understand user behavior from system data

## Why Not Just Monitor?
Monitoring tells you something is wrong. Observability lets you ask *why* it's wrong. Monitoring raises alerts. Observability enables open-ended debugging without predefined hypotheses.

## Java Ecosystem
- **Micrometer**: Metrics facade for JVM applications
- **Spring Boot Actuator**: Built-in production-ready features
- **OpenTelemetry**: Distributed tracing standard
- **Logback/Log4j2**: Structured logging
- **Prometheus + Grafana**: Metrics collection and visualization
- **ELK/Loki**: Log aggregation
