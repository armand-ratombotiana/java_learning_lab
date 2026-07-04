# Hexagonal Architecture Flashcards

## Q: What is a port?
**A:** An interface that defines how external systems interact with the domain core.

## Q: What is an adapter?
**A:** An implementation of a port that translates between the domain and external technology.

## Q: What is the dependency rule?
**A:** Dependencies point inward toward the domain; adapters depend on ports, not vice versa.

## Q: What is a driving adapter?
**A:** An adapter that initiates calls into the domain (e.g., REST controller).

## Q: What is a driven adapter?
**A:** An adapter that is called by the domain to interact with external systems (e.g., repository).

## Q: What should be in the domain layer?
**A:** Pure business logic, entities, value objects, port interfaces, domain services.

## Q: Why hexagonal architecture?
**A:** To isolate domain logic from infrastructure concerns, improving testability and maintainability.

## Q: What is an in-memory adapter?
**A:** A test double that implements a port without real infrastructure.

## Q: What is a decorator adapter?
**A:** An adapter wrapping another adapter to add cross-cutting concerns like logging or caching.

## Q: What is ArchUnit?
**A:** A Java library for testing architecture rules, including hexagonal layering.
