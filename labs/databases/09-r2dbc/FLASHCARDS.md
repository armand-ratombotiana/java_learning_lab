# Flashcards: R2DBC

**Q:** What is R2DBC?
**A:** Reactive Relational Database Connectivity – non-blocking API for relational databases

**Q:** Difference between JDBC and R2DBC?
**A:** JDBC blocks on database calls; R2DBC uses Reactive Streams with backpressure.

**Q:** What is `DatabaseClient`?
**A:** Fluent reactive API for executing SQL queries in R2DBC.

**Q:** What does `Mono<T>` represent in reactive programming?
**A:** A stream of 0 or 1 element (single value or empty).

**Q:** What does `Flux<T>` represent?
**A:** A stream of 0..N elements (multiple values).

**Q:** What is `ReactiveCrudRepository`?
**A:** Spring Data interface for reactive CRUD operations (returns `Mono`/`Flux`).

**Q:** How do you prevent unbounded memory consumption when consuming large results?
**A:** Use `.limitRate(N)` to apply backpressure in batches.

**Q:** Does R2DBC support transactions?
**A:** Yes, via `R2dbcTransactionManager` and `@Transactional`.
