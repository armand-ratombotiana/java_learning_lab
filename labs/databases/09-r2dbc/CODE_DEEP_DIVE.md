# Code Deep Dive: R2DBC

## DatabaseClient Usage

```java
@Repository
public class UserRepository {
    private final DatabaseClient client;

    public UserRepository(ConnectionFactory cf) {
        this.client = DatabaseClient.create(cf);
    }

    public Mono<User> findById(Long id) {
        return client.sql("SELECT id, name, email FROM users WHERE id = :id")
            .bind("id", id)
            .fetch()
            .one()
            .map(this::toUser);
    }

    public Flux<User> findAll() {
        return client.sql("SELECT id, name, email FROM users")
            .fetch()
            .all()
            .map(this::toUser);
    }

    public Mono<Integer> createUser(String name, String email) {
        return client.sql("INSERT INTO users (name, email) VALUES ($1, $2)")
            .bind("$1", name)
            .bind("$2", email)
            .fetch()
            .rowsUpdated();
    }

    private User toUser(Map<String, Object> row) {
        return new User(
            ((Long) row.get("id")),
            ((String) row.get("name")),
            ((String) row.get("email"))
        );
    }
}
```

## Spring Data R2DBC Repository

```java
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Flux<User> findByName(String name);
    Flux<User> findByEmailEndingWith(String domain);
}

@Service
public class UserService {
    private final UserRepository repository;

    public Flux<User> searchUsers(String domain) {
        return repository.findByEmailEndingWith(domain)
            .filter(user -> !user.isBlocked());
    }

    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }
}
```
