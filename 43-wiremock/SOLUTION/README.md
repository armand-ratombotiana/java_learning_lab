# WireMock Solution

## Overview
This module covers HTTP mocking with WireMock.

## Key Features

### Request Stubbing
- GET/POST/PUT/DELETE stubs
- Request body matching
- Header matching

### Response Configuration
- Status codes
- Response bodies
- Delays and timeouts

### State Management
- Scenario-based stubs
- State transitions
- Sequence handling

## Usage

```java
WireMockServer server = new WireMockServer(8080);
server.start();
WireMockSolution solution = new WireMockSolution(server);

// Stub GET
solution.stubGetRequest("/api/test", 200, "{\"status\":\"ok\"}");

// Stub POST with body
solution.stubPostRequest("/api/create", "{\"name\":\"test\"}", 201, "{\"id\":1}");

// Stub with headers
solution.stubWithHeaders("/api/header", Map.of("Content-Type", "application/json"), "ok");

// Stub with delay
solution.stubWithDelay("/api/slow", 500, "delayed");
```

## Dependencies
- WireMock
- REST Assured
- JUnit 5