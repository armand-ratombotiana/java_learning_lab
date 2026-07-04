# Refactoring: JDBC → R2DBC

## Synchronous Service → Reactive Service

### Before (JDBC)
```java
@Service
public class UserService {
    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
```

### After (R2DBC)
```java
@Service
public class UserService {
    private final ReactiveUserRepository repository;

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }
}
```

## JPA → Spring Data R2DBC

### Before (JPA)
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
}
public interface UserRepo extends JpaRepository<User, Long> {}
```

### After (R2DBC)
```java
import org.springframework.data.annotation.Id;

public class User {
    @Id
    private Long id;
    private String name;
}
public interface UserRepo extends ReactiveCrudRepository<User, Long> {}
```

**Key differences**: No `@Entity`/`@Table`, no lazy loading, no 1st-level cache, no auto-generated DDL. R2DBC is simpler but lower-level than JPA.
