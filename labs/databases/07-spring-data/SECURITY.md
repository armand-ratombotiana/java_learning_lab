# Security: Spring Data JPA

## SQL Injection Protection
- **Derived queries** are safe (parameterized automatically)
- **@Query with native SQL** – use `?1` or `:name` placeholders, never string concatenation
```java
// SAFE
@Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
User findByEmail(@Param("email") String email);

// DANGEROUS
@Query(value = "SELECT * FROM users WHERE email = '" + email + "'", nativeQuery = true)
User findByEmailUnsafe(String email);
```

## Auditing for Security
- `@CreatedBy` / `@LastModifiedBy` for audit trails
- Use Spring Security's `AuditorAware` to populate current user

## Data Access Security
```java
// Repository-level security with @PreAuthorize
@PreAuthorize("hasRole('ADMIN') or #entity.owner == authentication.name")
@Override
<S extends T> S save(S entity);
```

## Sensitive Data
- Never log entities with sensitive fields (use `@ToString.Exclude`)
- Encrypt PII at the application level before persisting
- Use column-level `@ColumnTransformer` for transparent encryption
