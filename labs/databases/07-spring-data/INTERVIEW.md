# Interview: Spring Data JPA

## Common Questions

**Q:** Explain how Spring Data JPA creates repository implementations.
**A:** Uses JDK dynamic proxies. `JpaRepositoryFactory` creates a proxy that delegates to `SimpleJpaRepository`. Custom method names are parsed into JPQL at startup and cached.

**Q:** How do you handle the N+1 problem in Spring Data JPA?
**A:** Use `@EntityGraph(attributePaths = {...})`, `JOIN FETCH` in `@Query`, or `@BatchSize`. Also ensure proper fetch strategies in `@OneToMany`/`@ManyToOne` annotations.

**Q:** What's the difference between `findById` and `getOne` / `getReferenceById`?
**A:** `findById()` hits the database immediately; `getReferenceById()` returns a proxy (lazy loading). Accessing a property triggers the actual query.

**Q:** How do you implement soft deletes?
**A:** Add a `@Column boolean deleted` field, use `@Where(clause = "deleted = false")` on the entity, and override `delete()` to set the flag.

**Q:** Explain Spring Data JPA auditing.
**A:** `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy` with `AuditingEntityListener`. Requires `AuditorAware` bean to supply the current user.
