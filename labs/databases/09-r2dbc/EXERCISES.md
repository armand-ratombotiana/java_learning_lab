# Exercises: R2DBC

## Exercise 1 – Basic DatabaseClient
Create a `DatabaseClient`-based repository for a `book` table:
- `findById(Long id)` returning `Mono<Book>`
- `findAll()` returning `Flux<Book>`
- `save(Book book)` returning `Mono<Book>`
- `deleteById(Long id)` returning `Mono<Void>`

## Exercise 2 – Reactive Repository
Convert the above to use `ReactiveCrudRepository`:
- Implement custom query methods (`findByAuthor`, `findByYearBetween`)
- Test with Spring Data R2DBC + H2

## Exercise 3 – Transactions
- Write a service method that transfers money between accounts
- Use `@Transactional` for atomicity with reactive types
- Test rollback behavior when the transfer fails

## Exercise 4 – Backpressure
- Create a large dataset (10,000+ rows)
- Stream using `Flux` and observe memory consumption
- Apply `limitRate()` and compare memory usage

## Exercise 5 – Migration from JPA
Take an existing JPA entity and repository:
- Remove `@Entity`, `@Table`, JPA annotations
- Add Spring Data R2DBC annotations
- Update service layer to return reactive types
