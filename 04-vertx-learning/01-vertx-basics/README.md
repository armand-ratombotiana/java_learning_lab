# Vert.x Basics

Introduction to Vert.x framework fundamentals and core concepts.

## Overview

This module covers the essential concepts of Vert.x including:
- Creating and managing Vert.x instances
- Understanding the event loop architecture
- Building basic HTTP servers

## Prerequisites

- Java 11+
- Maven
- Vert.x dependency

## Key Concepts

- **Vertx Instance**: The entry point for Vert.x applications
- **Event Loop**: Non-blocking I/O handling mechanism
- **Verticle**: The deployment unit in Vert.x

## Source Code

- `VertxLab.java` - Main demonstration class
- `VertxBasicsTraining.java` - Training exercises

## Building

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.vertx.VertxLab"
```

## Dependencies

- io.vertx:vertx-core