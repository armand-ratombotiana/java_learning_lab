# 04 - Eclipse Vert.x Learning

Reactive toolkit for building polyglot applications on the JVM. Covers Vert.x core concepts, event bus messaging, HTTP servers, reactive extensions (RxJava), microservices patterns, database integration, WebSocket communication, and circuit breaker patterns.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Vert.x instance and event loop model
- Verticle deployment model
- Event bus for inter-verticle communication (point-to-point, pub/sub, request-reply)
- HTTP server with routing and request/response handling
- Reactive extensions with Vert.x Rx
- Microservices patterns with Vert.x
- Database integration (JDBC, MongoDB)
- WebSocket bidirectional communication
- Circuit breaker for fault tolerance

## Module Structure

- `01-vertx-basics/` - Vert.x core, event loop, verticles
- `02-event-bus/` - Event bus messaging patterns
- `02-vertx-rx/` - RxJava integration
- `03-http-server/` - HTTP server and routes
- `03-vertx-microservices/` - Microservices patterns
- `04-database-integration/` - Database connectivity
- `05-event-bus/` - Advanced event bus patterns
- `05-websockets/` - WebSocket server/client
- `06-circuit-breaker/` - Circuit breaker pattern

## Learning Objectives

- Build reactive applications with Vert.x
- Implement event-driven communication via the event bus
- Create HTTP APIs and real-time WebSocket endpoints
- Apply circuit breaker and fault-tolerance patterns

## Estimated Time

- 6-10 hours across all submodules

## How to Build

```bash
cd 04-vertx-learning
mvn clean package
```

Run individual submodules:

```bash
cd 01-vertx-basics
mvn compile exec:java -Dexec.mainClass="com.learning.vertx.VertxLab"
```
