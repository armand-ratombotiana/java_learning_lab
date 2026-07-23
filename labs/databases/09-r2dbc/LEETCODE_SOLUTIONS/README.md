# LEETCODE_SOLUTIONS — R2DBC

## Reactive Database Access Solutions

| LeetCode Problem | R2DBC Approach | Reactive Type |
|-----------------|----------------|---------------|
| 175 Combine Tables | `Flux<Person>` + reactive join | `Flux.zip()` |
| 182 Duplicate Emails | Reactive GROUP BY | `Flux.groupBy()` |
| 585 Investments | Reactive subquery | Nested `Mono.from()` |

### Example: Reactive Repository
```java
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    @Query("SELECT e.name FROM Employee e JOIN Employee m " +
           "ON e.managerId = m.id WHERE e.salary > m.salary")
    Flux<String> findEmpEarningMoreThanManager();
}
```

### Key LeetCode Patterns with R2DBC
- Use `Mono` for single result, `Flux` for collections
- Reactive joins: fetch data then `flatMap()` for related queries
- Use `Flux.from(Flux.just(...))` for in-memory datasets
