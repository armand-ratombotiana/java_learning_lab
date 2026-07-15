# Spring Data JPA Internals

Welcome to the atomic mastery lab for **Spring Data JPA Internals**. This lab is part of the Spring Boot Internals module.

## 🧠 What You Will Master
- The magic behind dynamic repository interfaces.
- The role of `JpaRepositoryFactoryBean` and `RepositoryProxyPostProcessor`.
- Query derivation from method names (e.g., `findByEmail`).
- Customizing the Persistence Context and Transaction management.
- Performance implications of `n+1` queries and how Spring Data mitigates them.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The Repository abstraction and Proxy pattern.
2. [INTERNALS.md](./INTERNALS.md) - How Spring generates implementation classes at runtime.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Writing custom repository fragments and optimizing queries.