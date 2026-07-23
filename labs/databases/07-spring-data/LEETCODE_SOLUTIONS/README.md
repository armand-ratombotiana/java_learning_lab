# LEETCODE_SOLUTIONS — Spring Data

## Spring Data JPA Solutions

| LeetCode Problem | Spring Data Pattern | Method |
|------------------|---------------------|--------|
| 175 Combine Two Tables | `@OneToMany` + `LEFT JOIN FETCH` | `findAllWithAddresses()` |
| 176 Second Highest | Derived query + `Pageable` | `findTopByOrderBySalaryDesc(Pageable)` |
| 182 Duplicate Emails | `@Query` GROUP BY + HAVING | Custom JPQL |
| 197 Rising Temperature | `@Query` with date arithmetic | Custom query |

### Spring Data JPA Examples
```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.address")
    List<Person> findAllWithAddress();
    
    @Query("SELECT p.email FROM Person p GROUP BY p.email HAVING COUNT(p) > 1")
    List<String> findDuplicateEmails();
}
```
