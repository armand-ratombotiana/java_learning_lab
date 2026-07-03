# Module 26: Spring Data JPA - Quizzes

---

## Q1: Derived Queries
How does Spring Data JPA implement a method like `findByEmailAndName(String email, String name)`?

A) You must write the implementation class yourself.
B) You must provide the exact SQL via the `@Query` annotation.
C) Spring Data parses the method name and automatically generates the corresponding JPQL query.
D) It uses Java Reflection to search the database.

**Answer**: C
**Explanation**: Spring Data JPA utilizes query derivation from method names. By following a specific naming convention (e.g., `findBy...`, `countBy...`), Spring parses the method signature and dynamically constructs the query proxy.

---

## Q2: Fetch Types
What is the default fetch strategy for a `@ManyToOne` relationship in JPA?

A) `FetchType.LAZY`
B) `FetchType.EAGER`
C) `FetchType.ASYNC`
D) There is no default; it must be specified.

**Answer**: B
**Explanation**: In the JPA specification, single-valued associations (`@OneToOne` and `@ManyToOne`) are fetched eagerly by default. It is highly recommended to explicitly set them to `FetchType.LAZY` to avoid N+1 and performance issues.

---

## Q3: Dirty Checking
When modifying an existing entity within a `@Transactional` service method, what is the required step to save those changes to the database?

A) Call `entityManager.flush()`.
B) Call `repository.save(entity)`.
C) Call `repository.update(entity)`.
D) Nothing; Hibernate will detect the changes via Dirty Checking and automatically commit the UPDATE.

**Answer**: D
**Explanation**: Managed entities inside a transaction boundary are tracked by the persistence context. Any state changes (Dirty Checking) are automatically flushed to the database as an UPDATE statement when the transaction completes.