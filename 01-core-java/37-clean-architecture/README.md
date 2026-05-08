# 37 - Clean Architecture

Software architecture approach that emphasizes separation of concerns and dependency direction, keeping business logic independent of external concerns.

## Overview

- **Topic**: Clean Architecture Implementation
- **Prerequisites**: Design patterns, SOLID principles, Java
- **Duration**: 3-4 hours

## Key Concepts

- Domain Layer (entities, business rules)
- Application Layer (use cases, ports)
- Infrastructure Layer (adapters, external services)
- Presentation Layer (controllers, APIs)
- Dependency rule (dependencies point inward)

## Getting Started

Run the training code:
```bash
cd 37-clean-architecture
mvn compile exec:java -Dexec.mainClass=com.learning.arch.CleanArchitectureTraining
```

## Module Contents

- Layer responsibilities and boundaries
- Dependency injection
- Use case implementation
- Repository patterns