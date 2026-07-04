# Exercises: Spring Data JPA

## Exercise 1 – Basic Repository
Create a `ProductRepository` extending `JpaRepository` with methods to:
- Find products by category
- Find products with price greater than a value
- Find products by name ignoring case
- Find top 10 most expensive products

## Exercise 2 – Query Methods
Build a `CustomerRepository` with:
- `findByEmail(String email)` – return `Optional`
- `findByRegistrationDateBetween(LocalDate start, LocalDate end)`
- `findByOrdersNotEmpty()` – customers who placed orders
- `countByActiveTrue()`

## Exercise 3 – Specifications
Implement dynamic filtering for an `Order` entity:
- Filter by status, date range, customer, minimum amount
- Compose specifications with `and()` / `or()`
- Add sorting and pagination

## Exercise 4 – Auditing
- Add `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy` to an entity
- Configure `AuditorAware` to get username from `SecurityContextHolder`
- Verify timestamps update correctly on save and update

## Exercise 5 – Performance Tuning
- Enable SQL logging and identify N+1 queries
- Add `@EntityGraph` to fix lazy loading issues
- Compare query counts before and after the fix
