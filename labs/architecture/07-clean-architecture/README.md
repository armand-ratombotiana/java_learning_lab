# Clean Architecture

## Overview
Clean Architecture, introduced by Robert C. Martin, organizes code into concentric layers. The dependency rule states that source code dependencies can only point inward. The innermost layer contains enterprise business rules, while outer layers handle delivery mechanisms, persistence, and frameworks.

## Topics Covered
- Clean architecture layers
- Use cases and interactors
- Entities and business rules
- Interface adapters (controllers, presenters, gateways)
- Frameworks and drivers layer
- Dependency rule and dependency injection
- Screaming architecture

## Java/Spring Stack
- Spring Boot for framework layer
- Spring Data JPA for gateway implementations
- Spring Web for controller adapters
- MapStruct for entity/model mapping
- ArchUnit for architectural rules
- JUnit 5 with Mockito for testing
