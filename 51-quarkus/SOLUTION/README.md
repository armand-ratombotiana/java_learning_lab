# Quarkus Solution

## Overview
This module covers Quarkus framework - Supersonic Subatomic Java.

## Key Features

### REST Endpoints
- JAX-RS resources
- Reactive endpoints
- Path parameters

### Dependency Injection
- CDI beans
- Inject annotations

### Reactive
- Mutiny
- Non-blocking I/O

### Configuration
- MicroProfile Config
- Application properties

## Usage

```java
QuarkusSolution solution = new QuarkusSolution();

// Run application
solution.runApplication();

// Check running status
boolean running = solution.isApplicationRunning();

// REST endpoints via resources
// GET /hello -> "Hello from Quarkus"
// GET /greet/{name} -> "Hello, {name}!"
// GET /reactive -> reactive Uni
```

## Dependencies
- Quarkus Core
- Quarkus REST (RESTEasy)
- Quarkus Arc (CDI)
- REST Assured