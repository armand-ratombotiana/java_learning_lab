# LEETCODE_SOLUTIONS — JDBC / JPA

## Java Data Access Solutions

| LeetCode Problem | JPA Technique | Key Annotations |
|-----------------|---------------|-----------------|
| 175 Combine Two Tables | `@OneToMany` / `@ManyToOne` | `@JoinColumn` |
| 181 Emp > Manager | Self-referencing entity | `@ManyToOne` to self |
| 184 Dept Highest Salary | JPQL with subquery | `@Query` |
| 185 Top 3 Salaries | JPQL with window function | Native query for DENSE_RANK |

### JPA Repository Example
```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.salary > " +
           "(SELECT AVG(e2.salary) FROM Employee e2 WHERE e2.department = e.department)")
    List<Employee> findAboveAvgSalary();
}
```
