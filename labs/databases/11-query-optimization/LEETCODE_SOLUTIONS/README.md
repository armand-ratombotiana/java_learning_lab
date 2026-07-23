# LEETCODE_SOLUTIONS — Query Optimization

## SQL Performance Solutions

| LeetCode Problem | Optimization Technique | Impact |
|-----------------|----------------------|--------|
| 176 Second Highest | Index on salary DESC | Avoid full scan |
| 180 Consecutive Numbers | LEAD window function | No self-join required |
| 177 Nth Highest | DENSE_RANK over OFFSET | Better plan stability |
| 184 Dept Highest | Window + INDEX | Avoid subquery |
| 185 Top 3 Salaries | DENSE_RANK + composite index | Index on (deptId, salary) |
| 197 Rising Temperature | Index on recordDate | Range scan efficiency |

### Execution Plan Analysis
```sql
-- Check if LeetCode query uses index
EXPLAIN PLAN FOR
SELECT name FROM Employee WHERE salary > 2000;

-- Optimize: Add index
CREATE INDEX idx_emp_salary ON Employee(salary);
```

### Key Optimization Rules
1. Index columns in WHERE, JOIN, and ORDER BY
2. Prefer EXISTS over IN for subqueries
3. Use UNION ALL instead of UNION when duplicates allowed
4. Avoid functions on indexed columns in WHERE
5. Use bind variables (no literal values)
