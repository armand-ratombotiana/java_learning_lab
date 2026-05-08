# Vert.x - Event-Driven Framework

## Overview
Vert.x is an event-driven, non-blocking JVM framework that enables building reactive applications with polyglot language support.

## Key Features
- Event-driven, non-blocking I/O
- Polyglot (Java, Kotlin, JS, Groovy)
- Verticle deployment model
- Event bus for messaging
- Web client and server

## Project Structure
```
54-vertx/
  vertx-web-client/
    src/main/java/com/learning/vertx/webclient/VertxWebClientLab.java
```

## Running
```bash
cd 54-vertx/vertx-web-client
mvn compile exec:java
```

## Concepts Covered
- Verticle concept
- Event loop and concurrency
- Routing with Router
- WebClient usage

## Dependencies
- Vert.x Core
- Vert.x Web