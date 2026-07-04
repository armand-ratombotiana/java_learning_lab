# Hexagonal Architecture (Ports and Adapters)

## Overview
Hexagonal Architecture, also known as Ports and Adapters, structures an application so that it's loosely coupled and testable. The core domain is isolated from external concerns (databases, UI, messaging) through ports (interfaces) and adapters (implementations).

## Topics Covered
- Port and adapter pattern
- Dependency inversion principle
- Core domain isolation
- Driver and driven adapters
- Testing strategies for hexagonal architecture
- Adapter implementations (REST, JPA, Kafka)

## Java/Spring Stack
- Spring Boot for adapter implementation
- Spring Data JPA for repository adapters
- Spring Web for REST adapters
- Spring Cloud Stream for messaging adapters
- ArchUnit for architectural testing
- TestContainers for integration tests
