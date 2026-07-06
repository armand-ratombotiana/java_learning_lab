# Interview Questions

## Question 1: Core Concept
Q: Explain the core concept and why it matters in Java development.
A: This concept provides a structured approach to building Java applications. It improves maintainability, testability, and team collaboration. Every professional Java developer should understand it.

## Question 2: SOLID Principles
Q: How do the SOLID principles apply here?
A: SOLID principles guide effective design. Single Responsibility keeps components focused. Open-Closed enables extension. Liskov Substitution ensures correct inheritance. Interface Segregation prevents fat interfaces. Dependency Inversion keeps code loosely coupled.

## Question 3: Dependency Injection
Q: What are the benefits of dependency injection?
A: Benefits include easier testing (mock injection), loose coupling, configuration flexibility, and clearer dependencies.

## Question 4: Testing Strategy
Q: How does good design affect testing?
A: Well-designed components are naturally testable. Interfaces enable mocking, dependency injection allows isolation, and single responsibility means fewer test cases per class.

## Question 5: Performance Considerations
Q: What performance considerations apply?
A: Consider object allocation overhead, interface dispatch cost (minimal after JIT), proxy/reflection overhead (avoid in hot paths), and appropriate caching strategies.

## Question 6: Code Review
Q: What do you look for in code reviews?
A: Check for proper abstraction levels, appropriate coupling, interface segregation, dependency injection usage, error handling, test coverage, and naming conventions.

## Question 7: Migration
Q: How do you migrate a legacy codebase?
A: Identify seams for extraction, extract interfaces, introduce dependency injection, add tests, and refactor gradually while keeping tests green.

## Question 8: Best Practices
Q: What are your top Java best practices?
A: Follow SOLID, prefer composition over inheritance, program to interfaces, use dependency injection, write tests first, and keep methods small and focused.
