# Helidon - MicroProfile Implementation

## Overview
Helidon is Oracle's open-source Java microservices framework supporting both MicroProfile and a reactive programming model.

## Key Features
- MicroProfile support (MP 1, 2, 3)
- Reactive WebClient
- Built-in health and metrics
- Configuration with YAML/properties
- Native image ready

## Project Structure
```
53-helidon/
  helidon-mp/
    src/main/java/com/learning/helidon/mp/HelidonLab.java
```

## Running
```bash
cd 53-helidon/helidon-mp
mvn compile exec:java
```

## Concepts Covered
- Routing and handlers
- Health endpoints
- Configuration
- Reactive programming

## Dependencies
- Helidon MP
- Helidon WebServer