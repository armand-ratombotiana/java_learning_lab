# Architecture — Polymorphism

## Strategy Pattern

Define a family of algorithms, encapsulate each, make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

## Observer Pattern

Define a one-to-many dependency between objects. When one object changes state, all dependents are notified. Java's `Observer`/`Observable` (deprecated) or property change listeners.

## Command Pattern

Encapsulate a request as an object. Parameterize clients with queues, requests, and operations. Supports undoable operations.

## Visitor Pattern

Represent an operation to be performed on elements of an object structure. Allows adding new operations without changing the element classes.

## State Pattern

Allow an object to alter its behavior when its internal state changes. The object appears to change its class.

## Polymorphism as Architectural Glue

Polymorphism allows different parts of a system to communicate through stable interfaces while implementations evolve independently.

## Plugin Architecture

Define plugin interfaces, discover implementations via `ServiceLoader` or DI, invoke polymorphically. Foundation of extensible systems.
