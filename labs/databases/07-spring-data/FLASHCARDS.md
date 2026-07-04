# Flashcards: Spring Data JPA

**Q:** What is the base interface for all Spring Data repositories?
**A:** `Repository<T, ID>`

**Q:** How does Spring Data derive queries from method names?
**A:** Parses `findBy`, `countBy`, `deleteBy` + property expressions + comparators

**Q:** How to define a custom JPQL query in a repository?
**A:** Annotate method with `@Query("JPQL or native SQL here")`

**Q:** What annotation is needed for `INSERT`/`UPDATE`/`DELETE` custom queries?
**A:** `@Modifying` (must also use `@Transactional`)

**Q:** What is `Specification<T>` used for?
**A:** Dynamic, type-safe, composable WHERE clause predicates

**Q:** How to enable auditing?
**A:** `@EnableJpaAuditing` + `@EntityListeners(AuditingEntityListener.class)`

**Q:** What does `@EntityGraph` do?
**A:** Defines eager fetch paths for a query to prevent N+1

**Q:** Difference between `Page<T>` and `Slice<T>`?
**A:** `Page` includes total count query; `Slice` only has next/previous flags
