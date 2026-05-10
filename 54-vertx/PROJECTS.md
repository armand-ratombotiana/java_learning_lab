# Vert.x - Projects

This document contains two complete projects demonstrating Vert.x event-driven framework: a mini-project for learning Verticles and event bus, and a real-world project implementing production-grade reactive services.

## Project 1: Vert.x Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Vert.x concepts including Verticles, event bus, and reactive streams.

### Project Structure

```
vertx-basics/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── vertx/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>vertx-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <vertx.version>4.4.6</vertx.version>
    </properties>
</project>
```

### VerticleExample.java

```java
package com.learning.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;

public class VerticleExample extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        
        server.requestHandler(request -> {
            request.response()
                .putHeader("Content-Type", "application/json")
                .end("{\"message\":\"Hello Vert.x!\"}");
        });
        
        server.listen(8080, result -> {
            if (result.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(result.cause());
            }
        });
    }
}
```

## Project 2: Production Vert.x Services

### EventBusExample.java

```java
package com.learning.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class EventBusExample extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        
        eb.consumer("orders", message -> {
            System.out.println("Received: " + message.body());
            message.reply("Processed");
        });
    }
}
```

### Build and Run

```bash
cd vertx-production

# Run
java -jar target/vertx-production.jar
```

## Summary

Vert.x provides reactive event-driven programming for building scalable microservices.