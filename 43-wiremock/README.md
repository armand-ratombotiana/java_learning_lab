# 43 - WireMock Learning Module

## Overview
WireMock is a tool for building mock HTTP services. This module covers contract testing and service mocking with WireMock.

## Module Structure
- `wiremock-service/` - WireMock implementation for testing

## Technology Stack
- Spring Boot 3.x
- WireMock library
- JUnit 5
- Maven

## Prerequisites
- JUnit 5 tests
- WireMock standalone (optional)

## Key Features
- HTTP request/response stubbing
- Recording and playback
- Fault injection (latency, errors)
- Stateful behavior simulation
- Standalone server mode
- REST API for configuration

## Build & Run Tests
```bash
cd wiremock-service
mvn clean test
```

## Testing Patterns
- Unit test with embedded WireMock
- Integration test with standalone server
- Contract testing between services

## Core Concepts
- Stub: Response configuration for request
- Mapping: URL + method + response
- Verification: Assert request was made
- State: Behavior based on call count

## Related Modules
- 42-testcontainers (integration testing)
- 50-junit5 (testing framework)