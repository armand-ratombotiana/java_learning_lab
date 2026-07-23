# Mock Interview: Spring Data (Lab 07)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does Spring Data JPA simplify database access?

**Candidate:** Spring Data JPA provides a repository abstraction that eliminates boilerplate:
- **`CrudRepository`:** Save, findById, findAll, count, delete
- **`JpaRepository`:** CRUD + batch operations + flush
- **`PagingAndSortingRepository`:** Pagination + sorting
- **Query methods:** `findByLastName(String name)` — automatically implements query from method name
- **`@Query`:** Custom JPQL or native SQL
- **`@Modifying`:** UPDATE/DELETE queries
- **Specifications:** Dynamic query building
- **Auditing:** `@CreatedDate`, `@LastModifiedDate` automatically populated

**Interviewer:** How do you define custom queries in Spring Data JPA?

**Candidate:** Three approaches:
1. **Derived query methods:** `List<User> findByEmailAndActiveTrue(String email)`
2. **`@Query` annotation:** `@Query("SELECT u FROM User u WHERE u.email = :email")`
3. **Criteria API:** For dynamic queries at runtime using `Specification<User>` and `JpaSpecificationExecutor`

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain Spring Data REST. How does it expose JPA repositories as REST endpoints?

**Candidate:** Spring Data REST auto-generates REST endpoints for Spring Data repositories. Add `spring-boot-starter-data-rest` dependency:
- `GET /users` — list users (with pagination)
- `POST /users` — create user
- `GET /users/{id}` — get user
- `PUT /users/{id}` — full update
- `PATCH /users/{id}` — partial update
- `DELETE /users/{id}` — delete user

Configuration via `application.yml`:
```yaml
spring.data.rest:
  base-path: /api
  default-page-size: 20
  max-page-size: 1000
```

For custom endpoints, add `@RepositoryRestController` or `@Projection` to control the response shape.

**Interviewer:** How does Spring Data handle auditing?

**Candidate:** Enable with `@EnableJpaAuditing`:
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```

`@CreatedBy` requires an `AuditorAware` bean implementation:
```java
@Bean
public AuditorAware<String> auditorAware() {
    return () -> Optional.ofNullable(SecurityContextHolder.getContext()
        .getAuthentication()?.getName());
}
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** You have an entity with 20 fields. Different API endpoints need different subsets of these fields for performance. How do you design this with Spring Data JPA?

**Candidate:** Use **DTO projections** and **Entity Graphs**:

**Interface-based projections:**
```java
public interface UserSummary {
    Long getId();
    String getName();
    String getEmail();
}

public interface UserWithOrders extends UserSummary {
    List<OrderSummary> getOrders();
}

// Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findByActiveTrue();
    
    @EntityGraph(attributePaths = {"orders", "profile"})
    Optional<UserWithOrders> findById(Long id);
}
```

**Class-based DTOs:**
```java
public record UserDTO(Long id, String name, String email) {}

@Query("SELECT new com.example.dto.UserDTO(u.id, u.name, u.email) FROM User u WHERE u.active = true")
List<UserDTO> findActiveUserDTOs();
```

**Performance impact:** Entity-based queries load all 20 columns + collections. Projections load only requested columns. For high-traffic endpoints, this can mean the difference between 10ms and 100ms.

---

## Interviewer Feedback

**Strengths:** Solid Spring Data knowledge, practical projection patterns, good auditing understanding  
**Areas to Improve:** Could discuss Spring Data JDBC as an alternative to JPA  
**Verdict:** Hire

---

*Databases Lab 07 MOCK_INTERVIEW.md*
