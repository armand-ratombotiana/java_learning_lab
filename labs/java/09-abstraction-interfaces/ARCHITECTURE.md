# Architecture — Abstraction & Interfaces

## Hexagonal Architecture (Ports and Adapters)

Interfaces define "ports" (boundary contracts). Concrete classes implement "adapters" that connect to external systems. Core domain has no dependency on infrastructure.

## Service Interface Pattern

Define service interfaces for each bounded context. Implementations can be swapped, mocked, or proxied.

## Repository Pattern

Interface `UserRepository` with implementations `JdbcUserRepository`, `JpaUserRepository`, `MockUserRepository`. Domain layer depends on interface only.

## Dependency Inversion Principle

High-level modules should not depend on low-level modules — both should depend on abstractions. Interfaces invert the dependency direction.

## Adapter Pattern

Convert one interface to another clients expect. Wrap third-party libraries behind your own interface — easy to swap implementations.

## Facade Pattern

Provide a unified interface to a set of interfaces in a subsystem. Abstracts complexity behind a simple contract.

## API Contracts in Microservices

Interfaces define service contracts. Different services implement the same interface for polyglot environments.

## Default Methods for API Evolution

Add new capabilities to interfaces without breaking existing implementations. Critical for library and framework evolution.
