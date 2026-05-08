# 44 - JMeter Learning Module

## Overview
Apache JMeter is an open-source load testing tool for analyzing and measuring application performance.

## Module Structure
- `jmeter-performance/` - JMeter test plans and scripts

## Technology Stack
- Apache JMeter 5.x
- JMeter Maven Plugin
- Maven
- Java 17+

## Prerequisites
- JMeter installed or Maven plugin downloads
- Target application to test

## Key Features
- Load generation (thread groups)
- Various protocol support (HTTP, JDBC, JMS)
- Assertion and validation
- Distributed testing
- HTML reporting
- Extensibility with plugins

## Build & Run Tests
```bash
cd jmeter-performance
mvn clean verify  # Runs JMeter tests
# Or run manually: jmeter -n -t src/test/jmeter/test-plan.jmx
```

## Test Plan Elements
- Thread Group: Virtual users
- Samplers: HTTP, JDBC, etc.
- Listeners: Results collection
- Assertions: Response validation
- Timers: Think time simulation

## Related Modules
- 45-gatling (alternative load testing)
- 38-prometheus (metrics collection)