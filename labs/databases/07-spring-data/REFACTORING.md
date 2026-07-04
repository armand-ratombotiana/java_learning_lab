# Refactoring: Spring Data JPA

## Plain JPA DAO → Spring Data Repository

### Before
```java
@Repository
public class UserDao {
    @PersistenceContext private EntityManager em;
    public User findById(Long id) { return em.find(User.class, id); }
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }
    public User save(User u) {
        if (u.getId() == null) { em.persist(u); return u; }
        return em.merge(u);
    }
    public void delete(User u) { em.remove(em.contains(u) ? u : em.merge(u)); }
}
```

### After
```java
public interface UserRepository extends JpaRepository<User, Long> {}
```

## Complex DAO Method → Specification
```java
// Before: Multiple query methods for different combinations
// After: Single findAll(Specification, Pageable)

public Page<User> search(SearchCriteria criteria, Pageable pageable) {
    Specification<User> spec = UserSpecifications.fromCriteria(criteria);
    return userRepository.findAll(spec, pageable);
}
```

## Native SQL → @Query
```java
@Query(value = "SELECT * FROM users WHERE last_login < :date",
       nativeQuery = true,
       countQuery = "SELECT COUNT(*) FROM users WHERE last_login < :date")
Page<User> findInactiveUsers(@Param("date") LocalDateTime date, Pageable pageable);
```
