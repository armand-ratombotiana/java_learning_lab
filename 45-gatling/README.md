# 45 - Gatling Learning Module

## Overview
Gatling is a load testing tool focused on HTTP and designed for high performance with scenarios written in Scala.

## Module Structure
- `gatling-load/` - Gatling simulation scripts

## Technology Stack
- Gatling 3.x
- Scala 2.13+
- Maven
- Java 17+

## Prerequisites
- JVM 17+
- Target application to test

## Key Features
- DSL for scenario definition
- High performance load generation
- WebSocket and SSE support
- HTTP/2 support
- Detailed HTML reports
- CI/CD integration

## Build & Run Tests
```bash
cd gatling-load
mvn gatling:test  # Run Gatling simulations
# HTML report generated in target/gatling
```

## Core Concepts
- Simulation: Load test configuration
- Scenario: User behavior workflow
- Feeder: Data injection
- Check: Response validation
- Chain: Sequential actions

## Related Modules
- 44-jmeter (comparison tool)
- 38-prometheus (metrics integration)