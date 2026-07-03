# Module 37: Clean Architecture - Quizzes

---

## Q1: The Dependency Rule
What is the fundamental Dependency Rule in Clean Architecture?

A) Dependencies must point from the Domain to the Database.
B) Dependencies must point only inward, toward higher-level policies (the Domain).
C) Dependencies must point outward, toward external frameworks.
D) Dependencies should be circular to allow two-way communication.

**Answer**: B
**Explanation**: In Clean Architecture, inner layers (Entities and Use Cases) must not know anything about outer layers (Adapters, UI, DB). Dependencies always point inward.

---

## Q2: Separation of Entities
Why is it considered a bad practice in Clean Architecture to put `@Entity` (JPA) annotations on your core Domain classes?

A) Because JPA is too slow.
B) Because it violates the Dependency Rule by making the core Domain depend on a specific database framework.
C) Because Domain classes cannot have properties.
D) Because Java does not allow annotations on POJOs.

**Answer**: B
**Explanation**: The Domain should be completely framework-agnostic. Tying it to JPA/Hibernate means a change in the database technology could force changes in the core business rules, which violates the architecture.

---

## Q3: Layer Responsibilities
In which layer of Clean Architecture would an HTTP REST Controller reside?

A) Entities
B) Use Cases
C) Interface Adapters
D) Frameworks and Drivers

**Answer**: C
**Explanation**: Controllers act as Interface Adapters. They adapt the external web request (from the Frameworks layer) into a format convenient for the inner Use Case layer.