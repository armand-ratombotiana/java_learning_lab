# Exercises: Spring Data JPA

## Exercise 1: Basic Entity & Repository (Beginner)
**Goal:** Create a mapped entity and perform CRUD operations.
1. Create a `Product` entity with fields: `id` (Long, auto-generated), `name` (String, required), `price` (BigDecimal), and `stockQuantity` (Integer).
2. Create a `ProductRepository` extending `JpaRepository<Product, Long>`.
3. Write code to save a few products, find a product by ID, and delete one product.

## Exercise 2: Derived & Custom Queries (Intermediate)
**Goal:** Query the database using method names and JPQL.
1. Add derived query methods to `ProductRepository`:
   - Find products with a price less than a given amount.
   - Find products by name containing a given string (case insensitive).
2. Add a `@Query` method using JPQL:
   - Find products out of stock (`stockQuantity = 0`).
3. Add a native SQL query method:
   - Find the top 3 most expensive products.

## Exercise 3: Entity Relationships (Intermediate/Advanced)
**Goal:** Map a bidirectional One-to-Many relationship.
1. Create a `Category` entity with an `id` and `name`.
2. Map a `@OneToMany` relationship from `Category` to `Product`.
3. Map a `@ManyToOne` relationship from `Product` to `Category`.
4. Ensure you implement utility methods on `Category` (`addProduct` / `removeProduct`) to keep both sides of the relationship synchronized.

## Exercise 4: Pagination & Sorting (Advanced)
**Goal:** Handle large datasets using `Pageable`.
1. Use `PageRequest.of(page, size, Sort)` to fetch products from a specific category, sorted by price descending.
2. Ensure the repository method returns a `Page<Product>`.
3. Extract total elements, total pages, and the content from the returned `Page` object.

## Exercise 5: Transaction Management (Advanced)
**Goal:** Manage transaction boundaries and rollbacks.
1. Create a `ProductService` class.
2. Implement a method `@Transactional void processOrder(Long productId, int quantity)` that decreases the stock quantity.
3. Throw a runtime exception if the stock falls below zero, and verify that the database update is rolled back.

## Solutions
See `SpringDataJpaTraining.java` for implementation approaches and test cases covering these concepts.