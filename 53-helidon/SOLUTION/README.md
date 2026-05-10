# Helidon Solution

## Overview
This module covers Helidon framework - Oracle's Java microservices framework.

## Key Features

### Web Server
- Server creation
- Routing configuration
- Port binding

### Request Handling
- HTTP handlers
- Path parameters
- Response sending

### Health Checks
- Health endpoints
- Status reporting

## Usage

```java
HelidonSolution solution = new HelidonSolution();

// Create server
var routing = solution.createRouting();
WebServer server = solution.createServer(routing);

// Start server
solution.startServer(server);

// Routes
// GET /hello -> "Hello from Helidon"
// GET /greet/{name} -> "Hello, {name}!"
// GET /health -> health status
```

## Dependencies
- Helidon WebServer
- Helidon Common
- JUnit 5