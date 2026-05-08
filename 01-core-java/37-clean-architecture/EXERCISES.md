# Clean Architecture Exercises

## Exercise 1: Layer Identification
Given a typical Spring Boot project, identify which classes belong to which layer (Domain, Application, Infrastructure, Presentation).

## Exercise 2: Extract Domain Layer
Take a service class with database calls and business logic, and separate into:
- Domain entity
- Repository interface (in domain)
- Use case (in application)
- Repository implementation (in infrastructure)

## Exercise 3: Create a Use Case
Implement a "Create Order" use case following clean architecture with:
- Input port (interface)
- Input data object
- Output port (interface)
- Use case implementation

## Exercise 4: Implement Dependency Inversion
Create repository interface in domain layer and implement in infrastructure layer, injected into use case.

## Exercise 5: Architecture Review
Review an existing project and identify violations of clean architecture (what depends on what).