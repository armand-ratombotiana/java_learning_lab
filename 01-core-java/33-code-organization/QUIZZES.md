# Module 33: Code Organization - Quizzes

---

## Q1: Package Structures
What is the primary advantage of organizing code by "Feature" rather than by "Layer"?

A) It runs faster on the JVM.
B) It promotes high cohesion, placing related Controllers, Services, and Repositories for a specific business feature in the same package.
C) It allows for circular dependencies.
D) It prevents the use of public classes.

**Answer**: B
**Explanation**: Packaging by feature keeps all the code related to a specific domain concept together. This makes the codebase easier to navigate and significantly easier to split into microservices later, as boundaries are already well-defined.

---

## Q2: SOLID Principles
Which SOLID principle states that "Software entities should be open for extension but closed for modification"?

A) Single Responsibility Principle
B) Open/Closed Principle
C) Liskov Substitution Principle
D) Dependency Inversion Principle

**Answer**: B
**Explanation**: The Open/Closed Principle (OCP) encourages designing classes so that new functionality can be added (e.g., via inheritance or strategy patterns) without altering existing, tested code.

---

## Q3: Hexagonal Architecture
In Hexagonal Architecture, what is the role of an "Adapter"?

A) It defines the core business logic.
B) It manages the database schemas.
C) It translates data between the external world (e.g., HTTP requests or Database queries) and the core application "Ports".
D) It routes traffic between microservices.

**Answer**: C
**Explanation**: Adapters sit on the outside of the hexagon. A "Driving Adapter" (like a REST Controller) translates an HTTP request into a call to an application Port. A "Driven Adapter" (like a Repository) translates a Port interface call into an actual database query.