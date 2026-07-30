# Interview Questions — Domain-Driven Design

## Q1: What is the difference between Entity and Value Object?
**A:** Entity has an identity that persists across changes (e.g., Person with ID). Value Object is immutable and defined by its attributes (e.g., Money(100, USD) — two Money objects with same values are equal).

## Q2: What is an Aggregate?
**A:** A cluster of domain objects treated as a unit. The aggregate root is the only entry point; external references point only to the root.

## Q3: How do bounded contexts relate to microservices?
**A:** Each bounded context maps well to a microservice boundary. They share a ubiquitous language and own their data model independently.

## Q4: What is a domain event and why is it useful?
**A:** A domain event captures something meaningful that happened in the domain. Useful for decoupling side effects, implementing sagas, and building audit logs.
