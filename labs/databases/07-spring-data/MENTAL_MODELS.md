# Mental Models: Spring Data JPA

## The Repository as Collection Metaphor
Think of a `JpaRepository<User, Long>` as an in-memory `Collection<User>` backed by a database table. You `save()` (put), `findById()` (get), `findAll()` (iterate), `delete()` (remove).

## Proxy as Magic Implementation
Spring generates repository implementations at startup. The proxy decodes your method name, builds a JPQL query, executes it, and maps results – all without you writing the implementation.

## Specification as Predicate Builder
`Specification<T>` is like a `Predicate<T>` in Java streams – composable building blocks for WHERE clauses. Chain them with `and()` and `or()` to build dynamic queries.

## Query Derivation as DSL
Method names are a domain-specific language: `findByLastNameAndAgeBetweenOrderByAgeDesc` translates directly to `WHERE last_name = ? AND age BETWEEN ? ORDER BY age DESC`. The method signature IS the query definition.
