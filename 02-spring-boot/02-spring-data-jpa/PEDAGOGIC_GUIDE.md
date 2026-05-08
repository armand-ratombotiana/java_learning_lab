# Spring Data JPA - Pedagogic Guide

## Learning Path

### Phase 1: JPA Fundamentals (Day 1)
1. **Entity Mapping** - `@Entity`, `@Id`, `@Table`
2. **Primary Keys** - `@GeneratedValue`, strategies
3. **Column Mapping** - `@Column`, nullable, unique
4. **Basic CRUD** - persist, find, remove

### Phase 2: Spring Data Repositories (Day 2)
1. **JpaRepository** interface
2. **Auto-generated methods** - save, findById, delete
3. **Query Derivation** - method naming conventions
4. **Custom Queries** - `@Query` annotation

### Phase 3: Relationships (Day 3)
1. **@OneToOne** - Single reference
2. **@OneToMany / @ManyToOne** - Collections
3. **@ManyToMany** - Complex relationships
4. **Cascade Types** - PERSIST, REMOVE, MERGE

## Key Concepts

### Entity Lifecycle
- **Transient** - New, not managed
- **Persistent** - Managed by EntityManager
- **Detached** - Managed but not synced
- **Removed** - Marked for deletion

### Spring Data Query Derivation
```
findByName → SELECT ... WHERE name = ?
findByNameContaining → LIKE %?%
findByAgeGreaterThan → > ?
orderByNameDesc → ORDER BY name DESC
```

### Transaction Management
- `@Transactional` for methods
- `@Modifying` for UPDATE/DELETE
- Propagation levels

## Common Patterns

### Repository Pattern
```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByActiveTrue();
}
```

### Service Layer
```java
@Service
public class UserService {
    private final UserRepository userRepo;
    @Transactional
    public User save(User user) { return userRepo.save(user); }
}
```

## Best Practices
- Use constructor injection
- Keep transactions short
- Avoid lazy loading issues
- Use DTOs for projections