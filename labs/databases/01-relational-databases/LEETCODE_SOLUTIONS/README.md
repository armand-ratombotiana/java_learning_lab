# LEETCODE_SOLUTIONS — Relational Databases

## SQL Schema Design Examples

| Problem | Topic | Key Concepts |
|---------|-------|-------------|
| LeetCode 175 | Combine Two Tables | LEFT JOIN, foreign keys |
| LeetCode 181 | Employees Earning More | Self-JOIN, same table relationships |
| LeetCode 183 | Customers Who Never Order | LEFT JOIN + IS NULL pattern |
| LeetCode 197 | Rising Temperature | Self-JOIN, date arithmetic |
| LeetCode 601 | Human Traffic of Stadium | Consecutive rows, self-JOIN pattern |

### Schema Design Principle: Normalization
- Split EMP table: basic info + salary history + department
- Use foreign keys for relationships
- Apply 3NF for transactional systems

### LeetCode 181: Self-Join Pattern
```sql
SELECT e.name AS Employee
FROM Employee e
JOIN Employee m ON e.managerId = m.id
WHERE e.salary > m.salary;
```
