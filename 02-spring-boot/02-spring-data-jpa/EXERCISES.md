# Spring Data JPA - Exercises

## Lab Exercises

### Exercise 1: Run and Explore
```bash
mvn spring-boot:run
```
- Verify application starts successfully
- H2 in-memory database is auto-configured

### Exercise 2: Understand JPA Entities
- Identify entities in source code
- Note `@Entity`, `@Id`, `@GeneratedValue` annotations
- Understand relationship mappings

### Exercise 3: Spring Data Repositories
- Find repository interfaces extending `JpaRepository`
- Understand auto-generated CRUD methods
- Try `save()`, `findById()`, `delete()`

### Exercise 4: Query Methods
- Use Spring Data query derivation
- Write methods like `findByName(String name)`
- Use `@Query` for custom JPQL

### Exercise 5: Database Configuration
- Create `application.properties` with H2 settings:
  ```properties
  spring.datasource.url=jdbc:h2:mem:testdb
  spring.jpa.show-sql=true
  ```
- Enable H2 console: `spring.h2.console.enabled=true`

### Exercise 6: Add Relationships
- One-to-Many / Many-to-One relationships
- Cascade operations
- Fetch strategies (Eager vs Lazy)

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```