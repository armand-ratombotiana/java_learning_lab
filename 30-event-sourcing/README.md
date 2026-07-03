# 30 - Event Sourcing

Event sourcing pattern implementation with Spring Boot. Covers domain events (OrderCreatedEvent), event store as append-only log, event publishing via ApplicationEventPublisher, event listeners for side-effect processing, and REST API for event source interaction.

## Prerequisites

- Java 11+
- Maven 3.x
- Spring Boot 3.x

## Key Concepts

- Event sourcing: state changes as immutable event sequence
- Domain events: `OrderCreatedEvent` extending `ApplicationEvent`
- Event store: append-only log of all domain events
- Event publishing: `ApplicationEventPublisher.publishEvent()`
- Event listeners: `@EventListener` for processing events
- State reconstruction: replaying events to rebuild current state
- Command vs Event: commands express intent, events record facts
- Audit log: complete event history for debugging and compliance

## Module Structure

- `event-sourcing-learning/` - Spring Boot event sourcing application

## Learning Objectives

- Implement event sourcing with Spring Boot and Spring Events
- Design domain events and event store
- Build event-driven REST APIs with event processing

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 30-event-sourcing
mvn clean package
```

Run the Spring Boot application:

```bash
cd event-sourcing-learning
mvn spring-boot:run
```
