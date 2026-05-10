# Vert.x Solution

## Overview
This module covers Vert.x event bus and reactive programming.

## Key Features

### Event Bus
- Publish
- Send
- Consumer registration

### HTTP Server
- Server creation
- Request handling
- Response sending

### Router
- Route creation
- Path parameters
- JSON responses

## Usage

```java
VertxSolution solution = VertxSolution.create();

// Event bus
solution.publish("address", "message");
solution.send("address", "message", result -> {});
solution.registerConsumer("address", msg -> {});

// HTTP server
HttpServer server = solution.createHttpServer();
Router router = solution.createRouter();
solution.route(router, "/hello", new JsonHandler());
```

## Dependencies
- Vert.x Core
- Vert.x Web
- JUnit 5