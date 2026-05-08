# CQRS Pattern

## Overview
CQRS (Command Query Responsibility Segregation) separates read and write operations into different models for independent scaling.

## Key Features
- Separate models for read/write
- Optimized read models
- Event-driven updates
- Scalability improvements
- eventual consistency

## Project Structure
```
66-cqrs/
  cqrs-framework/
    src/main/java/com/learning/cqrs/CQRSLab.java
```

## Running
```bash
cd 66-cqrs/cqrs-framework
mvn compile exec:java
```

## Concepts Covered
- Command model
- Query model
- Command/Query handlers
- Read/Write separation
- Event projection

## Implementation
- Command: Create, Update, Delete
- Query: Read operations