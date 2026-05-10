# Micronaut Solution

## Overview
This module covers Micronaut AOT (Ahead-of-Time) compilation.

## Key Features

### Application Lifecycle
- Run application
- Startup events
- Shutdown events

### REST Controllers
- @Controller
- @Get, @Post
- Path variables

### Configuration
- @Property
- Application properties

### Dependency Injection
- jakarta.inject
- Services

## Usage

```java
MicronautSolution solution = new MicronautSolution();

// Run application
solution.runApplication();

// Endpoints
// GET /hello -> "Hello from Micronaut"
// GET /greet/{name} -> "Hello, {name}!"
```

## Dependencies
- Micronaut Core
- Micronaut HTTP Server
- Micronaut HTTP Client
- JUnit 5