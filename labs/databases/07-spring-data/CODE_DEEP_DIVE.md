# Code Deep Dive: Spring Data JPA

## Custom Repository with Specifications

```java
public interface UserRepository extends JpaRepository<User, Long>,
                                        JpaSpecificationExecutor<User> {
}

// Usage with dynamic filters
public class UserService {
    private final UserRepository userRepository;

    public List<User> searchUsers(String name, Integer minAge, String city) {
        Specification<User> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (minAge != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("age"), minAge));
        }
        if (city != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("address").get("city"), city));
        }

        return userRepository.findAll(spec, Sort.by("name").ascending());
    }
}
```

## Auditing with Spring Data

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuditEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;
}

// Configuration
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(ctx -> ctx.getAuthentication().getName());
    }
}
```
