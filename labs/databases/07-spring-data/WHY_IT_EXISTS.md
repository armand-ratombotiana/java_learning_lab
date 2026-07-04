# Why Spring Data JPA Exists

Spring Data JPA exists to eliminate the repetitive boilerplate of JPA data access code. Before Spring Data, developers wrote DAO implementations with `EntityManager` injections, `try/catch` blocks for `PersistenceException`, transaction demarcation, and manual pagination logic. Spring Data JPA automates all of this via repository interfaces and provides:

- Zero implementation code for standard CRUD operations
- Automatic query derivation from method names
- Consistent abstraction across different data stores (JPA, MongoDB, Redis, etc.)
- Integration with Spring's transaction management, resource handling, and exception translation
